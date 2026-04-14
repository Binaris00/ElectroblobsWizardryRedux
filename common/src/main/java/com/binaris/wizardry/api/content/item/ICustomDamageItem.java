package com.binaris.wizardry.api.content.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Exposing helper methods to define how to obtain/set the damage/max-damage for an item, especially used to avoid item
 * destruction in normal cases.
 */
public interface ICustomDamageItem {
    private Item self() {
        return (Item) this;
    }

    /** you can use this to set custom max damage based on some events */
    default int getCustomMaxDamage(ItemStack stack) {
        return this.self().getMaxDamage();
    }

    /** exposing a new way to control when and how damage is set */
    default void setCustomDamage(ItemStack stack, int damage) {
        stack.setDamageValue(damage);
    }

    /** used to control whether the item can be broken or if it should stay at 0 durability */
    default boolean canBreak(ItemStack stack) {
        return true;
    }
}
