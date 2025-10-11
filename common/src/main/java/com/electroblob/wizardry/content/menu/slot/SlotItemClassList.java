package com.electroblob.wizardry.content.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Arcane Workbench Slot that only allows items whose class matches one of the specified allowed item classes.
 * The slot also enforces a configurable stack size limit.
 */
public class SlotItemClassList extends Slot {
    private final Class<? extends Item>[] itemClasses;
    private final int stackLimit;

    @SuppressWarnings("unchecked")
    public SlotItemClassList(Container container, int slot, int x, int y, int stackLimit, Class<? extends Item>... allowedItemClasses) {
        super(container, slot, x, y);
        this.itemClasses = allowedItemClasses;
        this.stackLimit = stackLimit;
    }

    @Override
    public int getMaxStackSize() {
        return stackLimit;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        for (Class<? extends Item> itemClass : itemClasses) {
            if (itemClass.isAssignableFrom(stack.getItem().getClass())) {
                return true;
            }
        }

        return false;
    }
}
