package com.binaris.wizardry.content.entity.goal;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Helps a mob to look directly at its target with hard rotations, ignoring any smoothing that would normally be applied.
 * <p>
 * This is useful for spell-casting mobs that need to face their target precisely when casting spells that require direct
 * line of sight. The goal limits the maximum rotation per tick to avoid instant snapping, creating a more natural look.
 * <p>
 * Behavior:
 * <ul>
 *     <li>If the mob has a target, it will rotate to face it directly.</li>
 *     <li>The rotation speed is limited by specified maximum yaw and pitch changes per tick to prevent instant snapping.</li>
 * </ul>
 */
public class HardLookAtTargetGoal extends Goal {
    /** The mob that will use this goal */
    private final PathfinderMob mob;
    /** Maximum rotation per tick, using limit snapping */
    private final float maxYawRotationPerTick;
    /** Maximum pitch rotation per tick, used to limit snapping */
    private final float maxPitchRotationPerTick;
    /** The current target entity */
    private LivingEntity target;

    /**
     * Constructor
     *
     * @param mob                     Mob that will use this goal
     * @param maxYawRotationPerTick   Max horizontal rotation per tick (recommended: 10.0F)
     * @param maxPitchRotationPerTick Max vertical rotation per tick (recommended: 10.0F)
     */
    public HardLookAtTargetGoal(PathfinderMob mob, float maxYawRotationPerTick, float maxPitchRotationPerTick) {
        this.mob = mob;
        this.maxYawRotationPerTick = maxYawRotationPerTick;
        this.maxPitchRotationPerTick = maxPitchRotationPerTick;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        if (this.target == null) return false;
        return this.target.isAlive();
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
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            return;
        }

        double dx = this.target.getX() - this.mob.getX();
        double dy = this.target.getY() + this.target.getEyeHeight() - (this.mob.getY() + this.mob.getEyeHeight());
        double dz = this.target.getZ() - this.mob.getZ();

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);

        float targetYaw = (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
        float targetPitch = (float) (-(Mth.atan2(dy, distanceXZ) * (180.0 / Math.PI)));

        float yawDifference = Mth.wrapDegrees(targetYaw - this.mob.getYRot());
        float pitchDifference = targetPitch - this.mob.getXRot();

        yawDifference = Mth.clamp(yawDifference, -this.maxYawRotationPerTick, this.maxYawRotationPerTick);
        pitchDifference = Mth.clamp(pitchDifference, -this.maxPitchRotationPerTick, this.maxPitchRotationPerTick);

        float newYaw = this.mob.getYRot() + yawDifference;
        float newPitch = this.mob.getXRot() + pitchDifference;

        this.mob.setYRot(newYaw);
        this.mob.setXRot(newPitch);

        this.mob.yHeadRot = newYaw;
        this.mob.yBodyRot = newYaw;
    }
}