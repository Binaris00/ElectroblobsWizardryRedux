package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HungerCastingCharmEffect implements IArtifactEffect {

    @Override
    public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
        if (!(event.getCaster() instanceof Player player)) return;
        if (player.isCreative() || event.getSource() != SpellCastEvent.Source.WAND || !event.getSpell().isInstantCast())
            return;

        ItemStack wand = player.getMainHandItem();

        if (!(wand.getItem() instanceof ICastItem && wand.getItem() instanceof IManaItem)) {
            wand = player.getOffhandItem();
            if (!(wand.getItem() instanceof ICastItem && wand.getItem() instanceof IManaItem)) return;
        }

        if (((IManaItem) wand.getItem()).getMana(wand) < event.getSpell().getCost() * event.getModifiers().get(SpellModifiers.COST)) {
            int hunger = event.getSpell().getCost() / 5;

            if (player.getFoodData().getFoodLevel() >= hunger) {
                player.getFoodData().eat(-hunger, 0);
                event.getModifiers().set(SpellModifiers.COST, 0);
            }
        }
    }
}
