package com.electroblob.wizardry.content.menu;

import com.electroblob.wizardry.api.content.event.SpellBindEvent;
import com.electroblob.wizardry.api.content.item.IWorkbenchItem;
import com.electroblob.wizardry.client.EBClientConstants;
import com.electroblob.wizardry.content.item.SpellBookItem;
import com.electroblob.wizardry.content.menu.slot.SlotItemClassList;
import com.electroblob.wizardry.content.menu.slot.SlotItemList;
import com.electroblob.wizardry.content.menu.slot.SlotWorkbenchItem;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import com.electroblob.wizardry.core.mixin.accessor.SlotAccessor;
import com.electroblob.wizardry.setup.registries.EBAdvancementTriggers;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBMenus;
import com.electroblob.wizardry.setup.registries.WandUpgrades;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
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

/**
 * Where <i>Part</i> of the magic happens. This class handles the server-side logic of the Arcane Workbench GUI,
 * including the slots, shift-clicking, and the apply and clear buttons. Most of this work is delegated to the workbench
 * item and the changes between when is a wand is in the centre slot and when there isn't one are handled here.
 *
 * @see IWorkbenchItem
 *
 */
public class ArcaneWorkbenchMenu extends AbstractContainerMenu {
    public static final int CRYSTAL_SLOT = 8;
    public static final int CENTRE_SLOT = 9;
    public static final int UPGRADE_SLOT = 10;
    public static final int SLOT_RADIUS = 42;
    public static final int PLAYER_INVENTORY_SIZE = 36;
    public Container container;
    public boolean needsRefresh;

    public ArcaneWorkbenchMenu(int i, Inventory playerInv) {
        this(i, playerInv, new SimpleContainer(11));
    }

    public ArcaneWorkbenchMenu(int id, Inventory inventory, Container container) {
        super(EBMenus.ARCANE_WORKBENCH_MENU.get(), id);
        this.container = container;
        ItemStack wand = container.getItem(CENTRE_SLOT);

        // Spell Book Slots
        for (int i = 0; i < 8; i++)
            addSlot(new SlotItemClassList(container, i, -999, -999, 1, SpellBookItem.class));

        // Crystal and Workbench Slots
        addSlot(new SlotItemList(container, CRYSTAL_SLOT, 13, 101, 64, EBClientConstants.ARCANE_WORKBENCH_EMPTY_SLOT_CRYSTAL,
                EBItems.MAGIC_CRYSTAL.get(), EBItems.MAGIC_CRYSTAL_SHARD.get(), EBItems.MAGIC_CRYSTAL_GRAND.get()));
        addSlot(new SlotWorkbenchItem(container, CENTRE_SLOT, 80, 64, this));

        // Upgrade Slot
        Set<Item> upgrades = new HashSet<>();
        WandUpgrades.getSpecialUpgrades().forEach(d -> upgrades.add(d.get()));
        upgrades.add(EBItems.ARCANE_TOME.get());
        upgrades.add(EBItems.RESPLENDENT_THREAD.get());
        upgrades.add(EBItems.CRYSTAL_SILVER_PLATING.get());
        upgrades.add(EBItems.ETHEREAL_CRYSTAL_WEAVE.get());
        addSlot(new SlotItemList(container, UPGRADE_SLOT, 147, 17, 1, EBClientConstants.ARCANE_WORKBENCH_EMPTY_SLOT_UPGRADE, upgrades.toArray(new Item[0])));

        // Player Inventory Slots
        for (int x = 0; x < 9; x++)
            addSlot(new Slot(inventory, x, 8 + x * 18, 196));
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 9; x++)
                addSlot(new Slot(inventory, 9 + x + y * 9, 8 + x * 18, 138 + y * 18));

        onSlotChanged(CENTRE_SLOT, wand, null);
    }

    /**
     * Returns the X offset of the book slot at the given index, this is all assuming the slots are arranged in a circle
     * around the centre slot.
     *
     * @param i             The index of the book slot (0-7).
     * @param bookSlotCount The number of book slots to arrange in a circle (1 - 8).
     * @return The X offset of the book slot at the given index.
     */
    private static int getBookSlotXOffset(int i, int bookSlotCount) {
        float angle = i * (2 * (float) Math.PI) / bookSlotCount;
        return Math.round(SLOT_RADIUS * Mth.sin(angle));
    }

    /**
     * Returns the Y offset of the book slot at the given index, this is all assuming the slots are arranged in a circle
     * around the centre slot.
     *
     * @param i             The index of the book slot (0-7).
     * @param bookSlotCount The number of book slots to arrange in a circle (1 - 8).
     * @return The Y offset of the book slot at the given index.
     */
    private static int getBookSlotYOffset(int i, int bookSlotCount) {
        float angle = i * (2 * (float) Math.PI) / bookSlotCount;
        return Math.round(SLOT_RADIUS * -Mth.cos(angle));
    }

    /**
     * Handles shift-clicking. Attempts to move items between the workbench slots and the player inventory. If the item
     * can go in multiple workbench slots (e.g. spell books), it will try to put it in the first available one. If it
     * can't go in any workbench slots, it will try to move it between the hotbar and main inventory.
     *
     * @param player The player using the workbench.
     * @param index  The ID of the slot that was shift-clicked.
     * @return The rest of the stack that couldn't be moved, or ItemStack.EMPTY if the entire stack was moved.
     */
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) return itemstack;

        ItemStack itemstack1 = slot.getItem();
        itemstack = itemstack1.copy();

        // If shift-clicking from workbench slots (0-10)
        if (index < 11) {
            // Try to move to player inventory (slots 11-46)
            if (!this.moveItemStackTo(itemstack1, 11, 11 + PLAYER_INVENTORY_SIZE, true)) {
                return ItemStack.EMPTY;
            }
        }
        // If shift-clicking from player inventory
        else if (index >= 11 && index < 11 + PLAYER_INVENTORY_SIZE) {
            int[] slotRange = findSlotRangeForItem(itemstack1);

            if (slotRange != null) {
                // Try to move to the appropriate workbench slot(s)
                if (!this.moveItemStackTo(itemstack1, slotRange[0], slotRange[1] + 1, false)) {
                    // If that fails, try moving between hotbar and main inventory
                    if (index < 38) {
                        if (!this.moveItemStackTo(itemstack1, 38, 47, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, 11, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // No valid workbench slot, move between hotbar and main inventory
                if (index < 38) {
                    if (!this.moveItemStackTo(itemstack1, 38, 47, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 11, 38, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (itemstack1.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, itemstack1);

        return itemstack;
    }


    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);
    }

    /**
     * Called when a slot is changed. If the centre slot is changed, this updates the visibility and position of the
     * spell book slots. (Because there's no point in showing them if there's no wand in the centre slot). Depending of
     * the wand upgrade, the number of spell slots may vary, so the spell book slots are arranged in a circle around
     * the centre slot.
     *
     * @param slotNumber The slot number that changed.
     * @param stack      The new stack in that slot.
     * @param player     The player viewing the arcane workbench.
     */
    public void onSlotChanged(int slotNumber, ItemStack stack, Player player) {
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


    /**
     * Called when the apply button is pressed. Delegates the logic to the workbench item in the centre slot, if
     * there is one. Normally it will be a wand, in that case, it applies the spells from the spell book slots to the
     * wand.
     *
     * @param player The player using the workbench.
     */
    public void onApplyButtonPressed(Player player) {
        if (player.level().isClientSide) return;
        if (WizardryEventBus.getInstance().fire(new SpellBindEvent(player, this))) return;
        Slot centre = this.getSlot(CENTRE_SLOT);

        if (centre.getItem().getItem() instanceof IWorkbenchItem workbenchItem) {
            Slot[] spellBooks = this.slots.subList(0, 8).toArray(new Slot[8]);

            if (workbenchItem.onApplyButtonPressed(player, centre, this.getSlot(CRYSTAL_SLOT), this.getSlot(UPGRADE_SLOT), spellBooks)) {
                if (player instanceof ServerPlayer serverPlayer) EBAdvancementTriggers.ARCANE_WORKBENCH.trigger(serverPlayer, centre.getItem());
            }
        }
    }

    /**
     * Called when the clear button is pressed. Delegates the logic to the workbench item in the centre slot, if
     * there is one. Normally it will be a wand, in that case, it clears all spells from the wand.
     *
     * @param player The player using the workbench.
     */
    public void onClearButtonPressed(Player player) {
        Slot centre = this.getSlot(CENTRE_SLOT);

        if (centre.getItem().getItem() instanceof IWorkbenchItem workbenchItem) {
            Slot[] spellBooks = this.slots.subList(0, 8).toArray(new Slot[8]);
            workbenchItem.onClearButtonPressed(player, centre, this.getSlot(CRYSTAL_SLOT), this.getSlot(UPGRADE_SLOT), spellBooks);
        }
    }

    /**
     * Makes the slot at the given index visible and sets its position to the given coordinates.
     *
     * @param index The index of the slot to show.
     * @param x     The x position to set the slot to.
     * @param y     The y position to set the slot to.
     */
    private void showSlot(int index, int x, int y) {
        Slot slot = this.getSlot(index);
        ((SlotAccessor) slot).setX(x);
        ((SlotAccessor) slot).setY(y);
    }

    /**
     * Hides the slot at the given index by moving it off-screen. If there is an item in the slot, it tries to move
     * it into the player's inventory, and if that fails, it drops it on the ground.
     *
     * @param index  The index of the slot to hide.
     * @param player The player using the workbench.
     */
    private void hideSlot(int index, Player player) {
        Slot slot = this.getSlot(index);
        ((SlotAccessor) slot).setX(-999);
        ((SlotAccessor) slot).setY(-999);
        ItemStack stack = slot.getItem();
        ItemStack rest = this.quickMoveStack(player, index);
        if (rest == ItemStack.EMPTY && stack != ItemStack.EMPTY) {
            slot.set(ItemStack.EMPTY);
            player.drop(stack, false);
        }
    }


    /**
     * Finds the range of slots that the given item can go into. This is used for shift-clicking. Returns null if
     * there are no valid slots.
     *
     * @param stack The item stack to find slots for.
     * @return An array of two integers, the first is the index of the first valid slot, the second is the index of
     * the last valid slot (inclusive). Or null if there are no valid slots.
     */
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
