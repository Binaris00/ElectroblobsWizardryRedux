package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.core.ArtifactEffectContext;
import com.binaris.wizardry.core.ArtifactUtils;
import com.binaris.wizardry.core.IArtifactEffect;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class AmuletPotentialEffect implements IArtifactEffect {
    public static float PROBABILITY_EFFECT = 0.2F;

    @Override
    public void onUserHurt(LivingEntity user, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ArtifactEffectContext context) {
        if (source.isIndirect() || !(source.getDirectEntity() instanceof LivingEntity)) return;
        if (user.getRandom().nextFloat() < PROBABILITY_EFFECT) {
            ArtifactUtils.handleLightningEffect(user, (LivingEntity) source.getDirectEntity(), user);
        }
    }
}
