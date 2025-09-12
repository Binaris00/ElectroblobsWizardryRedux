package com.electroblob.wizardry.api.content.spell.properties;

import com.google.gson.JsonElement;
import net.minecraft.nbt.CompoundTag;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;

public class PropertyType {
    private static final Set<PropertyType> PROPERTY_TYPES = new HashSet<>();
    private String serializedIdentifier;
    private BiFunction<CompoundTag, String, SpellProperty<?>> deserializerNbt;
    private BiFunction<JsonElement, String, SpellProperty<?>> jsonDeserializer;

    public static PropertyType addType(String serializedIdentifier, BiFunction<CompoundTag, String, SpellProperty<?>> deserializer, BiFunction<JsonElement, String, SpellProperty<?>> jsonDeserializer) {
        PropertyType type = new PropertyType();
        type.serializedIdentifier = serializedIdentifier;
        type.deserializerNbt = deserializer;
        type.jsonDeserializer = jsonDeserializer;
        PROPERTY_TYPES.add(type);
        return type;
    }

    public SpellProperty<?> deserialize(CompoundTag tag, String location) {
        return this.deserializerNbt.apply(tag, location);
    }

    public SpellProperty<?> deserialize(JsonElement tag, String location) {
        return this.jsonDeserializer.apply(tag, location);
    }

    public static Iterator<PropertyType> getTypes() {
        return PROPERTY_TYPES.iterator();
    }

    public String id() {
        return serializedIdentifier;
    }

    private PropertyType() {
    }
}
