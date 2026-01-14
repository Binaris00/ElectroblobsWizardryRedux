package com.binaris.wizardry.api.content.data;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;

public interface CastCommandData {
    /**
     * Starts casting the given spell with the given modifiers.
     */
    void startCastingContinuousSpell(Spell spell, SpellModifiers modifiers, int duration);

    /**
     * Stops casting the current spell.
     */
    void stopCastingContinuousSpell();

    /**
     * Casts the current continuous spell, fires relevant events and updates the castCommandTick field.
     */
    void tick();

    /**
     * Returns whether the player is currently casting a spell via the cast command.
     */
    boolean isCommandCasting();
}
