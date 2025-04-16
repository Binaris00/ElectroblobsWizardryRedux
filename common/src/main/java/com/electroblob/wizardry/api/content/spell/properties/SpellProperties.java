package com.electroblob.wizardry.api.content.spell.properties;


import com.electroblob.wizardry.api.EBLogger;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

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
            if(prop.equals(property)) {
                EBLogger.debug("Getting Spell property... Id: %s Default: %s Value: %s", prop.identifier, prop.defaultValue, prop.value);
                return (T) prop.get();
            }
        }
        return property.getDefaultValue();
    }

    public static class Builder {
        private boolean isEmpty = true;
        private final List<SpellProperty<?>> builder = new ArrayList<>();

        public <T> Builder add(SpellProperty<T> property) {
            if(property != null){
                SpellProperty<T> cloned = property.copyOf();
                builder.add(cloned);

                if(isEmpty) isEmpty = false;
            }
            return this;
        }

        public <T> Builder add(SpellProperty<T> property, T defaultValue) {
            if(property != null){
                SpellProperty<T> cloned = property.copyOf();
                cloned.defaultValue(defaultValue);
                builder.add(cloned);
                if(isEmpty) isEmpty = false;
            }
            return this;
        }

        public SpellProperties build() {
            return new SpellProperties(new ArrayList<>(builder));
        }

        private Builder() {}
    }

}
