package com.electroblob.wizardry.api.content.data;

import com.electroblob.wizardry.content.entity.goal.MinionFollowOwnerGoal;
import com.electroblob.wizardry.content.entity.goal.MinionOwnerHurtByTargetGoal;
import com.electroblob.wizardry.content.entity.goal.MinionOwnerHurtTargetGoal;
import com.electroblob.wizardry.core.mixin.accessor.MobGoalsAccessor;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents the data associated with a minion entity in the game. A minion is a mob that is summoned from any spell or
 * ability related to minions. In order to create a very quick and easy implementation of a minion we use this interface
 * to store all the necessary data and functionality related to minions, this goes such as lifetime, ownership, and any
 * needed parts to remove from the original mob's goals and add the minion specific ones.
 * <p>
 * You don't need to implement this interface yourself, as the mod provides default implementations for all mobs that
 * are designated as minions. Instead, you can use {@link com.electroblob.wizardry.content.spell.abstr.MinionSpell MinionSpell}
 * and related classes to summon and manage minions without worrying about the underlying data handling.
 */
public interface MinionData {
    /**
     * Gets the mob that this minion data is associated with. Normally you won't need to use this method directly.
     *
     * @return the mob provider
     */
    Mob getProvider();

    /**
     * Clears all existing goals from the minion's target selectors and optionally the goal selectors, then adds the
     * standard minion goals to follow/defend its owner as appropriate.
     */
    default void updateGoals() {
        ((MobGoalsAccessor) getProvider()).getTargetSelector().removeAllGoals((goal) -> true);
        if (shouldDeleteGoals()) ((MobGoalsAccessor) getProvider()).getGoalSelector().removeAllGoals((goal) -> true);

        ((MobGoalsAccessor) getProvider()).getTargetSelector().addGoal(1, new MinionOwnerHurtByTargetGoal(getProvider()));
        ((MobGoalsAccessor) getProvider()).getTargetSelector().addGoal(1, new MinionOwnerHurtTargetGoal(getProvider()));
        if (shouldFollowOwner())
            ((MobGoalsAccessor) getProvider()).getGoalSelector().addGoal(2, new MinionFollowOwnerGoal(getProvider()));

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

    /**
     * Determines whether the minion's goals should be deleted when the minion is created.
     *
     * @return true if the goals should be deleted, false otherwise
     */
    boolean shouldDeleteGoals();

    /**
     * Sets whether the minion's goals should be deleted when the minion is created.
     *
     * @param shouldDeleteGoals true to delete the goals, false otherwise
     */
    void setShouldDeleteGoals(boolean shouldDeleteGoals);

    /**
     * Determines whether the minion should follow its owner.
     *
     * @return true if the minion should follow its owner, false otherwise
     */
    boolean shouldFollowOwner();

    /**
     * Sets whether the minion should follow its owner.
     *
     * @param shouldFollowOwner true if the minion should follow its owner, false otherwise
     */
    void setShouldFollowOwner(boolean shouldFollowOwner);

    /**
     * Gets the UUID of the owner of the minion.
     *
     * @return the owner's UUID
     */
    @Nullable UUID getOwnerUUID();

    /**
     * Sets the UUID of the owner of the minion.
     *
     * @param ownerUUID the owner's UUID
     */
    void setOwnerUUID(UUID ownerUUID);

    /**
     * Gets the owner entity of the minion.
     *
     * @return the owner entity
     */
    Player getOwner();

    /**
     * Sets whether the minion's original goals should be restarted, this is used when the minion's tick method is
     * called after it has been loaded into the world.
     * <p>
     * <em>Note:</em> This method is intended for internal use only and should not be called directly or overridden.
     *
     * @param shouldRestartGoals true to restart the goals, false otherwise
     */
    @ApiStatus.Internal
    void markGoalRestart(boolean shouldRestartGoals);

    /**
     * Determines whether the minion's original goals should be restarted, this is used when the minion's tick method is
     * called after it has been loaded into the world.
     * <p>
     * <em>Note:</em> This method is intended for internal use only and should not be called directly or overridden.
     *
     * @return true if the goals should be restarted, false otherwise
     */
    @ApiStatus.Internal
    boolean goalRestart();
}
