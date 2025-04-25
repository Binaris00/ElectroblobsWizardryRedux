package com.electroblob.wizardry.content.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SlotItemClassList extends Slot {
    private final Class<? extends Item>[] itemClasses;
    private int stackLimit;

    @SuppressWarnings("unchecked")
    public SlotItemClassList(Container p_40223_, int p_40224_, int p_40225_, int p_40226_, int stackLimit, Class<? extends Item>... allowedItemClasses) {
        super(p_40223_, p_40224_, p_40225_, p_40226_);
        this.itemClasses = allowedItemClasses;
        this.stackLimit = stackLimit;
    }

    @Override
    public int getMaxStackSize() {
        return stackLimit;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        for (Class<? extends Item> itemClass : itemClasses) {
            if (itemClass.isAssignableFrom(stack.getItem().getClass())) {
                return true;
            }
        }

        return false;
    }
}
