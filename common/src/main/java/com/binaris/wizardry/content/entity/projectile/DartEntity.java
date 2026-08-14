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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;


public class DartEntity extends MagicArrowEntity {
    public DartEntity(EntityType<DartEntity> entityDartEntityType, Level world) {
        super(entityDartEntityType, world);
    }

    public DartEntity(Level world) {
        super(EBEntities.DART.get(), world);
    }

    @Override
    public double getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.DART.property(DefaultProperties.DAMAGE);
    }

    @Override
    public int getLifetime() {
        return -1;
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.POISON;
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return result.getType() == HitResult.Type.BLOCK ? EBSounds.ENTITY_DART_HIT_BLOCK.get() : EBSounds.ENTITY_DART_HIT.get();
    }

    @Override
    public void onHitTargetExtraEffects(@NotNull EntityHitResult hitResult) {
        if (!(hitResult.getEntity() instanceof LivingEntity livingEntity)) return;
        livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, Spells.DART.property(DefaultProperties.EFFECT_DURATION),
                Spells.DART.property(DefaultProperties.EFFECT_STRENGTH), false, false));
    }

    @Override
    public void ticksInAir() {
        if (this.level().isClientSide() && tickCount > 1) {
            ParticleBuilder.create(EBParticles.LEAF, this).time(10 + random.nextInt(5)).spawn(level());
        }
    }

    @Override
    public void tickInGround() {
        if (this.ticksInGround > 40) {
            this.discard();
        }
    }
}
