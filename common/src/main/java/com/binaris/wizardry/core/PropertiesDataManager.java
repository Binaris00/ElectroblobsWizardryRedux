package com.binaris.wizardry.core;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.core.platform.Services;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * This class loads spell properties from JSON files located in the "data/modid/spells" directory of data packs.
 * Each JSON file should define properties for a specific spell, identified by its {@code ResourceLocation}.
 * The properties are then applied to the corresponding Spell instance in the registry.
 *
 * @see SpellProperties
 */
public class PropertiesDataManager extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = Deserializers.createFunctionSerializer().create();
    protected static final Logger LOGGER = LogManager.getLogger();
    public static PropertiesDataManager INSTANCE;

    public PropertiesDataManager() {
        super(GSON, "spells");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
            ResourceLocation location = entry.getKey();

            try {
                SpellProperties properties = SpellProperties.fromJson(GsonHelper.convertToJsonObject(entry.getValue(), location.toString()));
                if (properties == null) {
                    LOGGER.info("Skipping loading spell properties {} as it's serializer returned null", location);
                    continue;
                }
                Spell spell = Services.REGISTRY_UTIL.getSpell(location);
                if (spell == null) {
                    LOGGER.error("No spell found with ID {}, skipping loading of its properties", location);
                    continue;
                }
                spell.setProperties(properties);
            } catch (IllegalArgumentException | JsonParseException jsonParseException) {
                LOGGER.error("Parsing error loading spell properties {}", location, jsonParseException);
            }
        }
    }
}
