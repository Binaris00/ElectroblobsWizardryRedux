package com.electroblob.wizardry.common.content.entity.projectile;


import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FlamecatcherArrow extends MagicArrowEntity {
    public static final float SPEED = 3;
    public FlamecatcherArrow(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }
    public FlamecatcherArrow(Level world) {
        super(EBEntities.FLAME_CATCHER_ARROW.get(), world);
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/flamecatcher_arrow.png");
    }

    @Override
    public double getDamage() {
        return 16;
    }

    @Override
    public int getLifetime() {
        return (int) (10 / SPEED);
    }

    @Override
    public boolean doDeceleration() {
        return false;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult hitResult) {
        if(hitResult.getEntity() instanceof LivingEntity livingEntity) {
            if(!EBMagicDamageSource.isEntityImmune(EBDamageSources.FIRE, livingEntity)) livingEntity.setSecondsOnFire(15);
            this.playSound(EBSounds.ENTITY_FLAMECATCHER_ARROW_HIT.get(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            if(this.level().isClientSide) {
                ParticleBuilder.create(EBParticles.FLASH).pos(getX(), getY(), getZ()).color(0xff6d00).spawn(level());
            }
        }

        super.onHitEntity(hitResult);
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if(this.level().isClientSide){
            Vec3 vec = blockHitResult.getLocation().add(new Vec3(blockHitResult.getDirection().getStepX(),
                    blockHitResult.getDirection().getStepY(), blockHitResult.getDirection().getStepZ()).scale(0.15));
            ParticleBuilder.create(EBParticles.FLASH).pos(vec).color(0xff6d00).fade(0.85f, 0.5f, 0.8f).spawn(level());
        }
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void ticksInAir() {
        if(this.level().isClientSide) {
            ParticleBuilder.create(EBParticles.MAGIC_FIRE, level().getRandom(), this.getX(), this.getY(), this.getZ(), 0.03, false)
                    .time(20 + this.random.nextInt(10)).spawn(level());

            if(this.getLifetime() > 1) {
                double x = this.getX() - this.getDeltaMovement().x / 2;
                double y = this.getY() - this.getDeltaMovement().y / 2;
                double z = this.getZ() - this.getDeltaMovement().z / 2;
                ParticleBuilder.create(EBParticles.MAGIC_FIRE, level().getRandom(), x, y, z, 0.03, false)
                        .time(20 + this.random.nextInt(10)).spawn(level());
            }
        }
        super.ticksInAir();
    }

    @Override
    public ResourceKey<DamageType> getDamageType() {
        return EBDamageSources.FIRE;
    }
}
