package com.binaris.wizardry.content.entity.projectile;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class DarknessOrbEntity extends MagicProjectileEntity {
    public DarknessOrbEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public DarknessOrbEntity(Level world) {
        super(EBEntities.DARKNESS_ORB.get(), world);
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(this.getDeltaMovement().x / 0.99, this.getDeltaMovement().y / 0.99, this.getDeltaMovement().z / 0.99);

        if (level().isClientSide && tickCount > 2) {
            float brightness = random.nextFloat() * 0.2f;
            ParticleBuilder.create(EBParticles.SPARKLE, this).time(20 + random.nextInt(10)).color(brightness, 0.0f, brightness).spawn(level());
            ParticleBuilder.create(EBParticles.DARK_MAGIC, this).color(0.1f, 0.0f, 0.0f).spawn(level());
        }
    }

    @Override
    public void onHitTargetExtraEffects(@NotNull EntityHitResult hitResult) {
        if (!(hitResult.getEntity() instanceof LivingEntity livingEntity)) return;
        livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER,
                Spells.DARKNESS_ORB.property(DefaultProperties.EFFECT_DURATION),
                Spells.DARKNESS_ORB.property(DefaultProperties.EFFECT_STRENGTH)));
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public int getLifeTime() {
        return 60;
    }

    @Override
    public float getDamage(@NotNull EntityHitResult hitResult) {
        return Spells.DARKNESS_ORB.property(DefaultProperties.DAMAGE);
    }

    @Override
    public ResourceKey<DamageType> getDamageType(@NotNull EntityHitResult hitResult) {
        return EBDamageSources.WITHER;
    }

    @Override
    public @NotNull SoundEvent getSoundEvent(HitResult result) {
        return result.getType() == HitResult.Type.ENTITY ? EBSounds.ENTITY_DARKNESS_ORB_HIT.get() : SoundEvents.EMPTY;
    }
}