package com.electroblob.wizardry.api.common.spell.properties;

import net.minecraft.nbt.CompoundTag;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;

public class PropertyType {
    private static final Set<PropertyType> PROPERTY_TYPES = new HashSet<>();
    private String serializedIdentifier;
    private BiFunction<CompoundTag, String, SpellProperty<?>> deserializer;

    public static PropertyType addType(String serializedIdentifier, BiFunction<CompoundTag, String, SpellProperty<?>> deserializer) {
        PropertyType type = new PropertyType();
        type.serializedIdentifier = serializedIdentifier;
        type.deserializer = deserializer;
        PROPERTY_TYPES.add(type);
        return type;
    }

    public SpellProperty<?> deserialize(CompoundTag tag, String location) {
        return this.deserializer.apply(tag, location);
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
