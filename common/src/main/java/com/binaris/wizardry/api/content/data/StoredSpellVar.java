package com.binaris.wizardry.api.content.data;

import com.binaris.wizardry.core.EBLogger;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

/// Default implementation of [IStoredSpellVar] with support of serialization/deserialization using [Codec].
///
/// The save and load of these vars is managed by the [SpellManagerData] component.
///
/// @param <T> the type of value this spell variable holds
public class StoredSpellVar<T> implements IStoredSpellVar<T> {
    private final String key;
    private final VarPersistence persistence;
    private final Codec<T> codec;
    private boolean synced;
    private BiFunction<Player, T, T> ticker = (p, t) -> t;

    /// Creates a new stored spell variable with the specified serialization functions.
    ///
    /// @param key         the NBT key used to store this variable
    /// @param codec       the codec used to serialize and deserialize this variable
    /// @param persistence the persistence settings for this variable
    public StoredSpellVar(String key, Codec<T> codec, VarPersistence persistence) {
        this.key = key;
        this.codec = codec;
        this.persistence = persistence;
    }

    /// Creates a stored spell variable for byte values.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for byte values
    public static StoredSpellVar<Byte> ofByte(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, Codec.BYTE, persistence);
    }

    /// Creates a stored spell variable for boolean values.
    ///
    /// Booleans are stored as bytes (1 for true, 0 for false).
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for boolean values
    public static StoredSpellVar<Boolean> ofBoolean(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, Codec.BOOL, persistence);
    }

    /// Creates a stored spell variable for integer values.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for integer values
    public static StoredSpellVar<Integer> ofInt(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, Codec.INT, persistence);
    }

    /// Creates a stored spell variable for integer list.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for integer list
    public static StoredSpellVar<List<Integer>> ofIntList(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, Codec.INT.listOf(), persistence);
    }

    /// Creates a stored spell variable for float values.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for float values
    public static StoredSpellVar<Float> ofFloat(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, Codec.FLOAT, persistence);
    }

    /// Creates a stored spell variable for double values.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for double values
    public static StoredSpellVar<Double> ofDouble(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, Codec.DOUBLE, persistence);
    }

    /// Creates a stored spell variable for short values.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for short values
    public static StoredSpellVar<Short> ofShort(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, Codec.SHORT, persistence);
    }

    /// Creates a stored spell variable for long values.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for long values
    public static StoredSpellVar<Long> ofLong(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, Codec.LONG, persistence);
    }

    /// Creates a stored spell variable for string values.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for string values
    public static StoredSpellVar<String> ofString(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, Codec.STRING, persistence);
    }

    /// Creates a stored spell variable for BlockPos values.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for BlockPos values
    public static StoredSpellVar<BlockPos> ofBlockPos(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, BlockPos.CODEC, persistence);
    }

    /// Creates a stored spell variable for UUID values.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for UUID values
    public static StoredSpellVar<UUID> ofUUID(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, UUIDUtil.CODEC, persistence);
    }

    /// Creates a stored spell variable for raw NBT compound tag values.
    ///
    /// @param key         the NBT key
    /// @param persistence the persistence settings
    /// @return a new stored spell variable for NBT compound tag values
    public static StoredSpellVar<CompoundTag> ofNBT(String key, VarPersistence persistence) {
        return new StoredSpellVar<>(key, CompoundTag.CODEC, persistence);
    }

    /// Sets a custom ticker function for this spell variable. The ticker function is called during
    /// [#update(Player, Object)] to compute the new value.
    ///
    /// @param ticker a function that takes a player and current value, and returns the updated value
    /// @return this spell variable for method chaining
    public StoredSpellVar<T> withTicker(BiFunction<Player, T, T> ticker) {
        this.ticker = ticker;
        return this;
    }

    /// Enables network synchronization for this spell variable. When synced, the variable will be automatically
    /// sent to clients when it changes.
    ///
    /// @return this spell variable for method chaining
    public StoredSpellVar<T> setSynced() {
        this.synced = true;
        return this;
    }

    @Override
    public void write(CompoundTag nbt, T value) {
        if (value == null) return;
        codec.encodeStart(NbtOps.INSTANCE, value)
                .resultOrPartial(err -> EBLogger.error("Failed to encode spell var '" + key + "': " + err))
                .ifPresent(tag -> nbt.put(key, tag));
    }

    @Override
    public T read(CompoundTag nbt) {
        if (!nbt.contains(key)) return null;
        return codec.parse(NbtOps.INSTANCE, nbt.get(key))
                .resultOrPartial(err -> EBLogger.error("Failed to decode spell var '" + key + "': " + err))
                .orElse(null);
    }
    @Override
    public T update(Player player, T value) {
        return ticker.apply(player, value);
    }

    @Override
    public boolean isPersistent(boolean respawn) {
        return respawn ? persistence.persistsOnRespawn() : persistence.persistsOnDimensionChange();
    }

    @Override
    public boolean isSynced() {
        return synced;
    }

    @Override
    public void write(FriendlyByteBuf buf, T value) {
        if (!synced) return;
        CompoundTag nbt = new CompoundTag();
        write(nbt, value);
        buf.writeNbt(nbt);
    }

    @Override
    public T read(FriendlyByteBuf buf) {
        if (!synced) return null;
        CompoundTag nbt = buf.readNbt();
        return nbt != null ? read(nbt) : null;
    }
}
