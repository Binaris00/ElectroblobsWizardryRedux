package com.binaris.wizardry.api.content.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

/// Interface for spell variables that can be stored and synchronized between client and server.
///
/// Spell variables are used to store data associated with spells or players, with support for
/// persistence across dimension changes and respawns, as well as network synchronization.
///
/// @param <T> the type of value this spell variable holds
public interface ISpellVar<T> {
    /// Updates the value of this spell variable for the given player.
    ///
    /// Creates changes based on the current value and the player, returning the updated value.
    ///
    /// @param player the player whose spell variable is being updated
    /// @param value  the current value
    /// @return the updated value
    T update(Player player, T value);

    /// Determines whether this spell variable should persist.
    ///
    /// @param respawn if true, checks persistence across player respawns; if false, checks persistence across dimension changes
    /// @return true if the variable should persist, false otherwise
    boolean isPersistent(boolean respawn);

    /// Checks whether this spell variable should be synchronized between client and server.
    ///
    /// @return true if the variable should be synced, false otherwise
    boolean isSynced();

    /// Writes the value to a network buffer for synchronization.
    ///
    /// @param buf   the buffer to write to
    /// @param value the value to write
    void write(FriendlyByteBuf buf, T value);

    /// Reads the value from a network buffer.
    ///
    /// @param buf the buffer to read from
    /// @return the value read from the buffer, or null if no value is present
    T read(FriendlyByteBuf buf);

    /// Determines whether this spell variable can be purged for the given player.
    ///
    /// By default, spell variables cannot be purged.
    ///
    /// @param player the player whose spell variable is being checked
    /// @param value  the current value
    /// @return true if the variable can be purged, false otherwise
    default boolean canPurge(Player player, T value) {
        return false;
    }
}
