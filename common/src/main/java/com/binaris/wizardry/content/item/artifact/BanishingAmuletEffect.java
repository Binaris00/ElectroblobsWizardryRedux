package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.content.spell.necromancy.Banish;
import com.binaris.wizardry.core.ArtifactEffectContext;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.Spells;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class BanishingAmuletEffect implements IArtifactEffect {
    @Override
    public void onUserHurt(LivingEntity user, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ArtifactEffectContext context) {
        if (user.level().random.nextFloat() < 0.2f && !source.isIndirect() && source.getEntity() instanceof LivingEntity sourceEntity) {
            ((Banish) Spells.BANISH).teleport(sourceEntity, user.level(), 8 + user.level().random.nextDouble() * 8);
        }
    }
}