package com.electroblob.wizardry.api.content.spell.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;

/**
 * Interface defining methods for serializing and deserializing spell properties to and from JSON and NBT formats. This
 * is used as an interface just to give the possibility of having different and custom implementations of property types,
 * not just with primitive types but also for complex objects. (e.g. for handling multiple related values in a single property).
 *
 * @param <T> The type of the property value.
 */
public interface IPropertyType<T> {
    /**
     * Deserializes a spell property from a JSON element.
     *
     * @param jsonElement The JSON element to deserialize from.
     * @param location    The location or identifier for the property.
     * @return The deserialized spell property.
     */
    SpellProperty<T> deserialize(JsonElement jsonElement, String location);

    /**
     * Deserializes a spell property from a compound NBT tag.
     *
     * @param tag      The NBT tag to deserialize from.
     * @param location The location or identifier for the property.
     * @return The deserialized spell property.
     */
    SpellProperty<T> deserialize(CompoundTag tag, String location);

    /**
     * Serializes a spell property to a JSON object.
     *
     * @param json     The JSON object to serialize to.
     * @param property The spell property to serialize.
     */
    void serialize(JsonObject json, SpellProperty<T> property);

    /**
     * Serializes a spell property to a compound NBT tag.
     *
     * @param tag      The NBT tag to serialize to.
     * @param property The spell property to serialize.
     */
    void serialize(CompoundTag tag, SpellProperty<T> property);
}
