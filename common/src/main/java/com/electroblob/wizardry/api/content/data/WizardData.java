package com.electroblob.wizardry.api.content.data;

import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

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
}
