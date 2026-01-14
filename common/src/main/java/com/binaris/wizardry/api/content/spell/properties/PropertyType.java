package com.binaris.wizardry.api.content.spell.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Generic implementation of the {@link IPropertyType} interface using functional interfaces for serialization and
 * deserialization. This class allows for flexible handling of different property types by providing custom functions
 * for converting between JSON, NBT, and the property value type. We use this because the main <b>Electroblob's Wizardry</b>
 * mod codebase only needs a few primitive types, but addons might want to implement more complex types, in those cases
 * you need to implement your own {@link IPropertyType} and register it on {@link PropertyTypes}.
 */
public class PropertyType<T> implements IPropertyType<T> {
    private final BiFunction<JsonElement, String, SpellProperty<T>> jsonDeserializer;
    private final BiFunction<CompoundTag, String, SpellProperty<T>> tagDeserializer;
    private final BiConsumer<JsonObject, SpellProperty<T>> jsonSerializer;
    private final BiConsumer<CompoundTag, SpellProperty<T>> tagSerializer;

    public PropertyType(
            BiFunction<JsonElement, String, SpellProperty<T>> jsonDeserializer,
            BiFunction<CompoundTag, String, SpellProperty<T>> tagDeserializer,
            BiConsumer<JsonObject, SpellProperty<T>> jsonSerializer,
            BiConsumer<CompoundTag, SpellProperty<T>> tagSerializer
    ) {
        this.jsonDeserializer = jsonDeserializer;
        this.tagDeserializer = tagDeserializer;
        this.jsonSerializer = jsonSerializer;
        this.tagSerializer = tagSerializer;
    }

    @Override
    public SpellProperty<T> deserialize(JsonElement json, String loc) {
        return jsonDeserializer.apply(json, loc);
    }

    @Override
    public SpellProperty<T> deserialize(CompoundTag tag, String loc) {
        return tagDeserializer.apply(tag, loc);
    }

    @Override
    public void serialize(JsonObject json, SpellProperty<T> prop) {
        jsonSerializer.accept(json, prop);
    }

    @Override
    public void serialize(CompoundTag tag, SpellProperty<T> prop) {
        tagSerializer.accept(tag, prop);
    }
}
