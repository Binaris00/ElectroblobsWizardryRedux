package com.electroblob.wizardry.api.content.data;

/**
 * Interface for managing temporary enchantments on items. This system allows any enchantment
 * (vanilla or modded) to be applied temporarily to items, with automatic removal after a set duration.
 * <p>
 * This is similar to {@link ConjureData} but for enchantments instead of conjured items.
 * The data is stored directly in the ItemStack's NBT and tracked in player data for expiration.
 *
 * @see ConjureData
 */
public interface TemporaryEnchantmentData {
    /**
     * Gets the absolute game time when this temporary enchantment should expire.
     *
     * @return the expire time in game ticks, or -1 if not set or not temporary
     */
    long getExpireTime();

    /**
     * Sets the absolute game time when this temporary enchantment should expire.
     *
     * @param expireTime the expire time in game ticks
     */
    void setExpireTime(long expireTime);

    /**
     * Gets the duration this enchantment was applied for (for display purposes).
     *
     * @return the duration in ticks
     */
    int getDuration();

    /**
     * Sets the duration this enchantment was applied for (for display purposes).
     *
     * @param duration the duration in ticks
     */
    void setDuration(int duration);

    /**
     * Checks whether this item has any temporary enchantments.
     *
     * @return true if the item has temporary enchantments
     */
    boolean hasTemporaryEnchantment();

    /**
     * Sets whether this item has temporary enchantments.
     *
     * @param hasTemporary true if the item has temporary enchantments
     */
    void setHasTemporaryEnchantment(boolean hasTemporary);

    /**
     * Checks if the temporary enchantment has expired based on current game time.
     * -1 expire time means it never expires.
     *
     * @param currentGameTime the current game time in ticks
     * @return true if the enchantment should expire
     */
    default boolean hasExpired(long currentGameTime) {
        return hasTemporaryEnchantment() && getExpireTime() >= 0 && currentGameTime >= getExpireTime();
    }

    /**
     * Gets the remaining lifetime in ticks.
     *
     * @param currentGameTime the current game time in ticks
     * @return remaining ticks, or 0 if expired
     */
    default int getRemainingLifetime(long currentGameTime) {
        if (!hasTemporaryEnchantment() || getExpireTime() <= 0) return 0;
        long remaining = getExpireTime() - currentGameTime;
        return remaining > 0 ? (int) remaining : 0;
    }
}
