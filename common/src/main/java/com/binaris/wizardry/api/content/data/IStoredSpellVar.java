package com.binaris.wizardry.api.content.data;

import com.binaris.wizardry.api.content.util.NBTExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Extension of {@link ISpellVar} that adds NBT serialization capabilities for persistent storage.
 * <p>
 * Stored spell variables can be serialized to and deserialized from NBT tags, allowing them
 * to be saved to disk and persist across game sessions.
 *
 * @param <T> the type of value this spell variable holds
 */
@SuppressWarnings("unused")
public interface IStoredSpellVar<T> extends ISpellVar<T> {
    /**
     * Writes the value to an NBT compound tag for persistent storage.
     *
     * @param nbt   the NBT compound tag to write to
     * @param value the value to write
     */
    void write(CompoundTag nbt, T value);

    /**
     * Reads the value from an NBT compound tag.
     *
     * @param nbt the NBT compound tag to read from
     * @return the value read from the tag, or null if no value is present
     */
    T read(CompoundTag nbt);

    /**
     * Default implementation of {@link IStoredSpellVar} with NBT serialization support.
     * <p>
     * This implementation uses customizable serializer and deserializer functions to convert
     * between the value type and NBT tags. It supports both NBT and network synchronization.
     *
     * @param <T> the type of value this spell variable holds
     * @param <E> the type of NBT tag used for serialization
     */
    class StoredSpellVar<T, E extends Tag> implements IStoredSpellVar<T> {
        private final String key;
        private final Persistence persistence;
        private final Function<T, E> serializer;
        private final Function<E, T> deserializer;
        private boolean synced;
        private BiFunction<Player, T, T> ticker = (p, t) -> t;

        /**
         * Creates a new stored spell variable with the specified serialization functions.
         *
         * @param key          the NBT key used to store this variable
         * @param serializer   function to convert the value to an NBT tag
         * @param deserializer function to convert an NBT tag back to the value
         * @param persistence  the persistence settings for this variable
         */
        public StoredSpellVar(String key, Function<T, E> serializer, Function<E, T> deserializer, Persistence persistence) {
            this.key = key;
            this.serializer = serializer;
            this.deserializer = deserializer;
            this.persistence = persistence;
        }

        /**
         * Creates a stored spell variable for byte values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for byte values
         */
        public static StoredSpellVar<Byte, ByteTag> ofByte(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, ByteTag::valueOf, ByteTag::getAsByte, persistence);
        }

        /**
         * Creates a stored spell variable for boolean values.
         * <p>
         * Booleans are stored as bytes (1 for true, 0 for false).
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for boolean values
         */
        public static StoredSpellVar<Boolean, ByteTag> ofBoolean(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, b -> ByteTag.valueOf((byte) (b ? 1 : 0)), t -> t.getAsByte() == 1, persistence);
        }

        /**
         * Creates a stored spell variable for integer values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for integer values
         */
        public static StoredSpellVar<Integer, IntTag> ofInt(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, IntTag::valueOf, IntTag::getAsInt, persistence);
        }

        /**
         * Creates a stored spell variable for integer array values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for integer array values
         */
        public static StoredSpellVar<int[], IntArrayTag> ofIntArray(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, IntArrayTag::new, IntArrayTag::getAsIntArray, persistence);
        }

        /**
         * Creates a stored spell variable for float values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for float values
         */
        public static StoredSpellVar<Float, FloatTag> ofFloat(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, FloatTag::valueOf, FloatTag::getAsFloat, persistence);
        }

        /**
         * Creates a stored spell variable for double values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for double values
         */
        public static StoredSpellVar<Double, DoubleTag> ofDouble(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, DoubleTag::valueOf, DoubleTag::getAsDouble, persistence);
        }

        /**
         * Creates a stored spell variable for short values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for short values
         */
        public static StoredSpellVar<Short, ShortTag> ofShort(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, ShortTag::valueOf, ShortTag::getAsShort, persistence);
        }

        /**
         * Creates a stored spell variable for long values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for long values
         */
        public static StoredSpellVar<Long, LongTag> ofLong(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, LongTag::valueOf, LongTag::getAsLong, persistence);
        }

        /**
         * Creates a stored spell variable for string values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for string values
         */
        public static StoredSpellVar<String, StringTag> ofString(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, StringTag::valueOf, StringTag::getAsString, persistence);
        }

        /**
         * Creates a stored spell variable for BlockPos values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for BlockPos values
         */
        public static StoredSpellVar<BlockPos, CompoundTag> ofBlockPos(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, NbtUtils::writeBlockPos, NbtUtils::readBlockPos, persistence);
        }

        /**
         * Creates a stored spell variable for UUID values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for UUID values
         */
        public static StoredSpellVar<UUID, IntArrayTag> ofUUID(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, NbtUtils::createUUID, NbtUtils::loadUUID, persistence);
        }

        /**
         * Creates a stored spell variable for raw NBT compound tag values.
         *
         * @param key         the NBT key
         * @param persistence the persistence settings
         * @return a new stored spell variable for NBT compound tag values
         */
        public static StoredSpellVar<CompoundTag, CompoundTag> ofNBT(String key, Persistence persistence) {
            return new StoredSpellVar<>(key, t -> t, t -> t, persistence);
        }

        /**
         * Sets a custom ticker function for this spell variable. The ticker function is called during
         * {@link #update(Player, Object)} to compute the new value.
         *
         * @param ticker a function that takes a player and current value, and returns the updated value
         * @return this spell variable for method chaining
         */
        public StoredSpellVar<T, E> withTicker(BiFunction<Player, T, T> ticker) {
            this.ticker = ticker;
            return this;
        }

        /**
         * Enables network synchronization for this spell variable. When synced, the variable will be automatically
         * sent to clients when it changes.
         *
         * @return this spell variable for method chaining
         */
        public StoredSpellVar<T, E> setSynced() {
            this.synced = true;
            return this;
        }

        @Override
        public void write(CompoundTag nbt, T value) {
            if (value != null) NBTExtras.storeTagSafely(nbt, key, serializer.apply(value));
        }

        @Override
        @SuppressWarnings("unchecked")
        public T read(CompoundTag nbt) {
            return nbt.contains(key) ? deserializer.apply((E) nbt.get(key)) : null;
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
}