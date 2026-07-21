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
import org.jetbrains.annotations.NotNull;

public class IceLanceEntity extends MagicArrowEntity {
    public IceLanceEntity(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    public IceLanceEntity(Level world) {
        super(EBEntities.ICE_LANCE.get(), world);
    }

    @Override
    public void onHitTargetExtraEffects(@NotNull EntityHitResult hitResult) {
        if (hitResult.getEntity() instanceof LivingEntity livingEntity && !level().isClientSide) {
            livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), Spells.ICE_LANCE.property(DefaultProperties.EFFECT_DURATION), 0));
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        if (!this.level().isClientSide()) return;
        for (int j = 0; j < 10; j++) {
            ParticleBuilder.create(EBParticles.ICE, level().getRandom(), this.getX(), this.getY(), this.getZ(), 0.5, true)
                    .time(20 + random.nextInt(10)).gravity(true).spawn(this.level());
        }
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return result instanceof BlockHitResult ? EBSounds.ENTITY_ICE_LANCE_SMASH.get() : EBSounds.ENTITY_ICE_LANCE_HIT.get();
    }

    @Override
    public double getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.ICE_LANCE.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return 60;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.FROST;
    }
}
