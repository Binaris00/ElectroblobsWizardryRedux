package com.electroblob.wizardry.api.content.entity.projectile;


import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.util.RayTracer;
import com.electroblob.wizardry.client.renderer.entity.MagicArrowRenderer;
import com.electroblob.wizardry.content.spell.abstr.ArrowSpell;
import com.electroblob.wizardry.core.AllyDesignation;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * This class uses the base code from {@link AbstractArrow} and adds some methods to make it easier to use with the mod.
 * This is used as a base class for all the magic arrows spelled by the {@link ArrowSpell} class.
 * <p>
 * The methods {@link #aim(LivingEntity, float)} and {@link #aim(LivingEntity, Entity, float, float)} are used to set
 * the shooter of the projectile and aim it in the direction they are looking. (Originally copied from Ebwizardry 1.12.2)
 * <p>
 * To register the renderer for this entity, use {@link MagicArrowRenderer} and register the respective texture in
 * {@link  MagicArrowEntity#getTexture()}, if you want to use another renderer, you can do so accordingly, but you will
 * have to handle the texture yourself.
 */
public abstract class MagicArrowEntity extends AbstractArrow {
    public static final double LAUNCH_Y_OFFSET = 0.1;
    public static final int SEEKING_TIME = 15;
    public float damageMultiplier = 1.0f;
    protected int ticksInGround;
    protected int ticksInAir;

    public MagicArrowEntity(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    /**
     * Sets the shooter of the projectile to the given caster, positions the projectile at the given caster's eyes and
     * aims it in the direction they are looking with the given speed.
     */
    public void aim(LivingEntity caster, float speed) {
        if (getOwner() == null) this.setOwner(caster);

        this.absMoveTo(caster.getX(), caster.getY() + caster.getDimensions(caster.getPose()).height - LAUNCH_Y_OFFSET, caster.getZ()
                , caster.getYRot(), caster.getXRot());

        this.xo -= Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * 0.16F;
        this.yo -= 0.10000000149011612D;
        this.zo -= Mth.cos(this.getYRot() / 180.0F * (float) Math.PI) * 0.16F;

        this.setPos(xo, yo, zo);

        double motionX = -Mth.sin(this.getYRot() / 180.0F * (float) Math.PI)
                * Mth.cos(this.getXRot() / 180.0F * (float) Math.PI);
        double motionY = -Mth.sin(this.getXRot() / 180.0F * (float) Math.PI);
        double motionZ = Mth.cos(this.getYRot() / 180.0F * (float) Math.PI)
                * Mth.cos(this.getXRot() / 180.0F * (float) Math.PI);

        this.shoot(motionX, motionY, motionZ, speed * 1.5F, 1.0F);

    }

    public void aim(LivingEntity caster, Entity target, float speed, float aimingError) {
        if (getOwner() == null) this.setOwner(caster);

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
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        if (!(hitResult.getEntity() instanceof LivingEntity target)) return;
        if (EBMagicDamageSource.isEntityImmune(getDamageType(), target)) {
            this.discard();
            return;
        }

        // Damage stuff
        DamageSource damageSource = getOwner() == null ? EBMagicDamageSource.causeDirectMagicDamage(this, getDamageType())
                : EBMagicDamageSource.causeIndirectMagicDamage(this, this.getOwner(), getDamageType());

        target.hurt(damageSource, (float) getDamage() * this.damageMultiplier);

        // Knockback and post effects
        if (this.getKnockback() > 0) {
            double knockback = Math.max(0.0, 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            Vec3 vecKnockback = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale((double) this.getKnockback() * 0.6 * knockback);
            if (vecKnockback.lengthSqr() > 0.0) {
                target.push(vecKnockback.x, 0.1, vecKnockback.z);
            }
        }

        if (!this.level().isClientSide && getOwner() instanceof LivingEntity arrowOwner) {
            EnchantmentHelper.doPostHurtEffects(target, arrowOwner);
            EnchantmentHelper.doPostDamageEffects(arrowOwner, target);
        }
        this.discard();
    }


    // ======================= NBT =======================

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ticksInAir", ticksInAir);
        compound.putInt("ticksInGround", ticksInGround);
        compound.putFloat("damageMultiplier", damageMultiplier);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        ticksInAir = compound.getInt("ticksInAir");
        ticksInGround = compound.getInt("ticksInGround");
        damageMultiplier = compound.getFloat("damageMultiplier");
    }

    // ======================= Property getters (to be overridden by subclasses) =======================

    /**
     * Subclasses must override this to set their own base damage.
     */
    public abstract double getDamage();

    /**
     * Returns the maximum flight time in ticks before this projectile disappears, or -1 if it can continue indefinitely
     * until it hits something. This should be constant.
     */
    public abstract int getLifetime();

    /**
     * This method is used to get the damage type of the magic arrow. You must override this and return a valid
     * {@code ResourceKey<DamageType>} for your magic arrow's damage type.
     */
    public ResourceKey<DamageType> getDamageType() {
        return EBDamageSources.SORCERY;
    }

    /**
     * This method is used to get the texture for the magic arrow.
     * The texture is represented by a ResourceLocation object. You must return a valid ResourceLocation or implement
     * a different renderer of your own accordingly.
     */
    public abstract ResourceLocation getTexture();

    /**
     * Override this to disable deceleration (generally speaking, this isn't noticeable unless gravity is turned off).
     * Returns true by default.
     */
    @Deprecated
    public boolean doDeceleration() {
        return true;
    }

    /**
     * Override this to allow the projectile to pass through mobs intact (the onEntityHit method will still be called
     * and damage will still be applied). Returns false by default.
     */
    @Deprecated
    public boolean doOverpenetration() {
        return false;
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

    @Override
    public void tick() {
        super.tick();

        if (getLifetime() >= 0 && this.tickCount >= getLifetime()) {
            this.discard();
        }

        if (inGround) {
            ticksInGround++;
            tickInGround();
        } else {
            ticksInAir++;
            ticksInAir();
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

    public void tickInGround() {
    }

    public void ticksInAir() {
    }

    @Override
    protected boolean tryPickup(@NotNull Player player) {
        return false;
    }
}
