package com.binaris.wizardry.content.effect;

import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.core.MobEffectContext;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicBoolean;

public class WardMobEffect extends MagicMobEffect {
    public WardMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xc991d0);
    }

    @Override
    public void onUserHurt(LivingEntity user, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, MobEffectContext context) {
        if (EBDamageSources.isMagic(source)) {
            float f = amount.floatValue();
            f *= Math.max(0, 1 - 0.2f * (1 + user.getEffect(EBMobEffects.WARD.get()).getAmplifier()));
            amount.set(f);
        }
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {

    }
}
