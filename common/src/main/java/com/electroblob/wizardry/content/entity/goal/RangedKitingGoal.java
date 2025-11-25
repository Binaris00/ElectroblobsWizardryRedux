package com.electroblob.wizardry.content.entity.goal;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Intelligent kiting goal for ranged attack entities that maintains a safe distance. Just controls the movement directly
 * without using pathfinding (avoiding the look modifications that comes with that), leaving look control to other goals
 * (e.g. {@link HardLookAtTargetGoal}), and attack control to other goals (e.g.. {@link AttackSpellBasicGoal}).
 * <p>
 * Behavior:
 * <ul>
 *     <li>If the target is too close (FLEE_DISTANCE): retreats</li>
 *     <li>If the target is at optimal distance: stops movement</li>
 *     <li>If the target is too far (SAFE_DISTANCE): advances towards it</li>
 * </ul>
 */
public class RangedKitingGoal extends Goal {
    /** The mob that will use this goal */
    private final PathfinderMob mob;

    /** Movement speed when moving towards or away from the target */
    private final double moveSpeed;

    /** Optimal distance to maintain from the target, if beyond which the mob will advance */
    private final double safeDistance;

    /** Minimum distance before the mob starts to retreat, if closer than which it will retreat */
    private final double fleeDistance;

    /** Maximum range to follow the target, beyond which the goal will not activate */
    private final double followRange;

    /** The current target entity */
    private LivingEntity target;

    /**
     * General constructor, allowing full configuration for the kiting behavior.
     *
     * @param mob          Mob that will use this goal
     * @param moveSpeed    Movement speed when approaching or retreating
     * @param safeDistance Distance to maintain from the target
     * @param fleeDistance Minimum distance before retreating
     * @param followRange  Maximum range to follow the target
     */
    public RangedKitingGoal(PathfinderMob mob, double moveSpeed, double safeDistance, double fleeDistance, double followRange) {
        this.mob = mob;
        this.moveSpeed = moveSpeed;
        this.safeDistance = safeDistance;
        this.fleeDistance = fleeDistance;
        this.followRange = followRange;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    /**
     * Simplified constructor with default distance values.
     *
     * @param mob       Mob that will use this goal
     * @param moveSpeed Movement speed when approaching or retreating
     */
    public RangedKitingGoal(PathfinderMob mob, double moveSpeed) {
        this(mob, moveSpeed, 12.0, 6.0, 32.0);
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        if (this.target == null) return false;
        if (!this.target.isAlive()) return false;

        double distanceToTarget = this.mob.distanceToSqr(this.target);
        double followRangeSqr = this.followRange * this.followRange;

        return distanceToTarget <= followRangeSqr;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }

        double distanceToTarget = this.mob.distanceTo(this.target);

        if (distanceToTarget < this.fleeDistance) {
            // Too close distance: RETREAT FROM TARGET
            this.retreatFromTarget();
        } else if (distanceToTarget > this.safeDistance) {
            // Too far distance: GO TOWARDS TARGET
            this.advanceToTarget();
        } else {
            // Optimal distance: STOP MOVING
            this.stopMovement();
        }
    }

    /** Goes away from the target applying velocity directly backwards */
    private void retreatFromTarget() {
        if (this.target == null) return;

        Vec3 mobPos = this.mob.position();
        Vec3 targetPos = this.target.position();

        double dx = mobPos.x - targetPos.x;
        double dz = mobPos.z - targetPos.z;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0) {
            dx /= distance;
            dz /= distance;
        }

        this.mob.setDeltaMovement(
                this.mob.getDeltaMovement().x() + dx * this.moveSpeed * 0.1,
                this.mob.getDeltaMovement().y(),
                this.mob.getDeltaMovement().z() + dz * this.moveSpeed * 0.1
        );
    }

    /** Moves towards the target applying velocity directly forwards */
    private void advanceToTarget() {
        if (this.target == null) return;

        Vec3 mobPos = this.mob.position();
        Vec3 targetPos = this.target.position();

        double dx = targetPos.x - mobPos.x;
        double dz = targetPos.z - mobPos.z;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0) {
            dx /= distance;
            dz /= distance;
        }

        this.mob.setDeltaMovement(
                this.mob.getDeltaMovement().x() + dx * this.moveSpeed * 0.1,
                this.mob.getDeltaMovement().y(),
                this.mob.getDeltaMovement().z() + dz * this.moveSpeed * 0.1
        );
    }

    /** Gradually reduces horizontal movement to zero */
    private void stopMovement() {
        if (this.target == null) return;

        this.mob.setDeltaMovement(
                this.mob.getDeltaMovement().x() * 0.9,
                this.mob.getDeltaMovement().y(),
                this.mob.getDeltaMovement().z() * 0.9
        );
    }
}