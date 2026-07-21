package com.binaris.wizardry.api.content.entity.projectile;

import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.core.integrations.ArtifactChannel;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/// Extension of the normal projectiles and provide extra utilities to interact with Electroblobs Wizardry
///
/// - Included the [#getLifeTime] method to define the lifetime of the projectile and affects the velocity of the projectile when
///   it is launched by the [com.binaris.wizardry.content.spell.abstr.ProjectileSpell]
/// - You can use the [#onHitTargetExtraEffects] method to apply extra effects to the entity that was hit by the projectile.
/// - This entity already provides the ally check and damage calculation.
/// - Controls the sound and particles of the projectile.
public abstract class MagicProjectileEntity extends ThrowableProjectile {
    private static final byte HIT_ENTITY_EVENT_ID = 3;
    private static final byte HIT_BLOCK_EVENT_ID = 4;
    public static final double LAUNCH_Y_OFFSET = 0.3;
    public static final float FORWARD_OFFSET = 0.8f;
    public static final int SEEKING_TIME = 15;
    public float damageMultiplier = 1.0f;

    public MagicProjectileEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MagicProjectileEntity(EntityType<? extends ThrowableProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
    }

    public MagicProjectileEntity(EntityType<? extends ThrowableProjectile> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level);
    }

    /// Returns the lifetime of the projectile, or -1 if it will last until it hits something. This is used to determine when the
    /// projectile should be discarded.
    ///
    /// @return The lifetime of the projectile, or -1 if it will last until it hits something.
    public abstract int getLifeTime();

    /// **This isn't for applying damage to the entity**
    ///
    /// Here you can apply any extra effects to the entity that was hit by the projectile. (e.g. setting fire, poisoning, etc.)
    ///
    /// @param hitResult The result of the hit.
    public void onHitTargetExtraEffects(@NotNull EntityHitResult hitResult) {
    }

    /// Here you can apply any extra damage to the entity that was hit by the projectile. (e.g. setting fire, poisoning, etc.)
    ///
    /// @param hitResult The result of the hit.
    public abstract float getDamage(@NotNull EntityHitResult hitResult);

    /// Returns the damage type of the projectile.
    ///
    /// @param hitResult The result of the hit.
    public abstract ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult);

    /// Returns true if the entity is a valid target for the projectile, by default this is handled by the ally system.
    ///
    /// @param entity The entity to check.
    public boolean isValidTarget(@NotNull Entity entity) {
        Entity owner = this.getOwner() != null ? this.getOwner() : this;
        return AllyDesignation.isValidTarget(owner, entity);
    }

    /// Returns the sound event of the projectile.
    ///
    /// @param result The result of the hit.
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return SoundEvents.EMPTY;
    }

    /// Returns the seeking strength of this projectile, or the maximum distance from a target the projectile can be
    /// heading for that will make it curve towards that target. By default, this is 2 if the caster is wearing a ring
    /// of attraction, otherwise it is 0. You can override this method to give different behavior for different projectiles
    /// and also make it depend on other factors such as the caster's equipment.
    public float getSeekingStrength() {
        return getOwner() instanceof Player player && ArtifactChannel.isEquipped(player, EBItems.RING_SEEKING.get()) ? 2 : 0;
    }

    /// Plays the sound of the projectile.
    ///
    /// @param result The result of the hit.
    protected void playHitSound(HitResult result) {
        this.playSound(this.getSoundEvent(result), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    /// Spawns particles when the projectile hits something.
    ///
    /// @param type The type of hit.
    protected void spawnHitParticles(HitResult.Type type) {
    }

    /**
     * Allow the projectile to pass through mobs intact (the onEntityHit method will still be called
     * and damage will still be applied). Returns false by default.
     */
    public boolean doOverpenetration() {
        return false;
    }

    // -------------------------- PROTECTED METHODS --------------------------

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (!isValidTarget(result.getEntity())) return;

        MagicDamageSource.causeMagicDamage(this, result.getEntity(), getDamage(result) * damageMultiplier, this.getDamageType(result));
        this.onHitTargetExtraEffects(result);

        this.playHitSound(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, HIT_ENTITY_EVENT_ID);
            if (!this.doOverpenetration()) this.discard();
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);
        this.playHitSound(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, HIT_ENTITY_EVENT_ID);
        }

        // Most spell projectiles die on impact with terrain; override if yours should stick/bounce/pierce instead.
        if (!this.level().isClientSide) {
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == HIT_ENTITY_EVENT_ID) {
            this.spawnHitParticles(HitResult.Type.ENTITY);
        } else if (id == HIT_BLOCK_EVENT_ID) {
            this.spawnHitParticles(HitResult.Type.BLOCK);
        } else {
            super.handleEntityEvent(id);
        }
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
            if (!AllyDesignation.isValidTarget(owner, entity)) return;
            Vec3 direction = new Vec3(entity.xo, entity.yo + entity.getDimensions(entity.getPose()).height / 2, entity.zo).subtract(this.position()).normalize().scale(this.getDeltaMovement().length());
            this.setDeltaMovement(this.getDeltaMovement().add(direction.subtract(this.getDeltaMovement()).scale(2.0 / SEEKING_TIME)));
        }
    }

    /// Sets the shooter of the projectile to the given caster, positions the projectile at the given caster's eyes and
    /// aims it in the direction they are looking with the given speed.
    public void aim(LivingEntity caster, float speed) {
        Vec3 lookVector = caster.getLookAngle();

        this.setPos(
                caster.xo + lookVector.x * FORWARD_OFFSET,
                caster.yo + (double) caster.getEyeHeight() - LAUNCH_Y_OFFSET,
                caster.zo + lookVector.z * FORWARD_OFFSET
        );
        this.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0f, speed, 1.0f);
        this.setOwner(caster);
    }

    /// Sets the shooter of the projectile to the given caster, positions the projectile at the given caster's eyes and
    /// aims it at the given target with the given speed. The trajectory will be altered slightly by a random amount
    /// determined by `aimingError` parameter. For reference, skeletons set this to 10 on easy, 6 on normal and 2 on hard
    /// difficulty.
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

    @Override
    protected void defineSynchedData() {

    }
}