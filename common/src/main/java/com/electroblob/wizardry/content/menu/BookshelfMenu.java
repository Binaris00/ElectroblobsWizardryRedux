package com.electroblob.wizardry.content.menu;

import com.electroblob.wizardry.content.block.BookShelfBlock;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class BookshelfMenu extends AbstractContainerMenu {
    // TODO: This could be part of a "provider interface" for other mods to add their own books, I'm temporarily leaving it like this though
    private static final Set<Item> validItems = new HashSet<>();
    public Container container;

    public BookshelfMenu(int i, Inventory inventory) {
        this(i, inventory, new SimpleContainer(BookShelfBlock.SLOT_COUNT));
    }

    public BookshelfMenu(int i, Inventory inventory, Container container) {
        super(EBMenus.BOOKSHELF_MENU.get(), i);
        this.container = container;

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < BookShelfBlock.SLOT_COUNT / 2; x++) {
                this.addSlot(new SlotBookshelf(container, x + BookShelfBlock.SLOT_COUNT / 2 * y, 35 + x * 18, 17 + y * 18));
            }
        }

        // Add player hotbar
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inventory, x, 8 + x * 18, 124));
        }

        // Add player main inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(inventory, 9 + x + y * 9, 8 + x * 18, 66 + y * 18));
            }
        }
    }

    public static boolean isBook(ItemStack stack) {
        return validItems.contains(stack.getItem());
    }

    public static void registerBookItem(Item item) {
        validItems.add(item);
    }

    public static void initBookItems() {
        registerBookItem(Items.BOOK);
        registerBookItem(Items.WRITTEN_BOOK);
        registerBookItem(Items.WRITABLE_BOOK);
        registerBookItem(Items.ENCHANTED_BOOK);
        registerBookItem(EBItems.SPELL_BOOK.get());
        registerBookItem(EBItems.ARCANE_TOME.get());
        registerBookItem(EBItems.RUINED_SPELL_BOOK.get());
        registerBookItem(EBItems.SCROLL.get());
        registerBookItem(EBItems.BLANK_SCROLL.get());
        registerBookItem(EBItems.IDENTIFICATION_SCROLL.get());

        registerBookItem(EBItems.STORAGE_UPGRADE.get());
        registerBookItem(EBItems.SIPHON_UPGRADE.get());
        registerBookItem(EBItems.CONDENSER_UPGRADE.get());
        registerBookItem(EBItems.RANGE_UPGRADE.get());
        registerBookItem(EBItems.DURATION_UPGRADE.get());
        registerBookItem(EBItems.COOLDOWN_UPGRADE.get());
        registerBookItem(EBItems.BLAST_UPGRADE.get());
        registerBookItem(EBItems.ATTUNEMENT_UPGRADE.get());
        registerBookItem(EBItems.MELEE_UPGRADE.get());
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int clickedSlotId) {
        ItemStack remainder = ItemStack.EMPTY;
        Slot slot = this.slots.get(clickedSlotId);
        if (!slot.hasItem()) return remainder;

        ItemStack stack = slot.getItem();
        remainder = stack.copy();

        if (clickedSlotId < BookShelfBlock.SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, BookShelfBlock.SLOT_COUNT, this.slots.size(), true))
                return ItemStack.EMPTY;
        } else {
            int minSlotId = 0;
            int maxSlotId = BookShelfBlock.SLOT_COUNT - 1;

            if (!isBook(stack)) return ItemStack.EMPTY;

            if (!this.moveItemStackTo(stack, minSlotId, maxSlotId + 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == remainder.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);

        return remainder;
    }

    public static class SlotBookshelf extends Slot {
        public SlotBookshelf(Container container, int index, int x, int y) {
            super(container, index, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return isBook(stack);
        }
    }
}
