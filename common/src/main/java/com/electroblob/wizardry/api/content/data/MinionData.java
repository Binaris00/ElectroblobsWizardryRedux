package com.electroblob.wizardry.api.content.data;

import com.electroblob.wizardry.core.mixin.accessor.MobGoalsAccessor;
import net.minecraft.world.entity.Mob;

public interface MinionData {
    /**
     * Gets the mob that this minion data is associated with.
     *
     * @return the mob provider
     */
    Mob getProvider();

    /**
     * Clears all existing goals from the minion's goal and target selectors.
     */
    default void updateGoals(){
        ((MobGoalsAccessor) getProvider()).getTargetSelector().removeAllGoals((goal) -> true);
        ((MobGoalsAccessor) getProvider()).getGoalSelector().removeAllGoals((goal) -> true);
    }

    /**
     * Called every tick to update the minion's state, including checking its lifetime and spawning particles.
     */
    void tick();

    /**
     * Gets the lifetime of the minion in ticks.
     *
     * @return the lifetime of the minion
     */
    int getLifetime();

    /**
     * Sets the lifetime of the minion in ticks.
     *
     * @param lifetime the new lifetime of the minion
     */
    void setLifetime(int lifetime);

    /**
     * Decreases the minion's lifetime by one tick.
     */
    boolean isSummoned();

    /**
     * Sets whether the minion was summoned by a player.
     *
     * @param summoned true if the minion was summoned, false otherwise
     */
    void setSummoned(boolean summoned);
}
