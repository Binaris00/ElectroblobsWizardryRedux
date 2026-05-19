package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class ChannelingAmuletEffect implements IArtifactEffect {
    @Override
    public void onPlayerHurt(Player player, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
        if (player.level().random.nextFloat() < 0.3f && source.is(EBDamageSources.SHOCK)) {
            canceled.set(true);
        }
    }
}
