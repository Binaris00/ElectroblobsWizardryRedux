package com.electroblob.wizardry.api.content.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;

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

        if(this.getLifeTime() >= 0 && this.tickCount > this.getLifeTime()){
            this.discard();
        }
    }


    // Initialiser methods

    /** Sets the shooter of the projectile to the given caster, positions the projectile at the given caster's eyes and
     * aims it in the direction they are looking with the given speed. */
    public void aim(LivingEntity caster, float speed){
        this.setPos(caster.xo, caster.yo + (double)caster.getEyeHeight() - LAUNCH_Y_OFFSET, caster.zo);
        // This is the standard set of parameters for this method, used by snowballs and ender pearls amongst others.

        this.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0f, speed, 1.0f);
        this.setOwner(caster);
    }

    /** Sets the shooter of the projectile to the given caster, positions the projectile at the given caster's eyes and
     * aims it at the given target with the given speed. The trajectory will be altered slightly by a random amount
     * determined by the aimingError parameter. For reference, skeletons set this to 10 on easy, 6 on normal and 2 on hard
     * difficulty. */
    public void aim(LivingEntity caster, Entity target, float speed, float aimingError){
        this.setOwner(caster);

        this.yo = caster.xo + (double)caster.getEyeHeight() - LAUNCH_Y_OFFSET;
        double dx = target.xo - caster.xo;
        double dy = !this.isNoGravity() ? target.yo + (double)(target.getDimensions(target.getPose()).height / 3.0f) - this.yo
                : target.yo + (double)(target.getDimensions(target.getPose()).height / 2.0f) - this.yo;
        double dz = target.zo - caster.xo;
        double horizontalDistance = Mth.sqrt((float) (dx * dx + dz * dz));

        if(horizontalDistance >= 1.0E-7D){

            double dxNormalised = dx / horizontalDistance;
            double dzNormalised = dz / horizontalDistance;
            this.setPos(caster.xo + dxNormalised, this.yo, caster.zo + dzNormalised);

            // Depends on the horizontal distance between the two entities and accounts for a bullet drop,
            // but, if gravity is ignored, this should be 0 since there is no bullet drop.
            float bulletDropCompensation = !this.isNoGravity() ? (float)horizontalDistance * 0.2f : 0;

            // It turns out that this method normalizes the input (x, y, z) anyway
            this.shoot(dx, dy + (double)bulletDropCompensation, dz, speed, aimingError);
        }
    }


    /**
     * Returns the seeking strength of this projectile, or the maximum distance from a target the projectile can be
     * heading for that will make it curve towards that target. By default, this is 2 if the caster is wearing a ring
     * of attraction, otherwise it is 0.
     */
    public float getSeekingStrength(){
        // TODO: Ring of attraction here...
        // return getOwner() instanceof PlayerEntity && ItemArtefact.isArtefactActive((EntityPlayer)getThrower(), WizardryItems.ring_seeking) ? 2 : 0;
        return 0;
    }


    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        damageMultiplier = compoundTag.getFloat("damageMultiplier");
    }

    @Override
    public boolean save(CompoundTag compoundTag) {
        compoundTag.putFloat("damageMultiplier", damageMultiplier);
        return super.save(compoundTag);
    }

    public int getLifeTime() {
        return -1;
    }

}
