package com.electroblob.wizardry.api.content.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Map;

/**
 * Interface for tracking temporary enchantments on items, such as those applied by imbuement spells.
 *
 * @see com.electroblob.wizardry.content.spell.fire.FlamingWeapon
 */
public interface ImbuementEnchantData {

    /**
     * Adds a temporary enchantment with expiration time. This only adds tracking; the actual enchantment should be applied
     * to the item separately.
     *
     * @param enchant the Enchantment to track as temporary
     * @param expireTime the absolute game time when this enchantment expires
     */
    void addImbuement(Enchantment enchant, long expireTime);

    /**
     * Gets all temporary enchantments and their expiration times.
     * Returns a map of enchantment location -> expiration time
     *
     * @return map of enchantment ResourceLocations to expiration times
     */
    Map<ResourceLocation, Long> getImbuements();

    /**
     * Removes a temporary enchantment from tracking.
     *
     * @param enchant the enchantment to remove
     */
    void removeImbuement(Enchantment enchant);

    /**
     * Checks if an enchantment is tracked as temporary.
     *
     * @param enchant the Enchantment to check
     * @return true if the enchantment is temporary
     */
    boolean isImbuement(Enchantment enchant);

    /**
     * Gets the expiration time for a specific enchantment.
     *
     * @param enchantment the enchantment
     * @return the expiration time in game ticks, or -1 if not found
     */
    long getExpirationTime(Enchantment enchantment);

    /**
     * Gets the remaining time for an enchantment in ticks.
     *
     * @param enchantment the enchantment
     * @param currentGameTime the current game time
     * @return remaining ticks, or 0 if expired/not found
     */
    default int getRemainingTime(Enchantment enchantment, long currentGameTime) {
        long expireTime = getExpirationTime(enchantment);
        if (expireTime <= 0) return 0;
        long remaining = expireTime - currentGameTime;
        return remaining > 0 ? (int) remaining : 0;
    }
}