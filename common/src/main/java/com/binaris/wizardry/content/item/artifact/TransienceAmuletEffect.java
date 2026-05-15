package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.core.IArtifactEffect;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class TransienceAmuletEffect implements IArtifactEffect {
    @Override
    public void onPlayerHurt(Player player, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
        if (player.getHealth() <= 6 && player.level().random.nextFloat() < 0.25f) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0, false, false));
        }
    }
}
