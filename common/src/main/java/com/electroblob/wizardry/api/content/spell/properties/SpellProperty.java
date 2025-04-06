package com.electroblob.wizardry.api.content.spell.properties;

import com.electroblob.wizardry.api.content.util.Util;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;

public class SpellProperty<T> {
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

    public static SpellProperty<String> stringProperty(String id){
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

    public static SpellProperty<String> stringProperty(String id, String value){
        return createProperty("", id, value, STRING);
    }

    // You might be wondering why no string property? Well that's because what logic would a spell use a string for?
    // If you need a specific property then just inherit this class to add it

    protected static <T> SpellProperty<T> createProperty(T ignoredTypeIdentifier, String identifier, T defaultValue, PropertyType type) {
        SpellProperty<T> property = new SpellProperty<T>();
        property.identifier = identifier;
        property.type = type;
        property.defaultValue = defaultValue;
        property.value = defaultValue;
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

        PropertyType.getTypes().forEachRemaining(type -> {
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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SpellProperty<?> property){
            return property.identifier.equals(this.identifier) && property.type == this.type;
        }
        return false;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public SpellProperty<T> copyOf(){
        SpellProperty<T> cloned = new SpellProperty<>();
        cloned.identifier = this.identifier;
        cloned.type = this.type;
        cloned.defaultValue = this.defaultValue;
        cloned.value = this.value;
        return cloned;
    }

    public static final PropertyType BYTE = PropertyType.addType("byte_", SpellProperty::deserializeByte);
    public static final PropertyType SHORT = PropertyType.addType("short_", SpellProperty::deserializeShort);
    public static final PropertyType INT = PropertyType.addType("int_", SpellProperty::deserializeInt);
    public static final PropertyType LONG = PropertyType.addType("long_", SpellProperty::deserializeLong);
    public static final PropertyType FLOAT = PropertyType.addType("float_", SpellProperty::deserializeFloat);
    public static final PropertyType DOUBLE = PropertyType.addType("double_", SpellProperty::deserializeDouble);
    public static final PropertyType BOOLEAN = PropertyType.addType("boolean_", SpellProperty::deserializeBoolean);
    public static final PropertyType STRING = PropertyType.addType("string_", SpellProperty::deserializeString);

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

    private static SpellProperty<String> deserializeString(CompoundTag tag, String location) {
        SpellProperty<String> property = new SpellProperty<>();
        property.identifier = location;
        property.value = tag.getString(location);
        return property;
    }

    public static void load(){}

    protected SpellProperty(){}
}
