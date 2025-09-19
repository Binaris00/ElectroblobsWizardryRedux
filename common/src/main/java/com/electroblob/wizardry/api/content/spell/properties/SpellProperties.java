package com.electroblob.wizardry.api.content.spell.properties;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.event.EBPlayerJoinServerEvent;
import com.electroblob.wizardry.api.content.event.EBServerLevelLoadEvent;
import com.electroblob.wizardry.api.content.spell.*;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.networking.s2c.SpellPropertiesSyncS2C;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpellProperties {
    private final List<SpellProperty<?>> properties;

    private SpellProperties(List<SpellProperty<?>> properties) {
        this.properties = properties;
    }

    public static SpellProperties empty() {
        return new SpellProperties(new ArrayList<>());
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(SpellProperty<T> property) {
        for (SpellProperty<?> prop : properties) {
            if (prop.equals(property)) {
                return (T) prop.get();
            }
        }
        return property.getDefaultValue();
    }

    public List<SpellProperty<?>> getProperties() {
        return properties;
    }

    // Spell Base properties helpers

    public int getCooldown() {
        return get(DefaultProperties.COOLDOWN);
    }

    public int getCost() {
        return get(DefaultProperties.COST);
    }

    public int getCharge() {
        return get(DefaultProperties.CHARGE);
    }

    public SpellType getType() {
        String type = get(DefaultProperties.SPELL_TYPE);
        return SpellType.fromName(type);
    }

    public SpellTier getTier() {
        String s = get(DefaultProperties.TIER);
        for (SpellTier tier : Services.REGISTRY_UTIL.getTiers()) {
            if (tier.getLocation().toString().equals(s)) return tier;
        }
        return SpellTiers.NOVICE; // Default
    }

    public Element getElement() {
        String s = get(DefaultProperties.ELEMENT);
        for (Element element : Services.REGISTRY_UTIL.getElements()) {
            if (element.getLocation().toString().equals(s)) {
                return element;
            }
        }
        return Elements.MAGIC; // Default
    }

    public SpellAction getAction() {
        String action = get(DefaultProperties.SPELL_ACTION);
        SpellAction spellAction = SpellAction.get(ResourceLocation.tryParse(action));
        return spellAction != null ? spellAction : SpellAction.NONE;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        JsonObject baseProps = new JsonObject();
        addProperty(json, DefaultProperties.TIER);
        addProperty(json, DefaultProperties.ELEMENT);
        addProperty(json, DefaultProperties.SPELL_TYPE);
        addProperty(json, DefaultProperties.COST);
        addProperty(json, DefaultProperties.COOLDOWN);
        addProperty(json, DefaultProperties.CHARGE);
        addProperty(json, DefaultProperties.SPELL_ACTION);
        properties.stream().filter(p -> !isBaseProperty(p)).forEach(p -> addProperty(baseProps, p));
        if (baseProps.size() > 0) json.add("base_properties", baseProps);
        return json;
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        CompoundTag baseProps = new CompoundTag();
        addProperty(tag, DefaultProperties.TIER);
        addProperty(tag, DefaultProperties.ELEMENT);
        addProperty(tag, DefaultProperties.SPELL_TYPE);
        addProperty(tag, DefaultProperties.COST);
        addProperty(tag, DefaultProperties.COOLDOWN);
        addProperty(tag, DefaultProperties.CHARGE);
        addProperty(tag, DefaultProperties.SPELL_ACTION);
        properties.stream().filter(p -> !isBaseProperty(p)).forEach(p -> addProperty(baseProps, p));
        if (!baseProps.isEmpty()) tag.put("base_properties", baseProps);
        return tag;
    }

    public static SpellProperties fromNbt(CompoundTag tag) {
        Builder builder = builder();
        for (String key : tag.getAllKeys()) {
            if (key.equals("base_properties")) continue;
            SpellProperty<?> temp = SpellProperty.fromID(key);
            if (temp == null || temp.type == null) continue;
            builder.add(temp.type.deserialize(tag, key));
        }

        if (tag.contains("base_properties")) {
            CompoundTag basePropsTag = tag.getCompound("base_properties");
            for (String key : basePropsTag.getAllKeys()) {
                SpellProperty<?> temp = SpellProperty.fromID(key);
                if (temp == null || temp.type == null) continue;
                builder.add(temp.type.deserialize(basePropsTag, key));
            }
        }
        return builder.build();
    }

    public static SpellProperties fromJson(JsonObject jsonObject) {
        Builder builder = builder();
        jsonObject.entrySet().forEach(entry -> {
            String id = entry.getKey();
            if (id.equals("base_properties")) return;
            SpellProperty<?> temp = SpellProperty.fromID(id);
            if (temp == null || temp.type == null) return;
            builder.add(temp.type.deserialize(entry.getValue(), id));
        });

        if (jsonObject.has("base_properties")) {
            JsonObject basePropsJson = jsonObject.getAsJsonObject("base_properties");
            basePropsJson.entrySet().forEach(entry -> {
                String id = entry.getKey();
                SpellProperty<?> temp = SpellProperty.fromID(id);
                if (temp == null || temp.type == null) return;
                builder.add(temp.type.deserialize(entry.getValue(), id));
            });
        }
        return builder.build();
    }

    private <T> void addProperty(CompoundTag parent, SpellProperty<T> referenceProperty) {
        SpellProperty<T> property = getProperties().stream().filter(p -> p.equals(referenceProperty))
                .map(p -> (SpellProperty<T>) p).findFirst().orElse(null);
        if (property == null) return;
        property.type.serialize(parent, property);
    }

    private <T> void addProperty(JsonObject parent, SpellProperty<T> referenceProperty) {
        SpellProperty<T> property = getProperties().stream().filter(p -> p.equals(referenceProperty))
                .map(p -> (SpellProperty<T>) p).findFirst().orElse(null);
        if (property == null) return;
        property.type.serialize(parent, property);
    }

    public static void onServerLevelLoad(EBServerLevelLoadEvent event){
        File dir = Paths.get("config", "ebwizardry", "spells").toFile();
        if (dir.mkdirs()) return;

        for (File file : FileUtils.listFiles(dir, new String[]{"json"}, true)) {
            String relative = dir.toPath().relativize(file.toPath()).toString();
            String nameAndModID = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
            String modID = nameAndModID.split("/")[0];
            String name = nameAndModID.substring(nameAndModID.indexOf('/') + 1);

            ResourceLocation key = new ResourceLocation("ebwizardry", name);
            Spell spell = Services.REGISTRY_UTIL.getSpell(key);

            if (spell == null) {
                EBLogger.info("Spell properties file %s .json does not match any registered spells; ensure the filename is spelled correctly., Relative: %s, modid: %s, name: %s"
                        .formatted(nameAndModID, relative, modID, name));
                continue;
            }

            BufferedReader reader = null;

            try {
                reader = Files.newBufferedReader(file.toPath());
                JsonObject json = GsonHelper.fromJson(EBConfig.GSON, reader, JsonObject.class);
                SpellProperties newProperties = SpellProperties.fromJson(json);
                spell.setProperties(newProperties);
                EBLogger.warn("loaded spell properties for {} with {} element" +
                        " and {} base properties from file {}.json", key, spell.getProperties().getElement().getName(),
                        spell.getProperties().getProperties().size(), nameAndModID);

            } catch (JsonParseException jsonparseexception) {
                EBLogger.error("Parsing error loading spell property file for " + key, jsonparseexception);
            } catch (IOException ioexception) {
                EBLogger.error("Couldn't read spell property file for " + key, ioexception);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
    }

    public static void onPlayerJoin(EBPlayerJoinServerEvent event){
        if(event.getPlayer().level().isClientSide) return;

        Map<ResourceLocation, SpellProperties> map = Services.REGISTRY_UTIL.getSpells().stream()
                .collect(java.util.stream.Collectors.toMap(Spell::getLocation, Spell::getProperties));

        Services.NETWORK_HELPER.sendTo((ServerPlayer) event.getPlayer(), new SpellPropertiesSyncS2C(map));
    }

    // I'm not proud of this method
    private boolean isBaseProperty(@NotNull SpellProperty<?> prop) {
        return prop.identifier.equals(DefaultProperties.TIER.identifier)
                || prop.identifier.equals(DefaultProperties.ELEMENT.identifier)
                || prop.identifier.equals(DefaultProperties.SPELL_TYPE.identifier)
                || prop.identifier.equals(DefaultProperties.COST.identifier)
                || prop.identifier.equals(DefaultProperties.COOLDOWN.identifier)
                || prop.identifier.equals(DefaultProperties.CHARGE.identifier)
                || prop.identifier.equals(DefaultProperties.SPELL_ACTION.identifier);
    }

    public static class Builder {
        private final List<SpellProperty<?>> builder = new ArrayList<>();

        private Builder() {
        }

        public Builder assignBaseProperties(SpellTier tier, Element element, SpellType type, SpellAction action, int cost, int charge, int cooldown) {
            add(DefaultProperties.TIER, tier.getLocation().toString());
            add(DefaultProperties.ELEMENT, element.getLocation().toString());
            add(DefaultProperties.SPELL_TYPE, type.getUnlocalisedName());
            add(DefaultProperties.SPELL_ACTION, action.location.toString());
            add(DefaultProperties.COST, cost);
            add(DefaultProperties.COOLDOWN, cooldown);
            add(DefaultProperties.CHARGE, charge);

            return this;
        }

        public <T> Builder add(SpellProperty<T> property) {
            if (property != null) {
                SpellProperty<T> cloned = property.copyOf();
                builder.add(cloned);
            }
            return this;
        }

        public <T> Builder add(SpellProperty<T> property, T defaultValue) {
            if (property != null) {
                SpellProperty<T> cloned = property.copyOf();
                cloned.defaultValue(defaultValue);
                builder.add(cloned);
            }
            return this;
        }

        public SpellProperties build() {
            return new SpellProperties(new ArrayList<>(builder));
        }
    }
}
