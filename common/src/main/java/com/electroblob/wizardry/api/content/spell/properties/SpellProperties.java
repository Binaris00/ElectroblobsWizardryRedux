package com.electroblob.wizardry.api.content.spell.properties;


import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpellProperties {
    private final List<SpellProperty<?>> properties;

    private SpellProperties(List<SpellProperty<?>> properties) {
        this.properties = properties;
    }

    public boolean isEmpty() {
        return this.properties.isEmpty();
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
                EBLogger.debug("Getting Spell property... Id: %s Default: %s Value: %s", prop.identifier, prop.defaultValue, prop.value);
                return (T) prop.get();
            }
        }
        return property.getDefaultValue();
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

    /**
     * This is used inside the data generator to serialize the spell properties to JSON. Normally you wouldn't need to
     * call this unless you're making an additional data generator that needs to output spell properties in a custom way
     */
    public JsonObject serializeToJson() {
        JsonObject json = new JsonObject();
        JsonObject baseProperties = new JsonObject();

        addProperty(json, DefaultProperties.TIER);
        addProperty(json, DefaultProperties.ELEMENT);
        addProperty(json, DefaultProperties.SPELL_TYPE);
        addProperty(json, DefaultProperties.COST);
        addProperty(json, DefaultProperties.COOLDOWN);
        addProperty(json, DefaultProperties.CHARGE);
        addProperty(json, DefaultProperties.SPELL_ACTION);

        properties.stream().filter(prop -> !isBaseProperty(prop)).forEach(prop -> addProperty(baseProperties, prop));

        if (baseProperties.size() > 0) {
            json.add("base_properties", baseProperties);
        }

        return json;
    }

    /**
     * Used to filter out the base properties so they can be added to the root of the JSON object instead of inside
     * "base_properties".
     *
     * @see SpellProperties#serializeToJson()
     */
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

    /**
     * Helper method to add a property to a JsonObject, handling different types appropriately.
     * Logs an error if the property is not found in the current properties list.
     *
     * @see SpellProperties#serializeToJson()
     */
    private <T> void addProperty(JsonObject parent, SpellProperty<T> property) {
        Optional<SpellProperty<?>> foundProp = this.properties.stream().filter(prop -> prop.identifier.equals(property.identifier)).findFirst();
        if (foundProp.isEmpty()) {
            EBLogger.error("Datagen - Spell property '%s' not found when serializing to JSON, skipping...", property.identifier);
            return;
        }

        SpellProperty<?> prop = foundProp.get();
        if (prop.value instanceof Boolean) parent.addProperty(prop.identifier, (Boolean) prop.value);
        else if (prop.value instanceof Number) parent.addProperty(prop.identifier, (Number) prop.value);
        else parent.addProperty(prop.identifier, prop.value.toString());

    }

    public static class Builder {
        private boolean isEmpty = true;
        private final List<SpellProperty<?>> builder = new ArrayList<>();

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

                if (isEmpty) isEmpty = false;
            }
            return this;
        }

        public <T> Builder add(SpellProperty<T> property, T defaultValue) {
            if (property != null) {
                SpellProperty<T> cloned = property.copyOf();
                cloned.defaultValue(defaultValue);
                builder.add(cloned);
                if (isEmpty) isEmpty = false;
            }
            return this;
        }

        public SpellProperties build() {
            return new SpellProperties(new ArrayList<>(builder));
        }

        private Builder() {
        }
    }

}
