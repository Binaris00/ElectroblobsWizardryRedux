package com.binaris.wizardry.datagen.provider;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.properties.SpellProperty;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Base data provider for generating spell documentation in Markdown format.
 * This provider creates documentation files suitable for VitePress or other markdown-based documentation systems.
 */
public abstract class SpellDocsProvider implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackOutput output;
    private final String modId;
    private final String iconBasePath;
    private final List<Spell> spells = new ArrayList<>();
    private final Map<String, String> translations = new HashMap<>();

    /**
     * Creates a new spell documentation provider.
     *
     * @param output       The pack output
     * @param modId        The mod ID
     * @param iconBasePath The base path for spell icons in the documentation
     */
    protected SpellDocsProvider(PackOutput output, String modId, String iconBasePath) {
        this.output = output;
        this.modId = modId;
        this.iconBasePath = iconBasePath;
        loadTranslations();
    }

    /**
     * Implement this method to register spells for documentation generation.
     *
     * @param consumer The consumer to accept spells
     */
    protected abstract void buildSpells(@NotNull Consumer<Spell> consumer);

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        spells.clear();
        buildSpells(spells::add);

        List<Element> elements = Services.REGISTRY_UTIL.getElements().stream().toList();

        List<Spell> sortedSpells = spells.stream()
                .filter(spell -> spell != Spells.NONE)
                .filter(spell -> !spell.property(DefaultProperties.SENSIBLE))
                .sorted(Comparator.<Spell>comparingInt(s -> s.getTier().getLevel())
                .thenComparingInt(s -> elements.indexOf(s.getElement()))
                .thenComparing(s -> s.getLocation().toString())).toList();

        String spellsTableMarkdown = genSpellsTableFile(sortedSpells);

        // Generate Markdown documentation for each spell
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (Spell spell : sortedSpells) {
            Path outputPath = getSpellDocPath(spell);
            String markdown = genSpellFile(spell);
            futures.add(saveStringToFile(cachedOutput, markdown, outputPath));
        }

        // Save the spells.md table file
        Path spellsTablePath = output.getOutputFolder().resolve("docs/spells.md");
        futures.add(saveStringToFile(cachedOutput, spellsTableMarkdown, spellsTablePath));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    /**
     * Generates the whole Markdown content for a spell's documentation file, including the title, data information table,
     * description, and any additional properties. The formatting is designed to be compatible with VitePress or similar
     * markdown-based documentation systems.
     *
     * @param spell The spell to generate documentation for
     * @return A string containing the complete Markdown content for the spell's documentation
     */
    private String genSpellFile(Spell spell) {
        StringBuilder md = new StringBuilder();
        ResourceLocation id = spell.getLocation();

        String displayName = translate(spell.getDescriptionId());
        String description = translate(spell.getDescriptionId() + ".desc");

        md.append("# ").append(displayName).append("\n\n");

        String iconPath = iconBasePath + id.getPath() + ".png";

        md.append("## Data Information\n\n");
        md.append("| Property | Value |\n");
        md.append("|----------|-------|\n");
        md.append("| **Icon** | ![](").append(iconPath).append(") |\n");
        md.append("| **Element** | ").append(formatElement(spell.getElement())).append(" |\n");
        md.append("| **Type** | ").append(formatSpellType(spell.getType())).append(" |\n");
        md.append("| **Tier** | ").append(formatTier(spell.getTier())).append(" |\n");
        if (spell.isInstantCast()) md.append("| **Mana Cost** | ").append(spell.getCost()).append(" |\n");
        else md.append("| **Mana Cost** | ").append(spell.getCost()).append(" per tick |\n");
        md.append("| **Cooldown** | ").append(formatCooldown(spell.getCooldown())).append(" |\n");
        md.append("| **Charge Time** | ").append(spell.getChargeUp()).append(" ticks |\n");
        md.append("| **ID** | `").append(id).append("` |\n\n");

        // Extra properties (non-base properties)
        List<SpellProperty<?>> properties = spell.getProperties().getProperties();
        properties.removeIf(spellProperty -> spell.getProperties().isBaseProperty(spellProperty)); // Remove base properties to avoid duplication

        if (!properties.isEmpty()) {
            md.append("## Additional Properties\n\n");
            md.append("| Property | Value |\n");
            md.append("|----------|-------|\n");
            for (SpellProperty<?> prop : properties) {
                md.append("| ").append(formatPropertyName(prop)).append(" | ").append(prop.get()).append(" |\n");
            }
            md.append("\n");
        }

        // Description
        md.append("## Description\n\n");
        md.append(description).append("\n\n");

        return md.toString();
    }

    /**
     * Formats the property name for display, making it more human-readable, formatting with spaces and proper capitalization.
     * <p>
     * In case the property is a buff one from BuffSpell (e.g. "effect.minecraft.jump_boost_duration"), we split base on
     * '_' and put it like namespace:effect_name (e.g. "minecraft:jump_boost") + duration. This is to make it more readable
     * and consistent with how we display elements and tiers. This logic applies to '_strength".
     *
     * @param property The spell property to format
     * @return A formatted string representing the property name
     */
    private String formatPropertyName(SpellProperty<?> property) {
        String key = property.getIdentifier();
        if (key.contains("effect") && (key.endsWith("_duration") || key.endsWith("_strength"))) {
            int lastUnderscore = key.lastIndexOf('_');
            if (lastUnderscore != -1) {
                String base = key.substring(0, lastUnderscore);   // e.g. "effect.minecraft.jump_boost"
                String suffix = key.substring(lastUnderscore);     // e.g. "_duration"
                base = base.replace("effect.", "").replace(".", ":"); // e.g. "minecraft:jump_boost"
                suffix = suffix.replace("_", " ").trim(); // e.g. "duration"
                return "'%s' effect %s".formatted(base, suffix);
            }
        }

        key = key.replace("_", " ");
        key = Character.toUpperCase(key.charAt(0)) + key.substring(1); // start with uppercase
        return key;
    }


    /** Load en_us.json translations. We need this to get spell's display names and descriptions! */
    private void loadTranslations() {
        String langPath = "/assets/" + modId + "/lang/en_us.json";
        try (InputStream is = getClass().getResourceAsStream(langPath)) {
            if (is == null) {
                LOGGER.error("Warning: Could not find language file at {}", langPath);
                return;
            }
            JsonObject langJson = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            langJson.entrySet().forEach(entry ->
                    translations.put(entry.getKey(), entry.getValue().getAsString())
            );
            LOGGER.info("Loaded {} translations for {}", translations.size(), modId);
        } catch (IOException e) {
            LOGGER.error("Error loading translations: {}", e.getMessage());
        }
    }

    /**
     * Translates a key to its localized value. If the key is not found, returns the key itself.
     *
     * @param key The translation key
     * @return The translated string, or the key if not found
     */
    protected String translate(String key) {
        return translations.getOrDefault(key, key);
    }

    /**
     * Formats the element with appropriate styling. The CSS classes (e.g. "element-fire", "element-water") can be defined
     * in the documentation's CSS to apply different colors/styles based on element.
     *
     * @param element The spell element
     * @return A formatted string representing the element with appropriate styling
     */
    private String formatElement(Element element) {
        String displayName = translate(element.getDescriptionId());
        return "<span class=\"element-" + element.getName().toLowerCase() + "\">" +
                displayName + "</span>";
    }

    /**
     * Formats the spell type. It tries to translate the spell type's display name, and if the translation is missing,
     * it falls back to the unlocalized name.
     *
     * @param type The spell type
     * @return A formatted string representing the spell type
     */
    private String formatSpellType(SpellType type) {
        String key = type.getDisplayName();
        String translated = translate(key);
        return translated.equals(key) ? type.getName() : translated;
    }

    /**
     * Formats the tier with color coding. The CSS classes (e.g. "tier-basic", "tier-advanced") can be defined in the
     * documentation's CSS to apply different colors/styles based on tier.
     *
     * @param tier The spell tier
     * @return A formatted string representing the tier with appropriate styling
     */
    private String formatTier(SpellTier tier) {
        String displayName = translate(tier.getDescriptionId());
        return "<span class=\"tier-" + tier.getLocation().getPath().toLowerCase() + "\">" + displayName + "</span>";
    }

    /**
     * Formats the cooldown in a human-readable way. If cooldown is 0, returns "None". If less than 1 second, shows in
     * ticks. If whole seconds, shows as "X seconds". Otherwise, shows as "X.Y seconds".
     *
     * @param cooldownTicks The cooldown in ticks
     * @return A formatted string representing the cooldown
     */
    private String formatCooldown(int cooldownTicks) {
        if (cooldownTicks == 0) return "None";

        double seconds = cooldownTicks / 20.0;
        if (seconds < 1) {
            return cooldownTicks + " ticks";
        } else if (seconds % 1 == 0) {
            return String.format("%d seconds", (int) seconds);
        } else {
            return String.format("%.1f seconds", seconds);
        }
    }

    /**
     * Gets the output path for a spell's documentation file. The path is structured as "docs/spells/{namespace}/{path}.md"
     * to organize spells by their namespace and path.
     *
     * @param spell The spell to get the documentation path for
     * @return The Path where the spell's documentation file should be saved
     */
    protected Path getSpellDocPath(Spell spell) {
        ResourceLocation id = spell.getLocation();
        return output.getOutputFolder().resolve("docs/spells/" + id.getNamespace() + "/" + id.getPath() + ".md");
    }

    /**
     * Gets the relative link path for a spell's documentation. This is used in markdown links.
     * Override this method in subclasses to customize the spell link structure for addons.
     * By default, returns "spell/{spell_name}" (e.g., "spell/meteor").
     *
     * @param spell The spell to get the link path for
     * @return The relative link path to the spell's documentation (without .md extension)
     */
    protected String getSpellLinkPath(Spell spell) {
        return "spell/" + spell.getLocation().getPath();
    }

    /**
     * Generates a complete table of all spells in Markdown format. This creates a spells.md file with a table containing
     * all registered spells and their key properties.
     *
     * @param spellsList The list of spells to include in the table
     * @return A string containing the complete Markdown content for the spells table
     */
    private String genSpellsTableFile(List<Spell> spellsList) {
        StringBuilder md = new StringBuilder();

        md.append("# Spells\n\n");
        md.append("This is a complete list of all spells currently available.\n\n");

        md.append("| ID | Icon | Name | Element | Type | Tier |\n");
        md.append("|----|------|------|---------|------|------|\n");

        int id = 1;
        for (Spell spell : spellsList) {
            ResourceLocation location = spell.getLocation();

            String displayName = translate(spell.getDescriptionId());
            String iconPath = iconBasePath + location.getPath() + ".png";
            String spellLink = getSpellLinkPath(spell);

            md.append("| ").append(id++).append(" ");
            md.append("| ![](").append(iconPath).append(") ");
            md.append("| [").append(displayName).append("](").append(spellLink).append(") ");
            md.append("| ").append(formatElement(spell.getElement())).append(" ");
            md.append("| ").append(formatSpellType(spell.getType())).append(" ");
            md.append("| ").append(formatTier(spell.getTier())).append(" ");
            md.append("|\n");
        }

        md.append("\n");
        return md.toString();
    }

    /**
     * Helper method to save string content to a file. This method creates the necessary directories, converts the string
     * to bytes, and uses the CachedOutput to write the file.
     *
     * @param cache   The CachedOutput to use for writing the file
     * @param content The string content to save
     * @param path    The path where the file should be saved
     * @return A CompletableFuture representing the asynchronous file writing operation
     */
    @SuppressWarnings("deprecation")
    private CompletableFuture<?> saveStringToFile(CachedOutput cache, String content, Path path) {
        return CompletableFuture.runAsync(() -> {
            try {
                Files.createDirectories(path.getParent());
                byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
                cache.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
            } catch (IOException e) {
                throw new RuntimeException("Failed to save file: " + path, e);
            }
        });
    }

    @Override
    public @NotNull String getName() {
        return "Spell Documentation: " + modId;
    }
}

