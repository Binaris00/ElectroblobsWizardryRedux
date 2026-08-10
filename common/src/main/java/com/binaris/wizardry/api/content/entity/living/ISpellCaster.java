package com.binaris.wizardry.api.content.entity.living;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/// Marks an entity as capable of casting spells, providing access to its spell list, modifiers,
/// and continuous-spell state.
///
/// Implemented primarily by {@code AbstractWizard} (and subclasses) for NPC spellcasting. AI goals
/// such as {@code AttackSpellGoal} and {@code AttackSpellBasicGoal} operate on any {@code Mob & ISpellCaster}.
/// The continuous-spell machinery — a currently ticking spell plus a tick counter — is synced to
/// clients via {@code NPCSpellCastS2C} packets so that particle effects and animations render correctly.
/// {@code EntityUtil.isCasting()} uses the continuous-spell getter for quick checks.
public interface ISpellCaster {
    /// Returns the list of spells this caster knows.
    ///
    /// AI goals copy this list, filter by validity against the current target, and select one to cast.
    /// The returned list must never be null; implementors typically store it as a mutable field populated
    /// from the entity's NBT.
    ///
    /// @return a non-null list of spells available to this caster.
    @NotNull
    List<Spell> getSpells();

    /// Returns the spell modifiers used when constructing an {@code EntityCastContext} for this caster.
    ///
    /// The returned modifiers are applied as a baseline multiplier set (potency, cost, cooldown, blast,
    /// etc.) before attribute-based and mob-effect-based adjustments are layered on top by the spell
    /// cast event listeners. The default implementation supplies a fresh, unmodified {@code SpellModifiers}.
    ///
    /// @return a non-null set of spell modifiers; the default is empty (all values at 1.0).
    @NotNull
    default SpellModifiers getModifiers() {
        return new SpellModifiers();
    }

    /// Returns the currently active continuous spell, or {@code Spells.NONE} if no spell is ticking.
    ///
    /// Used by AI goals during their tick logic to call {@code spell.cast(ctx)} each tick, and by
    /// the client-side continuous-spell handler to decide whether to render particles. Defaults to
    /// {@code Spells.NONE} (no continuous spell active).
    ///
    /// @return the active continuous spell; never null.
    @NotNull
    default Spell getContinuousSpell() {
        return Spells.NONE;
    }

    /// Sets the continuous spell that should tick each update cycle.
    ///
    /// When starting a new continuous spell, AI goals call this followed by a network sync via
    /// {@code NPCSpellCastS2C}. When ending one, they pass {@code Spells.NONE} and reset the counter
    /// to zero. The default implementation is a no-op, relying on subclasses to store the value.
    ///
    /// @param spell the spell to mark as active, or {@code Spells.NONE} to clear.
    default void setContinuousSpell(Spell spell) {

    }

    /// Returns the current tick counter for the active continuous spell.
    ///
    /// Incremented by AI goals each tick the continuous spell fires and used by the client-side handler
    /// to determine how long to keep rendering the spell's effects. Defaults to 0 (no continuous spell
    /// in progress).
    ///
    /// @return the current tick count for the active continuous spell.
    default int getSpellCounter() {
        return 0;
    }

    /// Sets the tick counter for the active continuous spell.
    ///
    /// AI goals write the current tick during each update and reset it to 0 when the spell ends.
    /// The default is a no-op, relying on subclasses to store the count.
    ///
    /// @param count the new tick count, or 0 to reset.
    default void setSpellCounter(int count) {

    }

    /// Returns the aiming error in degrees for an NPC projectile cast, based on difficulty.
    ///
    /// Delegates to {@code EntityUtil.getDefaultAimingError(difficulty)}: 5 on Easy, 3 on Normal,
    /// 0 on Hard, and 4 on Peaceful. Callers (typically {@code ProjectileSpell} and {@code ArrowSpell})
    /// currently bypass this method and call the static utility directly.
    ///
    /// @param difficulty the current world difficulty level.
    ///
    /// @return the aiming error in degrees for an NPC projectile.
    default int getAimingError(Difficulty difficulty) {
        return EntityUtil.getDefaultAimingError(difficulty);
    }
}
