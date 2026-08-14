package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.core.ArtifactEffectContext;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.Spells;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class LeechingRingEffect implements IArtifactEffect {

    @Override
    public void onHurtEntity(LivingEntity user, LivingEntity damagedEntity, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ArtifactEffectContext context) {
        if (user instanceof Player player && player.getHealth() < player.getMaxHealth() && player.getRandom().nextFloat() < 0.3f) {
            float healFactor = Optional.ofNullable(Spells.LIFE_DRAIN.property(DefaultProperties.HEALTH)).map(Number::floatValue).orElse(0.5f);
            player.heal(amount.floatValue() * healFactor);
        }
    }
}
