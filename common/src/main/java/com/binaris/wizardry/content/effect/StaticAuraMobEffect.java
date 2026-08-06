package com.binaris.wizardry.content.effect;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.api.content.util.MagicDamageSource;
import com.binaris.wizardry.core.MobEffectContext;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicBoolean;

public class StaticAuraMobEffect extends MagicMobEffect {
    public StaticAuraMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public void onUserHurt(LivingEntity user, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, MobEffectContext context) {
        if (source.getEntity() == null) return;
        source.getEntity().hurt(MagicDamageSource.causeDirectMagicDamage(user, EBDamageSources.SHOCK), amount.floatValue() / 2);
        source.getEntity().playSound(EBSounds.SPELL_STATIC_AURA_RETALIATE.get(), 1.0F, user.level().random.nextFloat() * 0.4F + 1.5F);
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        ParticleBuilder.create(EBParticles.SPARK).pos(x, y, z).spawn(world);
    }
}
