package com.binaris.wizardry.content.menu;

import com.binaris.wizardry.api.content.event.SpellBindEvent;
import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.content.block.LecternBookshelfUtils;
import com.binaris.wizardry.content.blockentity.ArcaneWorkbenchBlockEntity;
import com.binaris.wizardry.content.item.SpellBookItem;
import com.binaris.wizardry.content.menu.slot.SlotItemClassList;
import com.binaris.wizardry.content.menu.slot.SlotItemList;
import com.binaris.wizardry.content.menu.slot.SlotWorkbenchItem;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.mixin.accessor.SlotAccessor;
import com.binaris.wizardry.setup.registries.EBAdvancementTriggers;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBMenus;
import com.binaris.wizardry.setup.registries.WandUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/// Where _Part_ of the magic happens. This class handles the server-side logic of the Arcane Workbench GUI,
/// including the slots, shift-clicking, and the apply and clear buttons. Most of this work is delegated to the workbench
/// item and the changes between when is a wand is in the center slot and when there isn't one are handled here.
///
/// @see IWorkbenchItem
public class ArcaneWorkbenchMenu extends AbstractContainerMenu {
    public static final int CRYSTAL_SLOT = 8;
    public static final int CENTRE_SLOT = 9;
    public static final int UPGRADE_SLOT = 10;
    public static final int SLOT_RADIUS = 42;
    public static final int PLAYER_INVENTORY_SIZE = 36;
    public static final ResourceLocation ARCANE_WORKBENCH_EMPTY_SLOT_CRYSTAL = new ResourceLocation("item/empty_slot_crystal");
    public static final ResourceLocation ARCANE_WORKBENCH_EMPTY_SLOT_UPGRADE = new ResourceLocation("item/empty_slot_upgrade");
    public Container container;
    public boolean needsRefresh;
    private final Player player;
    private @Nullable BlockPos clientWorkbenchPos;

    public ArcaneWorkbenchMenu(int i, Inventory playerInv) {
        this(i, playerInv, new SimpleContainer(11));
    }

    public ArcaneWorkbenchMenu(int id, Inventory inventory, Container container) {
        super(EBMenus.ARCANE_WORKBENCH_MENU.get(), id);
        this.container = container;
        this.player = inventory.player;
        ItemStack wand = container.getItem(CENTRE_SLOT);

        // Spell Book Slots
        for (int i = 0; i < 8; i++)
            addSlot(new SlotItemClassList(container, i, -999, -999, 1, SpellBookItem.class));

        // Crystal and Workbench Slots
        addSlot(new SlotItemList(container, CRYSTAL_SLOT, 13, 101, 64, ARCANE_WORKBENCH_EMPTY_SLOT_CRYSTAL,
                EBItems.MAGIC_CRYSTAL.get(), EBItems.MAGIC_CRYSTAL_SHARD.get(), EBItems.MAGIC_CRYSTAL_GRAND.get(),
                EBItems.MAGIC_CRYSTAL_EARTH.get(), EBItems.MAGIC_CRYSTAL_FIRE.get(), EBItems.MAGIC_CRYSTAL_HEALING.get(),
                EBItems.MAGIC_CRYSTAL_ICE.get(), EBItems.MAGIC_CRYSTAL_LIGHTNING.get(), EBItems.MAGIC_CRYSTAL_NECROMANCY.get(),
                EBItems.MAGIC_CRYSTAL_SORCERY.get()
        ));
        addSlot(new SlotWorkbenchItem(container, CENTRE_SLOT, 80, 64, this));

        // Upgrade Slot
        Set<Item> upgrades = new HashSet<>(WandUpgrades.getSpecialUpgrades());
        upgrades.add(EBItems.ARCANE_TOME.get());
        upgrades.add(EBItems.APPRENTICE_ARCANE_TOME.get());
        upgrades.add(EBItems.ADVANCED_ARCANE_TOME.get());
        upgrades.add(EBItems.MASTER_ARCANE_TOME.get());
        upgrades.add(EBItems.RESPLENDENT_THREAD.get());
        upgrades.add(EBItems.CRYSTAL_SILVER_PLATING.get());
        upgrades.add(EBItems.ETHEREAL_CRYSTAL_WEAVE.get());
        addSlot(new SlotItemList(container, UPGRADE_SLOT, 147, 17, 1, ARCANE_WORKBENCH_EMPTY_SLOT_UPGRADE, upgrades.toArray(new Item[0])));

        // Player Inventory Slots
        for (int x = 0; x < 9; x++)
            addSlot(new Slot(inventory, x, 8 + x * 18, 196));
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 9; x++)
                addSlot(new Slot(inventory, 9 + x + y * 9, 8 + x * 18, 138 + y * 18));

        onSlotChanged(CENTRE_SLOT, wand, this.player);
    }

    /// Returns the X offset of the book slot at the given index, this is all assuming the slots are arranged in a circle
    /// around the center slot.
    ///
    /// @param i             The index of the book slot (0-7).
    /// @param bookSlotCount The number of book slots to arrange in a circle (1 - 8).
    /// @return The X offset of the book slot at the given index.
    private static int getBookSlotXOffset(int i, int bookSlotCount) {
        float angle = i * (2 * (float) Math.PI) / bookSlotCount;
        return Math.round(SLOT_RADIUS * Mth.sin(angle));
    }

    /// Returns the Y offset of the book slot at the given index, this is all assuming the slots are arranged in a circle
    /// around the center slot.
    ///
    /// @param i             The index of the book slot (0-7).
    /// @param bookSlotCount The number of book slots to arrange in a circle (1 - 8).
    /// @return The Y offset of the book slot at the given index.
    private static int getBookSlotYOffset(int i, int bookSlotCount) {
        float angle = i * (2 * (float) Math.PI) / bookSlotCount;
        return Math.round(SLOT_RADIUS * -Mth.cos(angle));
    }

    /// Handles shift-clicking. Attempts to move items between the workbench slots and the player inventory. If the item
    /// can go in multiple workbench slots (e.g. spell books), it will try to put it in the first available one. If it
    /// can't go in any workbench slots, it will try to move it between the hotbar and main inventory.
    ///
    /// @param player The player using the workbench.
    /// @param index  The ID of the slot that was shift-clicked.
    /// @return The rest of the stack that couldn't be moved, or ItemStack.EMPTY if the entire stack was moved.
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        boolean moved = false;

        // If shift-clicking from workbench slots (0-10)
        if (index < 11) {
            // Try to move to player inventory (slots 11-46)
            moved = this.moveItemStackTo(stack, 11, 11 + PLAYER_INVENTORY_SIZE, true);
        }
        // If shift-clicking from player inventory
        else if (index < 11 + PLAYER_INVENTORY_SIZE) {
            int[] slotRange = findSlotRangeForItem(stack);

            if (slotRange != null) {
                moved = this.moveIntoWorkbenchSlot(stack, slotRange[0], slotRange[1] + 1);
            }

            // Any spell books left over are stored as stacks rather than split across one bookshelf slot per item.
            // This runs on both sides: the client consumes the predicted amount from the source stack, while only the
            // server mutates the bookshelf. Keeping the source stack identical prevents vanilla's QUICK_MOVE loop from
            // repeatedly flashing books through every workbench spell slot.
            if (stack.getItem() instanceof SpellBookItem && !stack.isEmpty()) {
                moved |= this.insertBookStackIntoNearbyBookshelf(stack);
            }
        }

        if (!moved || stack.getCount() == original.getCount()) return ItemStack.EMPTY;

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, stack);
        return original;
    }

    private boolean moveIntoWorkbenchSlot(ItemStack stack, int startIndex, int endIndex) {
        for (int index = startIndex; index < endIndex && !stack.isEmpty(); index++) {
            Slot target = this.getSlot(index);
            ItemStack present = target.getItem();
            if (!target.mayPlace(stack) || present.isEmpty() || !ItemStack.isSameItemSameTags(present, stack)) {
                continue;
            }

            int amount = Math.min(stack.getCount(), target.getMaxStackSize(stack) - present.getCount());
            if (amount <= 0) continue;

            present.grow(amount);
            stack.shrink(amount);
            target.setChanged();
            return true;
        }

        for (int index = startIndex; index < endIndex && !stack.isEmpty(); index++) {
            Slot target = this.getSlot(index);
            if (target.hasItem() || !target.mayPlace(stack)) continue;

            int amount = Math.min(stack.getCount(), target.getMaxStackSize(stack));
            target.set(stack.split(amount));
            target.setChanged();
            return true;
        }

        return false;
    }


    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);
    }

    /// Called when a slot is changed. If the center slot is changed, this updates the visibility and position of the
    /// spell book slots. (Because there's no point in showing them if there's no wand in the center slot). Depending on
    /// the wand upgrade, the number of spell slots may vary, so the spell book slots are arranged in a circle around
    /// the center slot.
    ///
    /// @param slotNumber The ID of the slot that changed.
    /// @param stack      The new stack in that slot.
    public void onSlotChanged(int slotNumber, ItemStack stack) {
        onSlotChanged(slotNumber, stack, this.player);
    }

    /// Called when a slot is changed. If the center slot is changed, this updates the visibility and position of the
    /// spell book slots. (Because there's no point in showing them if there's no wand in the center slot). Depending on
    /// the wand upgrade, the number of spell slots may vary, so the spell book slots are arranged in a circle around
    /// the center slot.
    ///
    /// @param slotNumber The slot number that changed.
    /// @param stack      The new stack in that slot.
    /// @param player     The player viewing the arcane workbench.
    public void onSlotChanged(int slotNumber, ItemStack stack, @NotNull Player player) {
        if (slotNumber != CENTRE_SLOT) return;
        if (stack.isEmpty()) {
            for (int i = 0; i < CRYSTAL_SLOT; i++) {
                this.hideSlot(i, player);
            }
            return;
        }

        if (stack.getItem() instanceof IWorkbenchItem workbenchItem) {
            int spellSlots = workbenchItem.getSpellSlotCount(stack);

            int centreX = this.getSlot(CENTRE_SLOT).x;
            int centreY = this.getSlot(CENTRE_SLOT).y;

            for (int i = 0; i < spellSlots; i++) {
                int x = centreX + getBookSlotXOffset(i, spellSlots);
                int y = centreY + getBookSlotYOffset(i, spellSlots);
                showSlot(i, x, y);
            }

            for (int i = spellSlots; i < CRYSTAL_SLOT; i++) {
                hideSlot(i, player);
            }
        }
    }


    /// Called when the apply button is pressed. Delegates the logic to the workbench item in the centre slot, if
    /// there is one. Normally it will be a wand, in that case, it applies the spells from the spell book slots to the
    /// wand.
    ///
    /// @param player The player using the workbench.
    public void onApplyButtonPressed(Player player) {
        if (player.level().isClientSide) return;
        if (WizardryEventBus.fireEvent(new SpellBindEvent(player, this))) return;
        Slot centre = this.getSlot(CENTRE_SLOT);

        if (centre.getItem().getItem() instanceof IWorkbenchItem workbenchItem) {
            Slot[] spellBooks = this.slots.subList(0, 8).toArray(new Slot[8]);

            if (workbenchItem.onApplyButtonPressed(player, centre, this.getSlot(CRYSTAL_SLOT), this.getSlot(UPGRADE_SLOT), spellBooks)) {
                if (player instanceof ServerPlayer serverPlayer)
                    EBAdvancementTriggers.ARCANE_WORKBENCH.trigger(serverPlayer, centre.getItem());
            }
        }
    }

    /// Called when the clear button is pressed. Delegates the logic to the workbench item in the centre slot, if
    /// there is one. Normally it will be a wand, in that case, it clears all spells from the wand.
    ///
    /// @param player The player using the workbench.
    public void onClearButtonPressed(Player player) {
        Slot centre = this.getSlot(CENTRE_SLOT);

        if (centre.getItem().getItem() instanceof IWorkbenchItem workbenchItem) {
            Slot[] spellBooks = this.slots.subList(0, 8).toArray(new Slot[8]);
            workbenchItem.onClearButtonPressed(player, centre, this.getSlot(CRYSTAL_SLOT), this.getSlot(UPGRADE_SLOT), spellBooks);
        }
    }

    /// Makes the slot at the given index visible and sets its position to the given coordinates.
    ///
    /// @param index The index of the slot to show.
    /// @param x     The x position to set the slot to.
    /// @param y     The y position to set the slot to.
    private void showSlot(int index, int x, int y) {
        Slot slot = this.getSlot(index);
        ((SlotAccessor) slot).setX(x);
        ((SlotAccessor) slot).setY(y);
    }

    /// Hides the slot at the given index by moving it off-screen. If there is an item in the slot, it tries to move
    /// it into the player's inventory, and if that fails, it drops it on the ground.
    ///
    /// @param index  The index of the slot to hide.
    /// @param player The player using the workbench.
    private void hideSlot(int index, Player player) {
        Slot slot = this.getSlot(index);
        ((SlotAccessor) slot).setX(-999);
        ((SlotAccessor) slot).setY(-999);

        this.quickMoveStack(player, index);
        ItemStack remaining = slot.getItem();

        if (remaining.isEmpty()) return;

        slot.set(ItemStack.EMPTY);
        if (player.addItem(remaining)) return;

        ItemEntity itemEntity = player.drop(remaining, false);
        if (itemEntity != null) {
            itemEntity.setNoPickUpDelay();
            itemEntity.setTarget(player.getUUID());
        }
    }


    /// Returns the position of the workbench backing this menu.
    public @Nullable BlockPos getWorkbenchPos() {
        return this.container instanceof ArcaneWorkbenchBlockEntity workbench
                ? workbench.getBlockPos()
                : this.clientWorkbenchPos;
    }

    /// Supplies the workbench position to the client menu, whose vanilla menu factory only creates a SimpleContainer.
    public void setClientWorkbenchPos(@Nullable BlockPos pos) {
        if (this.player.level().isClientSide) {
            this.clientWorkbenchPos = pos == null ? null : pos.immutable();
        }
    }

    public boolean takeBookToCursor(Container bookshelf, int shelfSlot) {
        if (bookshelf == null || shelfSlot < 0 || shelfSlot >= bookshelf.getContainerSize()) return false;

        ItemStack stack = bookshelf.getItem(shelfSlot);
        if (!(stack.getItem() instanceof SpellBookItem)) return false;

        ItemStack carried = this.getCarried();
        if (this.getCarried().isEmpty()) {
            this.setCarried(bookshelf.removeItem(shelfSlot, 1));
            bookshelf.setChanged();
            return true;
        }

        if (ItemStack.isSameItemSameTags(carried, stack) && carried.getCount() < carried.getMaxStackSize()) {
            ItemStack taken = bookshelf.removeItem(shelfSlot, 1);
            carried.grow(taken.getCount());
            this.setCarried(carried);
            bookshelf.setChanged();
            return true;
        }

        return false;
    }

    /// Moves one spell book from the given bookshelf slot into the spell slots or the player inventory, as used by
    /// shift-clicking in the bookshelf panel.
    public boolean storeBook(Container bookshelf, int shelfSlot) {
        if (bookshelf == null || shelfSlot < 0 || shelfSlot >= bookshelf.getContainerSize()) return false;

        ItemStack stack = bookshelf.getItem(shelfSlot);
        if (!(stack.getItem() instanceof SpellBookItem)) return false;

        ItemStack taken = bookshelf.removeItem(shelfSlot, 1);
        bookshelf.setChanged();
        if (taken.isEmpty()) return false;

        Slot centre = this.getSlot(CENTRE_SLOT);
        if (centre.getItem().getItem() instanceof IWorkbenchItem workbenchItem) {
            int spellSlots = Math.min(workbenchItem.getSpellSlotCount(centre.getItem()), CRYSTAL_SLOT);
            for (int i = 0; i < spellSlots; i++) {
                Slot spellSlot = this.getSlot(i);
                if (spellSlot.mayPlace(taken) && !spellSlot.hasItem()) {
                    spellSlot.set(taken);
                    spellSlot.setChanged();
                    return true;
                }
            }
        }

        if (!this.player.getInventory().add(taken)) this.player.drop(taken, false);
        return true;
    }

    /// Inserts one spell book from the player's cursor into the first available nearby bookshelf slot.
    public boolean storeCarriedBookInNearbyBookshelf(@Nullable BlockPos targetPos, int targetSlot) {
        ItemStack carried = this.getCarried();
        if (!(carried.getItem() instanceof SpellBookItem)) return false;

        if (targetPos != null) {
            BlockPos workbenchPos = this.getWorkbenchPos();
            if (workbenchPos == null
                    || !LecternBookshelfUtils.findBookshelfPositions(this.player.level(), workbenchPos).contains(targetPos)) {
                return false;
            }

            Container bookshelf = LecternBookshelfUtils.getBookshelf(this.player.level(), targetPos);
            if (bookshelf == null || targetSlot < 0 || targetSlot >= bookshelf.getContainerSize()) return false;

            ItemStack shelfStack = bookshelf.getItem(targetSlot);
            if (!shelfStack.isEmpty() && !(shelfStack.getItem() instanceof SpellBookItem)) return false;

            bookshelf.setItem(targetSlot, carried.copy());
            bookshelf.setChanged();
            this.setCarried(shelfStack.copy());
            return true;
        }

        if (!this.insertBookStackIntoNearbyBookshelf(carried)) return false;
        if (carried.isEmpty()) this.setCarried(ItemStack.EMPTY);
        return true;
    }

    /// Tries to insert as much of a spell-book stack as possible into nearby bookshelves.
    private boolean insertBookStackIntoNearbyBookshelf(ItemStack stack) {
        if (!(stack.getItem() instanceof SpellBookItem)) return false;

        BlockPos workbenchPos = this.getWorkbenchPos();
        if (workbenchPos == null) return false;

        boolean clientSide = this.player.level().isClientSide;
        boolean moved = false;
        for (BlockPos pos : LecternBookshelfUtils.findBookshelfPositions(this.player.level(), workbenchPos)) {
            Container bookshelf = LecternBookshelfUtils.getBookshelf(this.player.level(), pos);
            if (bookshelf == null) continue;

            // Fill compatible stacks before consuming a new bookshelf slot.
            for (int slot = 0; slot < bookshelf.getContainerSize() && !stack.isEmpty(); slot++) {
                ItemStack shelfStack = bookshelf.getItem(slot);
                if (!ItemStack.isSameItemSameTags(shelfStack, stack)) continue;

                int limit = Math.min(shelfStack.getMaxStackSize(), bookshelf.getMaxStackSize());
                int amount = Math.min(stack.getCount(), limit - shelfStack.getCount());
                if (amount <= 0) continue;

                if (!clientSide) {
                    shelfStack.grow(amount);
                    bookshelf.setChanged();
                }
                stack.shrink(amount);
                moved = true;
            }

            for (int slot = 0; slot < bookshelf.getContainerSize(); slot++) {
                if (stack.isEmpty()) return true;
                if (!bookshelf.getItem(slot).isEmpty()) continue;

                int amount = Math.min(stack.getCount(),
                        Math.min(stack.getMaxStackSize(), bookshelf.getMaxStackSize()));
                if (clientSide) {
                    stack.shrink(amount);
                } else {
                    bookshelf.setItem(slot, stack.split(amount));
                    bookshelf.setChanged();
                }
                moved = true;
            }
        }

        return moved;
    }

    /// Finds the range of slots that the given item can go into. This is used for shift-clicking. Returns null if
    /// there are no valid slots.
    ///
    /// @param stack The item stack to find slots for.
    /// @return An array of two integers, the first is the index of the first valid slot, the second is the index of
    /// the last valid slot (inclusive). Or null if there are no valid slots.
    public int @Nullable [] findSlotRangeForItem(ItemStack stack) {
        if (getSlot(0).mayPlace(stack)) {
            ItemStack centreStack = getSlot(CENTRE_SLOT).getItem();
            if (centreStack.getItem() instanceof IWorkbenchItem) {
                int spellSlots = ((IWorkbenchItem) centreStack.getItem()).getSpellSlotCount(centreStack);
                if (spellSlots > 0) return new int[]{0, spellSlots - 1};
            }
        } else if (getSlot(CRYSTAL_SLOT).mayPlace(stack)) return new int[]{CRYSTAL_SLOT, CRYSTAL_SLOT};
        else if (getSlot(CENTRE_SLOT).mayPlace(stack)) return new int[]{CENTRE_SLOT, CENTRE_SLOT};
        else if (getSlot(UPGRADE_SLOT).mayPlace(stack)) return new int[]{UPGRADE_SLOT, UPGRADE_SLOT};
        return null;
    }
}
