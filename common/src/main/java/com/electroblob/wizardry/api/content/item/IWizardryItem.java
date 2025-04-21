package com.electroblob.wizardry.api.content.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IWizardryItem {
    private Item self() {
        return (Item) this;
    }

    default int getMaxDamage(ItemStack stack) {
        return this.self().getMaxDamage();
    }

    default void setDamage(ItemStack stack, int damage) {
        stack.setDamageValue(damage);
    }
}
