package com.binaris.wizardry.api.content.data;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;

public interface CastCommandData {
    /// Starts casting the given spell with the given modifiers.
    void startCastingContinuousSpell(Spell spell, SpellModifiers modifiers, int duration);

    /// Stops casting the current spell.
    void stopCastingContinuousSpell();

    /// Casts the current continuous spell, fires relevant events and updates the castCommandTick field.
    void tick();

    /// Returns whether the player is currently casting a spell via the cast command.
    boolean isCommandCasting();

    /// Returns the spell currently being cast via the cast command, or the none spell if none.
    Spell getSpell();

    /// Returns the spell modifiers used for the current cast command cast.
    SpellModifiers getModifiers();

    /// Returns the number of ticks the current cast command cast has been running for.
    int getCastingTick();

    /// Returns the total duration in ticks of the current cast command cast.
    int getDuration();
}
