package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.core.IArtefactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HungerCastingCharmEffect implements IArtefactEffect {

    @Override
    public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack stack) {
        if (!(event.getCaster() instanceof Player player)) return;
        if (player.isCreative() || event.getSource() != SpellCastEvent.Source.WAND || !event.getSpell().isInstantCast())
            return;

        ItemStack wand = player.getMainHandItem();

        if (!(wand.getItem() instanceof ISpellCastingItem && wand.getItem() instanceof IManaStoringItem)) {
            wand = player.getOffhandItem();
            if (!(wand.getItem() instanceof ISpellCastingItem && wand.getItem() instanceof IManaStoringItem)) return;
        }

        if (((IManaStoringItem) wand.getItem()).getMana(wand) < event.getSpell().getCost() * event.getModifiers().get(SpellModifiers.COST)) {
            int hunger = event.getSpell().getCost() / 5;

            if (player.getFoodData().getFoodLevel() >= hunger) {
                player.getFoodData().eat(-hunger, 0);
                event.getModifiers().set(SpellModifiers.COST, 0, false);
            }
        }
    }
}
