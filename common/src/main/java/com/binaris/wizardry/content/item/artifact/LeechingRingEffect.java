package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.Spells;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class LeechingRingEffect implements IArtifactEffect {

    @Override
    public void onHurtEntity(Player player, LivingEntity damagedEntity, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
        // 30% chance to heal
        if (player.getHealth() < player.getMaxHealth() && player.getRandom().nextFloat() < 0.3f) {
            float healFactor = Optional.ofNullable(Spells.LIFE_DRAIN.property(DefaultProperties.HEALTH)).map(Number::floatValue).orElse(0.5f);
            player.heal(amount.floatValue() * healFactor);
        }
    }
}
