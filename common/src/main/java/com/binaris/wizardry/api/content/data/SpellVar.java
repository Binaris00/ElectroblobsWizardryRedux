package com.binaris.wizardry.api.content.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiFunction;

/// Default implementation of [ISpellVar] that provides basic functionality.
///
/// This implementation stores non-persistent, non-synced values with optional tick updates.
///
/// @param <T> the type of value this spell variable holds
public class SpellVar<T> implements ISpellVar<T> {
    private final VarPersistence persistence;
    private BiFunction<Player, T, T> ticker = (p, t) -> t;

    /// Creates a new spell variable with the specified persistence settings.
    ///
    /// @param persistence the persistence settings for this variable
    public SpellVar(VarPersistence persistence) {
        this.persistence = persistence;
    }

    /// Sets a custom ticker function for this spell variable.
    ///
    /// The ticker function is called during [#update(Player, Object)] to compute the new value.
    ///
    /// @param ticker a function that takes a player and current value, and returns the updated value
    /// @return this spell variable for method chaining
    public com.binaris.wizardry.api.content.data.SpellVar<T> withTicker(BiFunction<Player, T, T> ticker) {
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
