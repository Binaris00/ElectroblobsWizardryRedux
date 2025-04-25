package com.electroblob.wizardry.content.menu.slot;

import com.electroblob.wizardry.content.item.SpellBookItem;
import com.electroblob.wizardry.content.menu.ArcaneWorkbenchMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SlotBookList extends SlotItemClassList {
    private final ArcaneWorkbenchMenu container;
    private final int listIndex;

    @SuppressWarnings("unchecked")
    public SlotBookList(Container inventory, int index, int x, int y, ArcaneWorkbenchMenu container, int listIndex) {
        super(inventory, index, x, y, 64, SpellBookItem.class);
        this.container = container;
        this.listIndex = listIndex;
    }

    public VirtualSlot getDelegate() {
        return hasDelegate() ? container.getVisibleBookshelfSlots().get(listIndex) : null;
    }

    public boolean hasDelegate() {
        return listIndex < container.getVisibleBookshelfSlots().size();
    }

    @Override
    public ItemStack getItem() {
        return hasDelegate() ? getDelegate().getItem() : ItemStack.EMPTY;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return container.getBookshelfSlots().stream().anyMatch(s -> s.mayPlace(stack));
    }

    @Override
    public boolean isActive() {
        return container.hasBookshelves();
    }

    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public void set(ItemStack stack) {

    }
}
