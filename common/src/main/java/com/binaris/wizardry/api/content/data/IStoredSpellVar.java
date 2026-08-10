package com.binaris.wizardry.api.content.data;

import net.minecraft.nbt.*;

/// Extension of [ISpellVar] that adds NBT serialization capabilities for persistent storage.
///
/// Stored spell variables can be serialized to and deserialized using a custom implementation or using [com.mojang.serialization.Codec].
///
/// @param <T> the type of value this spell variable holds
@SuppressWarnings("unused")
public interface IStoredSpellVar<T> extends ISpellVar<T> {
    /// Writes the value to an NBT compound tag for persistent storage.
    ///
    /// @param nbt   the NBT compound tag to write to
    /// @param value the value to write
    void write(CompoundTag nbt, T value);

    /// Reads the value from an NBT compound tag.
    ///
    /// @param nbt the NBT compound tag to read from
    /// @return the value read from the tag, or null if no value is present
    T read(CompoundTag nbt);
}