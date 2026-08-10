package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.core.ArtifactEffectContext;
import com.binaris.wizardry.core.IArtifactEffect;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class TransienceAmuletEffect implements IArtifactEffect {
    @Override
    public void onUserHurt(LivingEntity user, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ArtifactEffectContext context) {
        if (user.getHealth() <= 6 && user.level().random.nextFloat() < 0.25f) {
            user.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0, false, false));
        }
    }
}
