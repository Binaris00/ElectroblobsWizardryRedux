package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.core.IArtefactEffect;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.Elements;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PoisonRingEffect implements IArtefactEffect {
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
