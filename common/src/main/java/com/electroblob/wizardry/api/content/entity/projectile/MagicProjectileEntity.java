package com.electroblob.wizardry.api.content.entity.projectile;

import com.electroblob.wizardry.api.content.util.RayTracer;
import com.electroblob.wizardry.core.AllyDesignation;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class MagicProjectileEntity extends ThrowableItemProjectile {
    public static final double LAUNCH_Y_OFFSET = 0.1;
    public static final int SEEKING_TIME = 15;
    public float damageMultiplier = 1.0f;

    public MagicProjectileEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MagicProjectileEntity(EntityType<? extends ThrowableItemProjectile> entityType, LivingEntity livingEntity, Level level) {
        super(entityType, livingEntity, level);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getLifeTime() >= 0 && this.tickCount > this.getLifeTime()) {
            this.discard();
        }

        if (getSeekingStrength() <= 0) return;
        HitResult hit = RayTracer.rayTrace(level(), this, this.position(), this.position().add(this.getDeltaMovement().scale(SEEKING_TIME)), getSeekingStrength(), false, LivingEntity.class, RayTracer.ignoreEntityFilter(null));

        if (hit instanceof EntityHitResult entityHit && getOwner() instanceof LivingEntity owner && entityHit.getEntity() instanceof LivingEntity entity) {
            if (AllyDesignation.isValidTarget(owner, entity)) {
                Vec3 direction = new Vec3(entity.xo, entity.yo + entity.getDimensions(entity.getPose()).height / 2, entity.zo).subtract(this.position()).normalize().scale(this.getDeltaMovement().length());
                this.setDeltaMovement(this.getDeltaMovement().add(direction.subtract(this.getDeltaMovement()).scale(2.0 / SEEKING_TIME)));
            }
        }
    }

    /**
     * Sets the shooter of the projectile to the given caster, positions the projectile at the given caster's eyes and
     * aims it in the direction they are looking with the given speed.
     */
    public void aim(LivingEntity caster, float speed) {
        this.setPos(caster.xo, caster.yo + (double) caster.getEyeHeight() - LAUNCH_Y_OFFSET, caster.zo);
        this.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0f, speed, 1.0f);
        this.setOwner(caster);
    }


    /**
     * Returns the seeking strength of this projectile, or the maximum distance from a target the projectile can be
     * heading for that will make it curve towards that target. By default, this is 2 if the caster is wearing a ring
     * of attraction, otherwise it is 0. You can override this method to give different behaviour for different projectiles
     * and also make it depend on other factors such as the caster's equipment.
     */
    public float getSeekingStrength() {
        return getOwner() instanceof Player player && EBAccessoriesIntegration.isEquipped(player, EBItems.RING_SEEKING.get()) ? 2 : 0;
    }

    /**
     * Sets the shooter of the projectile to the given caster, positions the projectile at the given caster's eyes and
     * aims it at the given target with the given speed. The trajectory will be altered slightly by a random amount
     * determined by {@code aimingError} parameter. For reference, skeletons set this to 10 on easy, 6 on normal and 2 on hard
     * difficulty.
     */
    public void aim(LivingEntity caster, Entity target, float speed, float aimingError) {
        this.setOwner(caster);

        this.yo = caster.yo + (double) caster.getDimensions(caster.getPose()).height * 0.85F - LAUNCH_Y_OFFSET;
        double dx = target.xo - caster.xo;
        double dy = !this.isNoGravity() ?
                target.yo + (double) (target.getDimensions(caster.getPose()).height / 3.0f) - this.yo
                : target.yo + (double) (target.getDimensions(caster.getPose()).height / 2.0f) - this.yo;
        double dz = target.zo - caster.zo;
        double horizontalDistance = Mth.sqrt((float) (dx * dx + dz * dz));

        if (horizontalDistance >= 1.0E-7D) {
            float yaw = (float) (Math.atan2(dz, dx) * 180.0d / Math.PI) - 90.0f;
            float pitch = (float) (-(Math.atan2(dy, horizontalDistance) * 180.0d / Math.PI));
            double dxNormalised = dx / horizontalDistance;
            double dzNormalised = dz / horizontalDistance;
            this.absMoveTo(caster.xo + dxNormalised, this.yo, caster.zo + dzNormalised, yaw, pitch);

            float bulletDropCompensation = !this.isNoGravity() ? (float) horizontalDistance * 0.2f : 0;
            this.shoot(dx, dy + (double) bulletDropCompensation, dz, speed, aimingError);
        }
    }


    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        damageMultiplier = tag.getFloat("damageMultiplier");
    }

    @Override
    public boolean save(CompoundTag tag) {
        tag.putFloat("damageMultiplier", damageMultiplier);
        return super.save(tag);
    }

    public int getLifeTime() {
        return -1;
    }

}
