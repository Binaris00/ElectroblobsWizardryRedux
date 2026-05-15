package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.platform.Services;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LichAmuletEffect implements IArtifactEffect {
    @Override
    public void onPlayerHurt(Player player, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
        if (!(player.level().random.nextFloat() < 0.15f)) return;

        List<LivingEntity> nearbyMinions = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5));
        nearbyMinions.removeIf(e -> !Services.OBJECT_DATA.isMinion(e));
        nearbyMinions.removeIf(e -> Services.OBJECT_DATA.getMinionData((Mob) e).getOwner() != player);

        if (!nearbyMinions.isEmpty()) {
            Collections.shuffle(nearbyMinions);
            nearbyMinions.get(0).hurt(source, amount.floatValue());
            canceled.set(true);
        }
    }
}
