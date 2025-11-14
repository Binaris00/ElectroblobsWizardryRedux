package com.electroblob.wizardry.api.content.data;

/**
 * This is the interface that we will use later to interact with the conjure item data!! Loading this as an interface and
 * later implemented by the loaders from "scratch" is basically because we need a similar way to load-change data for
 * Fabric and forge, with this we're trying to create a mask that we will trust that the loaders won't have any problem
 * saving and handling the data.
 *
 * @see com.electroblob.wizardry.core.mixin.ConjureMixin
 */
public interface ConjureData {
    /**
     * Gets the absolute game time when this conjured item should expire.
     *
     * @return the expire time in game ticks, or -1 if not set
     */
    long getExpireTime();

    /**
     * Sets the absolute game time when this conjured item should expire.
     *
     * @param expireTime the expire time in game ticks
     */
    void setExpireTime(long expireTime);

    /**
     * Gets the duration this item was conjured for (for display purposes).
     *
     * @return the duration in ticks
     */
    int getDuration();

    /**
     * Sets the duration this item was conjured for (for display purposes).
     *
     * @param duration the duration in ticks
     */
    void setDuration(int duration);

    /**
     * Checks whether this item was conjured.
     *
     * @return true if the item was conjured
     */
    boolean isSummoned();

    /**
     * Sets whether this item was conjured.
     *
     * @param summoned true if the item was conjured
     */
    void setSummoned(boolean summoned);

    /**
     * Checks if this conjured item has expired based on current game time. -1 expire time means it never expires.
     *
     * @param currentGameTime the current game time in ticks
     * @return true if the item should expire
     */
    default boolean hasExpired(long currentGameTime) {
        return isSummoned() && getExpireTime() >= 0 && currentGameTime >= getExpireTime();
    }

    /**
     * Gets the remaining lifetime in ticks.
     *
     * @param currentGameTime the current game time in ticks
     * @return remaining ticks, or 0 if expired
     */
    default int getRemainingLifetime(long currentGameTime) {
        if (!isSummoned() || getExpireTime() <= 0) return 0;
        long remaining = getExpireTime() - currentGameTime;
        return remaining > 0 ? (int) remaining : 0;
    }
}
