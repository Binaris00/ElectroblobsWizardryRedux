package com.binaris.wizardry.api.content.data;

import com.binaris.wizardry.api.content.spell.Spell;

import java.util.Map;

/// Per-player data container tracking discovered spells and spell-specific runtime variables.
///
/// Stores two categories of data: (1) spell variable storage — arbitrary typed key-value pairs scoped to spells,
/// ticked every tick by {@code DataEvents.spellDataTick()}, used by spells like Charge (charge time/modifiers)
/// and Curse of Soulbinding (soulbound creature UUIDs); (2) spell discovery tracking — a set of spells the player
/// has discovered, checked by the HUD, trading, loot tables, and scroll items. Implementations differ per loader.
public interface SpellManagerData {

    /// Synchronizes this SpellManagerData with the client.
    void sync();

    /// Retrieves the value of a specific spell variable.
    ///
    /// @param var the spell variable to retrieve
    /// @param <T> the type of the spell variable
    /// @return the value of the specified spell variable
    <T> T getVariable(ISpellVar<T> var);

    /// Sets the value of a specific spell variable.
    ///
    /// @param variable the spell variable to set
    /// @param value    the value to set for the spell variable
    /// @param <T>      the type of the spell variable
    <T> void setVariable(ISpellVar<? super T> variable, T value);

    /// Returns a map of all spell variables and their corresponding values.
    ///
    /// @return a Map containing all spell variables and their values
    Map<ISpellVar, Object> getSpellData();

    /// Checks if the given spell has been discovered by the player.
    ///
    /// @param spell the Spell to check
    /// @return true if the spell has been discovered, false otherwise
    boolean hasSpellBeenDiscovered(Spell spell);

    /// Marks the given spell as discovered for the player.
    ///
    /// @param spell the Spell to mark as discovered
    /// @return true if the spell was not already discovered, false otherwise
    boolean discoverSpell(Spell spell);

    /// Deletes the given spell from the player's discovered spells.
    ///
    /// @param spell the Spell to mark as undiscovered
    /// @return true if the spell was previously discovered, false otherwise
    boolean undiscoverSpell(Spell spell);
}


