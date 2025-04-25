package com.electroblob.wizardry.content.menu.slot;

import com.electroblob.wizardry.api.content.item.IWorkbenchItem;
import com.electroblob.wizardry.content.menu.ArcaneWorkbenchMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotWorkbenchItem extends Slot {
    private final ArcaneWorkbenchMenu container;

    public SlotWorkbenchItem(Container p_40223_, int p_40224_, int p_40225_, int p_40226_, ArcaneWorkbenchMenu container) {
        super(p_40223_, p_40224_, p_40225_, p_40226_);
        this.container = container;
    }

    @Override
    public void set(ItemStack stack) {
        super.set(stack);
        this.container.onSlotChanged(index, stack, null);
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.container.onSlotChanged(index, ItemStack.EMPTY, player);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof IWorkbenchItem workbenchItem && workbenchItem.canPlace(stack);
    }
}
