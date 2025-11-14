package com.electroblob.wizardry.api.content.spell.properties;

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * A class representing a property of a spell, such as its cost or cooldown. Each property has a type (e.g. Integer,
 * Float, Boolean), a default value, and a current value, these properties are identified by a unique string.
 * <p>
 * Properties can be created using the static factory methods provided, such as {@link #intProperty(String)} or
 * {@link #booleanProperty(String, boolean)}. Once created, the property's value can be accessed and modified using
 * the {@link #get()} and {@link #set(Object)} methods respectively.
 * (Check {@link com.electroblob.wizardry.content.spell.DefaultProperties} for examples of predefined properties.)
 * <p>
 *
 * @param <T> The type of the property's value.
 * @see com.electroblob.wizardry.content.spell.DefaultProperties
 * @see SpellProperties#builder()
 */
@SuppressWarnings("unused")
public class SpellProperty<T> {
    private static final Set<SpellProperty<?>> PROPERTIES = new HashSet<>();
    protected String identifier = null;
    protected T value = null;
    protected T defaultValue = null;
    protected IPropertyType<T> type = null;

    private SpellProperty() {
    }

    public static SpellProperty<Byte> byteProperty(String id) {
        return byteProperty(id, (byte) 0);
    }

    public static SpellProperty<Short> shortProperty(String id) {
        return shortProperty(id, (short) 0);
    }

    public static SpellProperty<Integer> intProperty(String id) {
        return intProperty(id, 0);
    }

    public static SpellProperty<Long> longProperty(String id) {
        return longProperty(id, 0L);
    }

    public static SpellProperty<Float> floatProperty(String id) {
        return floatProperty(id, 0f);
    }

    public static SpellProperty<Double> doubleProperty(String id) {
        return doubleProperty(id, 0d);
    }

    public static SpellProperty<Boolean> booleanProperty(String id) {
        return booleanProperty(id, false);
    }

    public static SpellProperty<String> stringProperty(String id) {
        return stringProperty(id, "");
    }

    public static SpellProperty<Byte> byteProperty(String id, byte value) {
        return createProperty(id, value, PropertyTypes.BYTE);
    }

    public static SpellProperty<Short> shortProperty(String id, short value) {
        return createProperty(id, value, PropertyTypes.SHORT);
    }

    public static SpellProperty<Integer> intProperty(String id, int value) {
        return createProperty(id, value, PropertyTypes.INT);
    }

    public static SpellProperty<Long> longProperty(String id, long value) {
        return createProperty(id, value, PropertyTypes.LONG);
    }

    public static SpellProperty<Float> floatProperty(String id, float value) {
        return createProperty(id, value, PropertyTypes.FLOAT);
    }

    public static SpellProperty<Double> doubleProperty(String id, double value) {
        return createProperty(id, value, PropertyTypes.DOUBLE);
    }

    public static SpellProperty<Boolean> booleanProperty(String id, boolean value) {
        return createProperty(id, value, PropertyTypes.BOOLEAN);
    }

    public static SpellProperty<String> stringProperty(String id, String value) {
        return createProperty(id, value, PropertyTypes.STRING);
    }

    protected static <T> SpellProperty<T> createProperty(String identifier, T defaultValue, IPropertyType<T> type) {
        SpellProperty<T> property = new SpellProperty<>();
        property.identifier = identifier;
        property.type = type;
        property.defaultValue = defaultValue;
        property.value = defaultValue;
        PROPERTIES.add(property);
        return property;
    }

    public static @Nullable SpellProperty<?> fromID(String identifier) {
        return PROPERTIES.stream().filter(p -> p.identifier.equals(identifier)).findFirst().orElse(null);
    }

    public T get() {
        return this.value;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void set(T value) {
        this.value = value;
    }

    SpellProperty<T> defaultValue(T value) {
        this.defaultValue = value;
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SpellProperty<?> property) {
            return property.identifier.equals(this.identifier) && property.type.equals(this.type);
        }
        return false;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public SpellProperty<T> copyOf() {
        SpellProperty<T> cloned = new SpellProperty<>();
        cloned.identifier = this.identifier;
        cloned.type = this.type;
        cloned.defaultValue = this.defaultValue;
        cloned.value = this.value;
        return cloned;
    }
}
