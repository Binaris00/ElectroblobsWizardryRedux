package com.electroblob.wizardry.api.common.spell;


import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class SpellProperties {

    private final LinkedHashSet<SpellProperty<?>> properties;
    private final boolean isEmpty;

    private SpellProperties(LinkedHashSet<SpellProperty<?>> properties, boolean isEmpty) {
        this.properties = properties;
        this.isEmpty = isEmpty;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public static SpellProperties empty() {
        return new SpellProperties(null, true);
    }

    public static SpellProperties.Builder builder() {
        return new SpellProperties.Builder();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(SpellProperty<T> property) {
        AtomicReference<T> value = new AtomicReference<>(property.getDefaultValue());
        this.properties.stream().filter(p -> p.identifier.equals(property.identifier)).findAny().ifPresent(
                p -> value.set((T) p.get())
        );
        return value.get();
    }

    @SuppressWarnings({"unchecked", "OptionalGetWithoutIsPresent"})
    <T> Optional<SpellProperty<T>> getProperty(SpellProperty<T> property) {
        return Optional.of((SpellProperty<T>) this.properties.stream().filter(p -> p.equals(property)).findAny().get());
    }

    public static class Builder {

        private boolean isEmpty = true;
        private final LinkedHashSet<SpellProperty<?>> builder = new LinkedHashSet<>();

        public <T> SpellProperties.Builder add(SpellProperty<T> property) {
            if(property != null) {
                this.builder.add(property);
                if (isEmpty) this.isEmpty = false;
            }
            return this;
        }

        public <T> SpellProperties.Builder add(SpellProperty<T> property, T defaultValue) {
            if(property != null) {
                this.builder.add(property.defaultValue(defaultValue));
                if (isEmpty) this.isEmpty = false;
            }
            return this;
        }

        public SpellProperties build() {
            return new SpellProperties(builder, builder.isEmpty());
        }

        private Builder() {}
    }

}
