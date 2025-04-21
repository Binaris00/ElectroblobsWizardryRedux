package com.electroblob.wizardry.api.content.item;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ISpellCastingItem {

    boolean showSpellHUD(Player player, ItemStack stack);

    @NotNull Spell getCurrentSpell(ItemStack stack);

    @NotNull default Spell getNextSpell(ItemStack stack) {
        return getCurrentSpell(stack);
    }

    @NotNull default Spell getPreviousSpell(ItemStack stack) {
        return getCurrentSpell(stack);
    }

    default Spell[] getSpells(ItemStack stack) {
        return new Spell[]{getCurrentSpell(stack)};
    }

    default void selectNextSpell(ItemStack stack) {}

    default void selectPreviousSpell(ItemStack stack) {}

    default boolean selectSpell(ItemStack stack, int index) {
        return false;
    }

    default int getCurrentCooldown(ItemStack stack) {
        return 0;
    }

    default int getCurrentMaxCooldown(ItemStack stack) {
        return 0;
    }

    default boolean showSpellsInWorkbench(Player player, ItemStack stack) {
        return true;
    }

    boolean canCast(ItemStack stack, Spell spell, Player caster, InteractionHand hand, int castingTick, SpellModifiers modifiers);

    boolean cast(ItemStack stack, Spell spell, Player caster, InteractionHand hand, int castingTick, SpellModifiers modifiers);
}
