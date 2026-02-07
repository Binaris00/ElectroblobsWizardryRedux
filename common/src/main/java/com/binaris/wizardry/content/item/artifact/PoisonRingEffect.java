package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import com.binaris.wizardry.setup.registries.Elements;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PoisonRingEffect implements IArtifactEffect {
    private static final int POISON_DURATION = 100; // (5 seconds)

    @Override
    public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();
        LivingEntity damaged = event.getDamagedEntity();

        if (!(sourceEntity instanceof Player player)) return;

        if (source.is(EBDamageSources.POISON)) {
            damaged.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION));
            return;
        }

        if (source.isIndirect()) {
            ItemStack wand = player.getItemInHand(player.getUsedItemHand());
            if (wand.isEmpty()) return;
            Item item = wand.getItem();
            if (item instanceof ISpellCastingItem castItem && castItem.getCurrentSpell(wand).getElement() == Elements.EARTH) {
                damaged.addEffect(new MobEffectInstance(MobEffects.POISON, POISON_DURATION));
            }
        }
    }
}
