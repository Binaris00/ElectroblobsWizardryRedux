package com.electroblob.wizardry.api.content.spell.properties;

import com.electroblob.wizardry.api.content.util.Util;
import com.google.gson.JsonElement;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class SpellProperty<T> {
    private static final Set<SpellProperty<?>> PROPERTIES = new HashSet<>();
    protected String identifier = null;
    protected T value = null;
    protected T defaultValue = null;
    protected PropertyType type = null;

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
        return createProperty((byte) 0, id, value, BYTE);
    }

    public static SpellProperty<Short> shortProperty(String id, short value) {
        return createProperty((short) 0, id, value, SHORT);
    }

    public static SpellProperty<Integer> intProperty(String id, int value) {
        return createProperty(0, id, value, INT);
    }

    public static SpellProperty<Long> longProperty(String id, long value) {
        return createProperty(0L, id, value, LONG);
    }

    public static SpellProperty<Float> floatProperty(String id, float value) {
        return createProperty(0F, id, value, FLOAT);
    }

    public static SpellProperty<Double> doubleProperty(String id, double value) {
        return createProperty(0D, id, value, DOUBLE);
    }

    public static SpellProperty<Boolean> booleanProperty(String id, boolean value) {
        return createProperty(false, id, value, BOOLEAN);
    }

    public static SpellProperty<String> stringProperty(String id, String value) {
        return createProperty("", id, value, STRING);
    }

    public static @Nullable SpellProperty<?> getPropertyFromIdentifier(String identifier) {
        return PROPERTIES.stream().filter(p -> p.identifier.equals(identifier)).findFirst().orElse(null);
    }

    protected static <T> SpellProperty<T> createProperty(T ignoredTypeIdentifier, String identifier, T defaultValue, PropertyType type) {
        SpellProperty<T> property = new SpellProperty<T>();
        property.identifier = identifier;
        property.type = type;
        property.defaultValue = defaultValue;
        property.value = defaultValue;
        PROPERTIES.add(property);
        return property;
    }

    protected SpellProperty<T> initProperty(T ignoredTypeIdentifier, String identifier, T defaultValue, PropertyType type) {
        this.identifier = identifier;
        this.type = type;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        return this;
    }

    public CompoundTag serializeOn(CompoundTag tag) {
        var wrapper = Util.wrapperTag(tag);
        if (type == BYTE)
            return wrapper.put(BYTE.id(), Util.compoundTagFrom(tag, BYTE.id(), t -> t.putByte(this.identifier, (Byte) this.value)));
        if (type == SHORT)
            return wrapper.put(SHORT.id(), Util.compoundTagFrom(tag, SHORT.id(), t -> t.putShort(this.identifier, (Short) this.value)));
        if (type == INT)
            return wrapper.put(INT.id(), Util.compoundTagFrom(tag, INT.id(), t -> t.putInt(this.identifier, (Integer) this.value)));
        if (type == LONG)
            return wrapper.put(LONG.id(), Util.compoundTagFrom(tag, LONG.id(), t -> t.putLong(this.identifier, (Long) this.value)));
        if (type == FLOAT)
            return wrapper.put(FLOAT.id(), Util.compoundTagFrom(tag, FLOAT.id(), t -> t.putFloat(this.identifier, (Float) this.value)));
        if (type == DOUBLE)
            return wrapper.put(DOUBLE.id(), Util.compoundTagFrom(tag, DOUBLE.id(), t -> t.putDouble(this.identifier, (Double) this.value)));
        if (type == BOOLEAN)
            return wrapper.put(BOOLEAN.id(), Util.compoundTagFrom(tag, BOOLEAN.id(), t -> t.putBoolean(this.identifier, (Boolean) this.value)));
        return tag;
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

    public static final PropertyType BYTE = PropertyType.addType("byte_", (t, s) -> deserializeNbt(s, t::getByte), (j, s) -> deserializeJson(j, s, t -> j.getAsByte()));
    public static final PropertyType SHORT = PropertyType.addType("short_", (t, s) -> deserializeNbt(s, t::getShort), (j, s) -> deserializeJson(j, s, t -> j.getAsShort()));
    public static final PropertyType INT = PropertyType.addType("int_", (t, s) -> deserializeNbt(s, t::getInt), (j, s) -> deserializeJson(j, s, t -> j.getAsInt()));
    public static final PropertyType LONG = PropertyType.addType("long_", (t, s) -> deserializeNbt(s, t::getLong), (j, s) -> deserializeJson(j, s, t -> j.getAsLong()));
    public static final PropertyType FLOAT = PropertyType.addType("float_", (t, s) -> deserializeNbt(s, t::getFloat), (j, s) -> deserializeJson(j, s, t -> j.getAsFloat()));
    public static final PropertyType DOUBLE = PropertyType.addType("double_", (t, s) -> deserializeNbt(s, t::getDouble), (j, s) -> deserializeJson(j, s, t -> j.getAsDouble()));
    public static final PropertyType BOOLEAN = PropertyType.addType("boolean_", (t, s) -> deserializeNbt(s, t::getBoolean), (j, s) -> deserializeJson(j, s, t -> j.getAsBoolean()));
    public static final PropertyType STRING = PropertyType.addType("string_", (t, s) -> deserializeNbt(s, t::getString), (j, s) -> deserializeJson(j, s, t -> j.getAsString()));


    private static <T> SpellProperty<T> deserializeNbt(String location, Function<String, T> getter) {
        SpellProperty<T> property = new SpellProperty<>();
        property.identifier = location;
        property.value = getter.apply(location);
        return property;
    }

    private static <T> SpellProperty<T> deserializeJson(JsonElement json, String location, Function<String, T> getter) {
        SpellProperty<T> property = new SpellProperty<>();
        property.identifier = location;
        property.value = getter.apply(location);
        return property;
    }

    public static void load() {
    }

    protected SpellProperty() {
    }
}
