package com.electroblob.wizardry.api.content.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class MagicProjectileEntity extends ThrowableItemProjectile {
    public static final double LAUNCH_Y_OFFSET = 0.1;
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


    public void aim(LivingEntity caster, float speed){
        this.setPos(caster.xo, caster.yo + (double)caster.getEyeHeight() - LAUNCH_Y_OFFSET, caster.zo);
        this.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0f, speed, 1.0f);
        this.setOwner(caster);
    }

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

            float bulletDropCompensation = !this.isNoGravity() ? (float)horizontalDistance * 0.2f : 0;

            this.shoot(dx, dy + (double)bulletDropCompensation, dz, speed, aimingError);
        }
    }


    public float getSeekingStrength(){
        // TODO: Ring of attraction here...
        // return getOwner() instanceof PlayerEntity && ItemArtefact.isArtefactActive((EntityPlayer)getThrower(), WizardryItems.ring_seeking) ? 2 : 0;
        return 0;
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
