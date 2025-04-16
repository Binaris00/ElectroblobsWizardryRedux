package com.electroblob.wizardry.api.content.data;

import com.electroblob.wizardry.api.content.util.NBTExtras;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface IStoredVariable<T> extends IVariable<T> {
    void write(CompoundTag nbt, T value);

    T read(CompoundTag nbt);

    class StoredVariable<T, E extends Tag> implements IStoredVariable<T> {
        private final String key;
        private final Persistence persistence;

        private final Function<T, E> serialiser;
        private final Function<E, T> deserialiser;

        private boolean synced;

        private BiFunction<Player, T, T> ticker;

        public StoredVariable(String key, Function<T, E> serialiser, Function<E, T> deserialiser, Persistence persistence) {
            this.key = key;
            this.serialiser = serialiser;
            this.deserialiser = deserialiser;
            this.persistence = persistence;
            this.ticker = (p, t) -> t;
        }

        public StoredVariable<T, E> withTicker(BiFunction<Player, T, T> ticker) {
            this.ticker = ticker;
            return this;
        }

        public StoredVariable<T, E> setSynced() {
            this.synced = true;
            return this;
        }

        @Override
        public void write(CompoundTag nbt, T value) {
            if (value != null) NBTExtras.storeTagSafely(nbt, key, serialiser.apply(value));
        }

        @Override
        @SuppressWarnings("unchecked")
        public T read(CompoundTag nbt) {
            return nbt.contains(key) ? deserialiser.apply((E) nbt.get(key)) : null;
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
            if (nbt == null) return null;
            return read(nbt);
        }

        public static StoredVariable<Byte, ByteTag> ofByte(String key, Persistence persistence) {
            return new StoredVariable<>(key, ByteTag::valueOf, ByteTag::getAsByte, persistence);
        }

        public static StoredVariable<Boolean, ByteTag> ofBoolean(String key, Persistence persistence) {
            return new StoredVariable<>(key, b -> ByteTag.valueOf((byte) (b ? 1 : 0)), t -> t.getAsByte() == 1, persistence);
        }

        public static StoredVariable<Integer, IntTag> ofInt(String key, Persistence persistence) {
            return new StoredVariable<>(key, IntTag::valueOf, IntTag::getAsInt, persistence);
        }

        public static StoredVariable<int[], IntArrayTag> ofIntArray(String key, Persistence persistence) {
            return new StoredVariable<>(key, IntArrayTag::new, IntArrayTag::getAsIntArray, persistence);
        }

        public static StoredVariable<Float, FloatTag> ofFloat(String key, Persistence persistence) {
            return new StoredVariable<>(key, FloatTag::valueOf, FloatTag::getAsFloat, persistence);
        }

        public static StoredVariable<Double, DoubleTag> ofDouble(String key, Persistence persistence) {
            return new StoredVariable<>(key, DoubleTag::valueOf, DoubleTag::getAsDouble, persistence);
        }

        public static StoredVariable<Short, ShortTag> ofShort(String key, Persistence persistence) {
            return new StoredVariable<>(key, ShortTag::valueOf, ShortTag::getAsShort, persistence);
        }

        public static StoredVariable<Long, LongTag> ofLong(String key, Persistence persistence) {
            return new StoredVariable<>(key, LongTag::valueOf, LongTag::getAsLong, persistence);
        }

        public static StoredVariable<String, StringTag> ofString(String key, Persistence persistence) {
            return new StoredVariable<>(key, StringTag::valueOf, StringTag::getAsString, persistence);
        }

        public static StoredVariable<BlockPos, CompoundTag> ofBlockPos(String key, Persistence persistence) {
            return new StoredVariable<>(key, NbtUtils::writeBlockPos, NbtUtils::readBlockPos, persistence);
        }

        public static StoredVariable<UUID, IntArrayTag> ofUUID(String key, Persistence persistence) {
            return new StoredVariable<>(key, NbtUtils::createUUID, NbtUtils::loadUUID, persistence);
        }

        public static StoredVariable<CompoundTag, CompoundTag> ofNBT(String key, Persistence persistence) {
            return new StoredVariable<>(key, t -> t, t -> t, persistence);
        }
    }
}
