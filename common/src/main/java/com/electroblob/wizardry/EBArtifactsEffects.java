package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.content.spell.sorcery.ImbueWeapon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class EBArtifactsEffects {
    private EBArtifactsEffects(){
    }

//    public static void condensingRingTick(ItemStack stack, SlotReference reference) {
//        if (!(reference.entity() instanceof Player player)) return;
//
//        if (player.tickCount % 150 == 0) {
//            EBLogger.info("I'm working chat!!!");
//            InventoryUtil.getHotbar(player).stream()
//                    .filter(st -> st.getItem() instanceof IManaStoringItem)
//                    .forEach(st -> ((IManaStoringItem) st.getItem()).rechargeMana(stack, 1));
//        }
//    }

    // ==================================
    // PRE CAST
    // ==================================

    public static void battlemageRingPreCast(SpellCastEvent.Pre event, ItemStack stack) {
        if(!(event.getCaster() instanceof Player player)) return;

        if (player.getOffhandItem().getItem() instanceof ISpellCastingItem && ImbueWeapon.isSword(player.getMainHandItem())) {
            event.getModifiers().set(SpellModifiers.POTENCY, 1.1f * event.getModifiers().get(SpellModifiers.POTENCY), false);
        }
    }
}
