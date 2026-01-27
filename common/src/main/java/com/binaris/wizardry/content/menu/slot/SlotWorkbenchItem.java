package com.binaris.wizardry.content.menu.slot;

import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Arcane Workbench slot that only accepts items implementing {@link IWorkbenchItem}.
 * Notifies the menu when its contents change.
 */
public class SlotWorkbenchItem extends Slot {
    private final ArcaneWorkbenchMenu menu;

    public SlotWorkbenchItem(Container container, int slot, int x, int y, ArcaneWorkbenchMenu menu) {
        super(container, slot, x, y);
        this.menu = menu;
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        super.set(stack);
        this.menu.onSlotChanged(index, stack, null);
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        this.menu.onSlotChanged(index, ItemStack.EMPTY, player);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (getMaxStackSize() > 0 && stack.getCount() > getMaxStackSize() && Services.PLATFORM.getPlatformName().equals("Fabric")) {
            return false;
        }

        return stack.getItem() instanceof IWorkbenchItem workbenchItem && workbenchItem.canPlace(stack);
    }
}
