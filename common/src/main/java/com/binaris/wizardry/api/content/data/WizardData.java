package com.binaris.wizardry.api.content.data;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import net.minecraft.world.entity.player.Player;

import java.util.AbstractMap;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public interface WizardData {
    /**
     * Sets the highest spell tier reached by the player, use this with caution as it can only be increased.
     *
     * @param tier The SpellTier to set as reached.
     */
    void setTierReached(SpellTier tier);

    /**
     * Checks if the player has reached the specified spell tier.
     *
     * @param tier The SpellTier to check.
     * @return true if the player has reached the specified tier, false otherwise.
     */
    boolean hasReachedTier(SpellTier tier);

    /**
     * Toggles the ally status of a friend for the original player.
     *
     * @param friend The player whose ally status is being toggled.
     * @return true if the friend was added as an ally, false if they were removed.
     */
    boolean toggleAlly(Player friend);

    /**
     * Checks if the given player is an ally of the original player.
     * This checks both the list of ally UUIDs and the original player's team.
     *
     * @param ally the Player to check
     * @return true if the player is an ally, false otherwise
     */
    boolean isPlayerAlly(Player ally);

    /**
     * Checks if the given player UUID is an ally of the original player.
     * This checks both the list of ally UUIDs and the original player's team.
     *
     * @param playerUUID the UUID of the player to check
     * @return true if the player is an ally, false otherwise
     */
    boolean isPlayerAlly(UUID playerUUID);

    /**
     * Gets the spell modifiers associated with the player.
     *
     * @return The SpellModifiers for the player.
     */
    SpellModifiers getSpellModifiers();

    void setSpellModifiers(SpellModifiers modifiers);

    /**
     * Records a spell cast for tracking recent spells by the player, used especially to avoid spell spam for quick
     * wand level progression and similar things.
     *
     * @param spell     The Spell that was just cast.
     * @param timestamp The time at which the spell was cast, in game ticks.
     */
    void trackRecentSpell(Spell spell, long timestamp);

    /**
     * Counts how many times a spell has been cast recently by the player. It could be 0 if it hasn't been cast recently.
     *
     * @param spell The Spell to count recent casts of.
     * @return The number of times the spell has been cast recently.
     */
    int countRecentCasts(Spell spell);

    /**
     * Removes recent spell casts that match the given predicate, allowing for custom filtering of which entries to remove.
     *
     * @param predicate A Predicate that tests AbstractMap.SimpleEntry&lt;Spell, Long&gt; objects representing
     *                  recent spell casts. If the predicate returns true for an entry, that entry will be removed.
     */
    void removeRecentCasts(Predicate<AbstractMap.SimpleEntry<Spell, Long>> predicate);

    /**
     * Gets a Random instance associated with this WizardData.
     *
     * @return A Random instance.
     */
    Random getRandom();
}