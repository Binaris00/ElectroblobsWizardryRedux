package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.*;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class IceShardEntity extends MagicArrowEntity {
    public IceShardEntity(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public IceShardEntity(Level world) {
        super(EBEntities.ICE_SHARD.get(), world);
    }

    @Override
    public void onHitTargetExtraEffects(@NotNull EntityHitResult hitResult) {
        if (hitResult.getEntity() instanceof LivingEntity livingEntity) {
            if (level().isClientSide) return;
            livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(),
                    Spells.ICE_SHARD.property(DefaultProperties.EFFECT_DURATION),
                    Spells.ICE_SHARD.property(DefaultProperties.EFFECT_STRENGTH), false, false));
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (this.level().isClientSide()) {
            Vec3 pos = blockHitResult.getLocation();
            ParticleBuilder.create(EBParticles.FLASH).pos(pos).color(0.75f, 1.0f, 1.0f).spawn(level());

            for (int i = 0; i < 8; i++) {
                ParticleBuilder.create(EBParticles.ICE, this.random, this.getX(), this.getY(), this.getZ(), 0.5, true)
                        .time(20 + this.random.nextInt(10)).gravity(true).spawn(this.level());
            }
        }

    }

    @Override
    public void tickInGround() {
        if (this.ticksInGround > 40) {
            this.discard();
        }
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return result instanceof BlockHitResult ? EBSounds.ENTITY_ICE_SHARD_SMASH.get() : EBSounds.ENTITY_ICE_SHARD_HIT.get();
    }

    @Override
    public double getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.ICE_SHARD.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return -1;
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.FROST;
    }
}

