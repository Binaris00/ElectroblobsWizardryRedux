package com.electroblob.wizardry.api.content.spell.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Registry class for different property types used in spell properties. This class maintains a mapping of property type
 * identifiers to their corresponding {@link IPropertyType} implementations, allowing for easy retrieval and management
 * of various property types. It includes predefined property types for common primitive data types such as Byte, Short,
 * Integer, Long, Float, Double, Boolean, and String. Additional custom property types can be added as needed.
 * <p>
 * Adding new property types should be done using the {@link #addType(String, IPropertyType)} method.
 */
public final class PropertyTypes {
    /** Map of property type identifiers to their corresponding IPropertyType implementations. */
    private static final Map<String, IPropertyType<?>> PROPERTY_TYPES = new HashMap<>();

    public static final PropertyType<Byte> BYTE = addType("byte_", (t, s) -> deserializeNbt(t, s, a -> t.getByte(s)), (j, s) -> deserializeJson(j, s, JsonElement::getAsByte), (j, p) -> serializeJson(j, p, (obj, val) -> obj.addProperty(p.identifier, val)), (t, p) -> serializeNbt(t, p, (tag, val) -> tag.putByte(p.identifier, val)));
    public static final PropertyType<Short> SHORT = addType("short_", (t, s) -> deserializeNbt(t, s, a -> t.getShort(s)), (j, s) -> deserializeJson(j, s, JsonElement::getAsShort), (j, p) -> serializeJson(j, p, (obj, val) -> obj.addProperty(p.identifier, val)), (t, p) -> serializeNbt(t, p, (tag, val) -> tag.putShort(p.identifier, val)));
    public static final PropertyType<Integer> INT = addType("int_", (t, s) -> deserializeNbt(t, s, a -> t.getInt(s)), (j, s) -> deserializeJson(j, s, JsonElement::getAsInt), (j, p) -> serializeJson(j, p, (obj, val) -> obj.addProperty(p.identifier, val)), (t, p) -> serializeNbt(t, p, (tag, val) -> tag.putInt(p.identifier, val)));
    public static final PropertyType<Long> LONG = addType("long_", (t, s) -> deserializeNbt(t, s, a -> t.getLong(s)), (j, s) -> deserializeJson(j, s, JsonElement::getAsLong), (j, p) -> serializeJson(j, p, (obj, val) -> obj.addProperty(p.identifier, val)), (t, p) -> serializeNbt(t, p, (tag, val) -> tag.putLong(p.identifier, val)));
    public static final PropertyType<Float> FLOAT = addType("float_", (t, s) -> deserializeNbt(t, s, a -> t.getFloat(s)), (j, s) -> deserializeJson(j, s, JsonElement::getAsFloat), (j, p) -> serializeJson(j, p, (obj, val) -> obj.addProperty(p.identifier, val)), (t, p) -> serializeNbt(t, p, (tag, val) -> tag.putFloat(p.identifier, val)));
    public static final PropertyType<Double> DOUBLE = addType("double_", (t, s) -> deserializeNbt(t, s, a -> t.getDouble(s)), (j, s) -> deserializeJson(j, s, JsonElement::getAsDouble), (j, p) -> serializeJson(j, p, (obj, val) -> obj.addProperty(p.identifier, val)), (t, p) -> serializeNbt(t, p, (tag, val) -> tag.putDouble(p.identifier, val)));
    public static final PropertyType<Boolean> BOOLEAN = addType("boolean_", (t, s) -> deserializeNbt(t, s, a -> t.getBoolean(s)), (j, s) -> deserializeJson(j, s, JsonElement::getAsBoolean), (j, p) -> serializeJson(j, p, (obj, val) -> obj.addProperty(p.identifier, val)), (t, p) -> serializeNbt(t, p, (tag, val) -> tag.putBoolean(p.identifier, val)));
    public static final PropertyType<String> STRING = addType("string_", (t, s) -> deserializeNbt(t, s, a -> t.getString(s)), (j, s) -> deserializeJson(j, s, JsonElement::getAsString), (j, p) -> serializeJson(j, p, (obj, val) -> obj.addProperty(p.identifier, val)), (t, p) -> serializeNbt(t, p, (tag, val) -> tag.putString(p.identifier, val)));
    public static final PropertyType<Map<String, Boolean>> CONTEXT_MAP = addType("context_map_", PropertyTypes::deserializeContextMapNbt, PropertyTypes::deserializeContextMapJson, PropertyTypes::serializeContextMapJson, PropertyTypes::serializeContextMapNbt);

    /**
     * Registers a new property type with the given identifier.
     *
     * @param id           The unique identifier for the property type.
     * @param propertyType The property type implementation to register.
     * @param <T>          The type of the property value.
     * @return The registered property type.
     */
    public static <T> IPropertyType<T> addType(String id, IPropertyType<T> propertyType) {
        PROPERTY_TYPES.put(id, propertyType);
        return propertyType;
    }

    /**
     * Retrieves the property type associated with the given identifier.
     *
     * @param id The unique identifier for the property type.
     * @return The property type implementation, or null if not found.
     */
    public static @Nullable IPropertyType<?> getType(String id) {
        return PROPERTY_TYPES.getOrDefault(id, null);
    }


    /**
     * Checks if a property type with the given identifier is registered.
     *
     * @param id The unique identifier for the property type.
     * @return True if the property type is registered, false otherwise.
     */
    public static boolean hasType(String id) {
        return PROPERTY_TYPES.containsKey(id);
    }

    /**
     * Returns a map of all registered property types.
     *
     * @return A map where keys are property type identifiers and values are their corresponding implementations.
     */
    public static Map<String, IPropertyType<?>> getAllTypes() {
        return PROPERTY_TYPES;
    }


    /**
     * Adds a new property type using functional interfaces for serialization and deserialization.
     *
     * @param name             The unique identifier for the property type.
     * @param tagDeserializer  Function to deserialize from NBT.
     * @param jsonDeserializer Function to deserialize from JSON.
     * @param jsonSerializer   Function to serialize to JSON.
     * @param tagSerializer    Function to serialize to NBT.
     * @param <T>              The type of the property value.
     * @return The registered property type implementation.
     */
    private static <T> PropertyType<T> addType(String name, BiFunction<CompoundTag, String, SpellProperty<T>> tagDeserializer, BiFunction<JsonElement, String, SpellProperty<T>> jsonDeserializer, BiConsumer<JsonObject, SpellProperty<T>> jsonSerializer, BiConsumer<CompoundTag, SpellProperty<T>> tagSerializer) {
        return (PropertyType<T>) addType(name, new PropertyType<>(jsonDeserializer, tagDeserializer, jsonSerializer, tagSerializer));
    }


    /**
     * Deserializes a spell property from a compound NBT tag using the provided getter function. Used to avoid
     * boilerplate in the property type definitions.
     *
     * @param tag      The NBT tag to deserialize from.
     * @param location The location or identifier for the property.
     * @param getter   Function to extract the property value from the NBT tag.
     * @param <T>      The type of the property value.
     * @return The deserialized spell property.
     * @throws IllegalArgumentException if no property is found with the given identifier.
     */
    private static <T> SpellProperty<T> deserializeNbt(CompoundTag tag, String location, Function<String, T> getter) {
        SpellProperty base = SpellProperty.fromID(location);
        if (base == null) throw new IllegalArgumentException("No property found with identifier: " + location);

        SpellProperty property = base.copyOf();
        property.value = getter.apply(location);
        return property;
    }

    /**
     * Deserializes a spell property from a JSON element using the provided getter function. Used to avoid
     * boilerplate in the property type definitions.
     *
     * @param json     The JSON element to deserialize from.
     * @param location The location or identifier for the property.
     * @param getter   Function to extract the property value from the JSON element.
     * @param <T>      The type of the property value.
     * @return The deserialized spell property.
     * @throws IllegalArgumentException if no property is found with the given identifier.
     */
    private static <T> SpellProperty<T> deserializeJson(JsonElement json, String location, Function<JsonElement, T> getter) {
        SpellProperty<T> base = (SpellProperty<T>) SpellProperty.fromID(location);
        if (base == null) throw new IllegalArgumentException("No property found with identifier: " + location);

        SpellProperty<T> property = base.copyOf();
        property.value = getter.apply(json);
        return property;
    }

    /**
     * Deserializes a context map spell property from a compound NBT tag. Used to avoid boilerplate in the property type
     * definitions just for context map types, since they require special handling.
     *
     * @param tag      The NBT tag to deserialize from.
     * @param location The location or identifier for the property.
     * @return The deserialized spell property containing a context map.
     * @throws IllegalArgumentException if no property is found with the given identifier.
     */
    private static SpellProperty<Map<String, Boolean>> deserializeContextMapNbt(CompoundTag tag, String location) {
        SpellProperty<Map<String, Boolean>> base = (SpellProperty<Map<String, Boolean>>) SpellProperty.fromID(location);
        if (base == null) throw new IllegalArgumentException("No property found with identifier: " + location);

        SpellProperty<Map<String, Boolean>> property = base.copyOf();
        Map<String, Boolean> contextMap = new HashMap<>();

        CompoundTag contextTag = tag.getCompound(location);
        for (String key : contextTag.getAllKeys()) {
            contextMap.put(key, contextTag.getBoolean(key));
        }

        property.value = contextMap;
        return property;
    }

    /**
     * Deserializes a context map spell property from a JSON element. Used to avoid boilerplate in the property type
     * definitions just for context map types, since they require special handling.
     *
     * @param json     The JSON element to deserialize from.
     * @param location The location or identifier for the property.
     * @return The deserialized spell property containing a context map.
     * @throws IllegalArgumentException if no property is found with the given identifier.
     */
    private static SpellProperty<Map<String, Boolean>> deserializeContextMapJson(JsonElement json, String location) {
        SpellProperty<Map<String, Boolean>> base = (SpellProperty<Map<String, Boolean>>) SpellProperty.fromID(location);
        if (base == null) throw new IllegalArgumentException("No property found with identifier: " + location);

        SpellProperty<Map<String, Boolean>> property = base.copyOf();
        Map<String, Boolean> contextMap = new HashMap<>();

        if (json.isJsonObject()) {
            JsonObject contextObj = json.getAsJsonObject();
            contextObj.entrySet().forEach(entry -> {
                contextMap.put(entry.getKey(), entry.getValue().getAsBoolean());
            });
        }

        property.value = contextMap;
        return property;
    }

    private static void serializeContextMapJson(JsonObject json, SpellProperty<Map<String, Boolean>> property) {
        JsonObject contextObj = new JsonObject();
        property.value.forEach(contextObj::addProperty);
        json.add(property.identifier, contextObj);
    }

    private static void serializeContextMapNbt(CompoundTag tag, SpellProperty<Map<String, Boolean>> property) {
        CompoundTag contextTag = new CompoundTag();
        property.value.forEach(contextTag::putBoolean);
        tag.put(property.identifier, contextTag);
    }

    private static <T> void serializeJson(JsonObject json, SpellProperty<T> property, BiConsumer<JsonObject, T> setter) {
        setter.accept(json, property.value);
    }

    private static <T> void serializeNbt(CompoundTag tag, SpellProperty<T> property, BiConsumer<CompoundTag, T> setter) {
        setter.accept(tag, property.value);
    }


    private PropertyTypes() {
    }
}
