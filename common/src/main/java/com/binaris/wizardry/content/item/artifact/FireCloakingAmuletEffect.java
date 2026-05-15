package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.content.entity.construct.FireRingConstruct;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.core.IArtifactEffect;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FireCloakingAmuletEffect implements IArtifactEffect {
    @Override
    public void onPlayerHurt(Player player, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
        List<FireRingConstruct> fireRings = player.level().getEntitiesOfClass(FireRingConstruct.class, player.getBoundingBox());

        for (FireRingConstruct fireRing : fireRings) {
            if (fireRing.getCaster() instanceof Player && (fireRing.getCaster() == player || AllyDesignation.isOwnerAlly(player, fireRing))) {
                amount.set(amount.floatValue() * 0.25F);
                break;
            }
        }
    }
}