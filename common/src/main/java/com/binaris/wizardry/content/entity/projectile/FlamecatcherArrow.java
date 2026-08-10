package com.binaris.wizardry.content.entity.projectile;


import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
    public double getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.FLAMECATCHER.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return (int) (Spells.FLAMECATCHER.property(DefaultProperties.RANGE) * SPEED);
    }

    @Override
    public boolean doDeceleration() {
        return false;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public void onHitTargetExtraEffects(@NotNull EntityHitResult hitResult) {
        if (hitResult.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.setSecondsOnFire(15);
        }
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return EBSounds.ENTITY_FLAMECATCHER_ARROW_HIT.get();
    }

    @Override
    public void ticksInAir() {
        super.ticksInAir();
        if (!this.level().isClientSide) return;
        ParticleBuilder.create(EBParticles.MAGIC_FIRE, level().getRandom(), this.getX(), this.getY(), this.getZ(), 0.03, false)
                .time(20 + this.random.nextInt(10)).spawn(level());

        if (this.getLifetime() > 1) {
            double x = this.getX() - this.getDeltaMovement().x / 2;
            double y = this.getY() - this.getDeltaMovement().y / 2;
            double z = this.getZ() - this.getDeltaMovement().z / 2;
            ParticleBuilder.create(EBParticles.MAGIC_FIRE, level().getRandom(), x, y, z, 0.03, false)
                    .time(20 + this.random.nextInt(10)).spawn(level());
        }
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.FIRE;
    }
}
