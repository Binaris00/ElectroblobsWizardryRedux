package com.binaris.wizardry.api.content.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IWorkbenchItem {
    default boolean canPlace(ItemStack stack) {
        return true;
    }

    int getSpellSlotCount(ItemStack stack);

    boolean onApplyButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks);

    default void onClearButtonPressed(Player player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) {
    }

    default boolean isClearable() {
        return false;
    }

    boolean showTooltip(ItemStack stack);

    default ItemStack applyUpgrade(@Nullable Player player, ItemStack stack, ItemStack upgrade) {
        return stack;
    }
}
