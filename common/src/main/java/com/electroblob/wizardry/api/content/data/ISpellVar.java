package com.electroblob.wizardry.api.content.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiFunction;

public interface ISpellVar<T> {
    T update(Player player, T value);

    boolean isPersistent(boolean respawn);

    boolean isSynced();

    void write(FriendlyByteBuf buf, T value);

    T read(FriendlyByteBuf buf);

    default boolean canPurge(Player player, T value) {
        return false;
    }

    class SpellVar<T> implements ISpellVar<T> {
        private final Persistence persistence;

        private BiFunction<Player, T, T> ticker;

        public SpellVar(Persistence persistence) {
            this.persistence = persistence;
            this.ticker = (p, t) -> t;
        }

        public SpellVar<T> withTicker(BiFunction<Player, T, T> ticker) {
            this.ticker = ticker;
            return this;
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
            return false;
        }

        @Override
        public void write(FriendlyByteBuf buf, T value) {

        }

        @Override
        public T read(FriendlyByteBuf buf) {
            return null;
        }
    }
}
