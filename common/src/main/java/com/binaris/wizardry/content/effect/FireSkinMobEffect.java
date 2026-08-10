package com.binaris.wizardry.content.effect;

import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.MobEffectContext;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.binaris.wizardry.setup.registries.Spells;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicBoolean;

public class FireSkinMobEffect extends MagicMobEffect {
    public FireSkinMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public void onUserHurt(LivingEntity user, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, MobEffectContext context) {
        if (source.getEntity() == null) return;
        source.getEntity().setSecondsOnFire(Spells.FIRE_BREATH.property(DefaultProperties.EFFECT_DURATION) * 20);
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        world.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        livingEntity.clearFire();
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return true;
    }
}
