package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Elements;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class PoisonRingEffect implements IArtifactEffect {
    private static final int POISON_DURATION = 100; // (5 seconds)

    @Override
    public void onHurtEntity(Player player, LivingEntity damagedEntity, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
        if (source.is(EBDamageSources.POISON)) {
            damagedEntity.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION));
            return;
        }

        ItemStack wand = player.getMainHandItem();
        if (wand.isEmpty()) return;
        if (wand.getItem() instanceof ICastItem castItem && castItem.getCurrentSpell(wand).getElement() == Elements.EARTH) {
            damagedEntity.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION));
        }
    }
}
