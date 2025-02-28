package com.electroblob.wizardry.api.common.spell;

import com.electroblob.wizardry.api.common.util.Util;
import net.minecraft.nbt.CompoundTag;

import java.util.*;
import java.util.function.BiFunction;

public class SpellProperty<T> {

    protected String identifier = null;
    protected T value = null;
    protected T defaultValue = null;
    protected Type type = null;

    public static SpellProperty<Byte> byteProperty(String identifier) {
        return byteProperty(identifier, (byte) 0);
    }

    public static SpellProperty<Short> shortProperty(String identifier) {
        return shortProperty(identifier, (short) 0);
    }

    public static SpellProperty<Integer> intProperty(String identifier) {
        return intProperty(identifier, 0);
    }

    public static SpellProperty<Long> longProperty(String identifier) {
        return longProperty(identifier, 0L);
    }

    public static SpellProperty<Float> floatProperty(String identifier) {
        return floatProperty(identifier, 0f);
    }

    public static SpellProperty<Double> doubleProperty(String identifier) {
        return doubleProperty(identifier, 0d);
    }

    public static SpellProperty<Boolean> booleanProperty(String identifier) {
        return booleanProperty(identifier, false);
    }

    public static SpellProperty<Byte> byteProperty(String identifier, byte defaultValue) {
        return createProperty((byte) 0, identifier, defaultValue, BYTE);
    }

    public static SpellProperty<Short> shortProperty(String identifier, short defaultValue) {
        return createProperty((short) 0, identifier, defaultValue, SHORT);
    }

    public static SpellProperty<Integer> intProperty(String identifier, int defaultValue) {
        return createProperty(0, identifier, defaultValue, INT);
    }

    public static SpellProperty<Long> longProperty(String identifier, long defaultValue) {
        return createProperty(0L, identifier, defaultValue, LONG);
    }

    public static SpellProperty<Float> floatProperty(String identifier, float defaultValue) {
        return createProperty(0F, identifier, defaultValue, FLOAT);
    }

    public static SpellProperty<Double> doubleProperty(String identifier, double defaultValue) {
        return createProperty(0D, identifier, defaultValue, DOUBLE);
    }

    public static SpellProperty<Boolean> booleanProperty(String identifier, boolean defaultValue) {
        return createProperty(false, identifier, defaultValue, BOOLEAN);
    }

    // You might be wondering why no string property? Well that's because what logic would a spell use a string for?
    // If you need a specific property then just inherit this class to add it

    protected static <T> SpellProperty<T> createProperty(T ignoredTypeIdentifier, String identifier, T defaultValue, Type type) {
        SpellProperty<T> property = new SpellProperty<T>();
        property.identifier = identifier;
        property.type = type;
        property.defaultValue = defaultValue;
        property.value = defaultValue;
        return property;
    }

    protected SpellProperty<T> initProperty(T ignoredTypeIdentifier, String identifier, T defaultValue, Type type) {
        this.identifier = identifier;
        this.type = type;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        return this;
    }

    public CompoundTag serializeOn(CompoundTag tag) {
        var wrapper = Util.wrapperTag(tag);
        if(type == BYTE)    return wrapper.put(BYTE.id(),    Util.compoundTagFrom(tag, BYTE.id(), t -> t.putByte(this.identifier, (Byte) this.value)));
        if(type == SHORT)   return wrapper.put(SHORT.id(),   Util.compoundTagFrom(tag, SHORT.id(), t -> t.putShort(this.identifier, (Short) this.value)));
        if(type == INT)     return wrapper.put(INT.id(),     Util.compoundTagFrom(tag, INT.id(), t -> t.putInt(this.identifier, (Integer) this.value)));
        if(type == LONG)    return wrapper.put(LONG.id(),    Util.compoundTagFrom(tag, LONG.id(), t -> t.putLong(this.identifier, (Long) this.value)));
        if(type == FLOAT)   return wrapper.put(FLOAT.id(),   Util.compoundTagFrom(tag, FLOAT.id(), t -> t.putFloat(this.identifier, (Float) this.value)));
        if(type == DOUBLE)  return wrapper.put(DOUBLE.id(),  Util.compoundTagFrom(tag, DOUBLE.id(), t -> t.putDouble(this.identifier, (Double) this.value)));
        if(type == BOOLEAN) return wrapper.put(BOOLEAN.id(), Util.compoundTagFrom(tag, BOOLEAN.id(), t -> t.putBoolean(this.identifier, (Boolean) this.value)));
        return tag;
    }

    public static List<SpellProperty<?>> deserialize(CompoundTag tag) {
        List<SpellProperty<?>> properties = new ArrayList<>();

        Type.getTypes().forEachRemaining(type -> {
            if (tag.contains(type.id())) {
                CompoundTag foundTag = (CompoundTag) tag.get(type.id());
                var key = foundTag.getAllKeys();
                key.forEach(s -> {
                    properties.add(type.deserialize(foundTag, s));
                });
            }
        });
        return properties;
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    SpellProperty<T> defaultValue(T value) {
        this.defaultValue = value;
        this.value = value;
        return this;
    }

    public boolean equals(SpellProperty<T> property) {
        return property.identifier.equals(this.identifier) && property.type == this.type;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public static final Type BYTE    = SpellProperty.Type.addType("byte_", SpellProperty::deserializeByte);
    public static final Type SHORT   = SpellProperty.Type.addType("short_", SpellProperty::deserializeShort);
    public static final Type INT     = SpellProperty.Type.addType("int_", SpellProperty::deserializeInt);
    public static final Type LONG    = SpellProperty.Type.addType("long_", SpellProperty::deserializeLong);
    public static final Type FLOAT   = SpellProperty.Type.addType("float_", SpellProperty::deserializeFloat);
    public static final Type DOUBLE  = SpellProperty.Type.addType("double_", SpellProperty::deserializeDouble);
    public static final Type BOOLEAN = SpellProperty.Type.addType("boolean_", SpellProperty::deserializeBoolean);

    private static SpellProperty<Byte> deserializeByte(CompoundTag tag, String location) {
        SpellProperty<Byte> property = new SpellProperty<>();
        property.identifier = location;
        property.value = tag.getByte(location);
        return property;
    }

    private static SpellProperty<Short> deserializeShort(CompoundTag tag, String location) {
        SpellProperty<Short> property = new SpellProperty<>();
        property.identifier = location;
        property.value = tag.getShort(location);
        return property;
    }

    private static SpellProperty<Integer> deserializeInt(CompoundTag tag, String location) {
        SpellProperty<Integer> property = new SpellProperty<>();
        property.identifier = location;
        property.value = tag.getInt(location);
        return property;
    }

    private static SpellProperty<Long> deserializeLong(CompoundTag tag, String location) {
        SpellProperty<Long> property = new SpellProperty<>();
        property.identifier = location;
        property.value = tag.getLong(location);
        return property;
    }

    private static SpellProperty<Float> deserializeFloat(CompoundTag tag, String location) {
        SpellProperty<Float> property = new SpellProperty<>();
        property.identifier = location;
        property.value = tag.getFloat(location);
        return property;
    }

    private static SpellProperty<Double> deserializeDouble(CompoundTag tag, String location) {
        SpellProperty<Double> property = new SpellProperty<>();
        property.identifier = location;
        property.value = tag.getDouble(location);
        return property;
    }

    private static SpellProperty<Boolean> deserializeBoolean(CompoundTag tag, String location) {
        SpellProperty<Boolean> property = new SpellProperty<>();
        property.identifier = location;
        property.value = tag.getBoolean(location);
        return property;
    }

    public static class Type {
        private static final Set<Type> PROPERTY_TYPES = new HashSet<>();
        private static boolean frozen;

        private String serializedIdentifier;
        private BiFunction<CompoundTag, String, SpellProperty<?>> deserializer;

        public static Type addType(String serializedIdentifier, BiFunction<CompoundTag, String, SpellProperty<?>> deserializer) {
            if(frozen) return null; // Throw error or crash the game so big unexplainable bugs / crashes dont happen

            Type type = new Type();
            type.serializedIdentifier = serializedIdentifier;
            type.deserializer = deserializer;
            PROPERTY_TYPES.add(type);
            return type;
        }

        public SpellProperty<?> deserialize(CompoundTag tag, String location) {
            return this.deserializer.apply(tag, location);
        }

        public static Iterator<Type> getTypes() {
            return PROPERTY_TYPES.iterator();
        }

        public static void freeze() {
            frozen = true;
        }

        public String id() {
            return serializedIdentifier;
        }

        private Type() {}
    }

    public static void load(){}

    protected SpellProperty(){}
}
