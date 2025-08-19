package com.electroblob.wizardry.content.menu;

import com.electroblob.wizardry.api.content.item.IWorkbenchItem;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.content.blockentity.ISpellSortable;
import com.electroblob.wizardry.content.item.SpellBookItem;
import com.electroblob.wizardry.content.menu.slot.*;
import com.electroblob.wizardry.core.mixin.accessor.SlotAccessor;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.EBMenus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/** Todo terrain of nobody, very old logic for making arcane workbench buttons and effects */
public class ArcaneWorkbenchMenu extends AbstractContainerMenu implements ISpellSortable {
    public Container container;

    public static final ResourceLocation EMPTY_SLOT_CRYSTAL = new ResourceLocation("item/empty_slot_crystal");
    public static final ResourceLocation EMPTY_SLOT_UPGRADE = new ResourceLocation("item/empty_slot_upgrade");

    public static final int CRYSTAL_SLOT = 8;
    public static final int CENTRE_SLOT = 9;
    public static final int UPGRADE_SLOT = 10;

    public static final int SLOT_RADIUS = 42;

    public static final int BOOKSHELF_SLOTS_X = 5;
    public static final int BOOKSHELF_SLOTS_Y = 10;

    public static final int PLAYER_INVENTORY_SIZE = 36;

    public static final int BOOKSHELF_UI_WIDTH = 122;

    private final List<VirtualSlot> bookshelfSlots = new ArrayList<>();
    private final List<VirtualSlot> activeBookshelfSlots = new ArrayList<>();

    private boolean hasBookshelves;

    private int scroll = 0;
    private ISpellSortable.SortType sortType = ISpellSortable.SortType.TIER;
    private boolean sortDescending = false;
    private String searchText = "";

    public boolean needsRefresh;

    // Starting the arcane workbench
    public ArcaneWorkbenchMenu(int i, Inventory playerInv) {
        this(i, playerInv, new SimpleContainer(11));
    }

    @SuppressWarnings("unchecked")
    public ArcaneWorkbenchMenu(int id, Inventory inventory, Container container) {
        super(EBMenus.ARCANE_WORKBENCH_MENU.get(), id);
        this.container = container;

        ItemStack wand = container.getItem(CENTRE_SLOT);

        // Spell Book Slots
        for (int i = 0; i < 8; i++) {
            Slot slot = new SlotItemClassList(container, i, -999, -999, 1, SpellBookItem.class);
            this.addSlot(slot);
        }

        // Crystal Slot and Workbench Item (Wands or scrolls normally)
        this.addSlot(new SlotItemList(container, CRYSTAL_SLOT, 13, 101, 64, EMPTY_SLOT_CRYSTAL, EBItems.MAGIC_CRYSTAL.get(), EBItems.MAGIC_CRYSTAL_SHARD.get(), EBItems.MAGIC_CRYSTAL_GRAND.get()));
        this.addSlot(new SlotWorkbenchItem(container, CENTRE_SLOT, 80, 64, this));

        // Upgrade Slot
        Set<Item> upgrades = new HashSet<>();
        WandHelper.getSpecialUpgrades().forEach(deferred -> upgrades.add(deferred.get()));

        upgrades.add(EBItems.ARCANE_TOME.get());
        upgrades.add(EBItems.RESPLENDENT_THREAD.get());
        upgrades.add(EBItems.CRYSTAL_SILVER_PLATING.get());
        upgrades.add(EBItems.ETHEREAL_CRYSTAL_WEAVE.get());

        this.addSlot(new SlotItemList(container, UPGRADE_SLOT, 147, 17, 1, EMPTY_SLOT_UPGRADE, upgrades.toArray(new Item[0])));


        // Player inv
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inventory, x, 8 + x * 18, 196));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(inventory, 9 + x + y * 9, 8 + x * 18, 138 + y * 18));
            }
        }

        // TODO Bookshelf slots
        for (int y = 0; y < BOOKSHELF_SLOTS_Y; y++) {
            for (int x = 0; x < BOOKSHELF_SLOTS_X; x++) {
                int index = x + y * BOOKSHELF_SLOTS_X;
                this.addSlot(new SlotBookList(container, UPGRADE_SLOT + 1 + index, 8 + x * 18, 34 + y * 18, this, index));
            }
        }


        refreshBookshelfSlots();
        this.onSlotChanged(CENTRE_SLOT, wand, null);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);
    }

    private void showSlot(int index, int x, int y) {
        Slot slot = this.getSlot(index);
        ((SlotAccessor) slot).setX(x);
        ((SlotAccessor) slot).setY(y);
    }

    private void hideSlot(int index, Player player) {
        Slot slot = this.getSlot(index);

        ((SlotAccessor) slot).setX(-999);
        ((SlotAccessor) slot).setY(-999);

        ItemStack stack = slot.getItem();

        ItemStack remainder = this.quickMoveStack(player, index);

        if (remainder == ItemStack.EMPTY && stack != ItemStack.EMPTY) {
            slot.set(ItemStack.EMPTY);
            player.drop(stack, false);
        }
    }

    public void onSlotChanged(int slotNumber, ItemStack stack, Player player) {
        if (slotNumber == CENTRE_SLOT) {
            if (stack.isEmpty()) {
                for (int i = 0; i < CRYSTAL_SLOT; i++) {
                    this.hideSlot(i, player);
                }
            } else {
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
        }
    }

    public static int getBookSlotXOffset(int i, int bookSlotCount) {
        float angle = i * (2 * (float) Math.PI) / bookSlotCount;
        return Math.round(SLOT_RADIUS * Mth.sin(angle));
    }

    public static int getBookSlotYOffset(int i, int bookSlotCount) {
        float angle = i * (2 * (float) Math.PI) / bookSlotCount;
        return Math.round(SLOT_RADIUS * -Mth.cos(angle));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int clickedSlotId) {
        ItemStack remainder = ItemStack.EMPTY;
        Slot slot = this.slots.get(clickedSlotId);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            remainder = stack.copy();

            if (clickedSlotId <= UPGRADE_SLOT) {
                if (!mergeStackIntoBookshelves(stack)) {
                    if (!this.moveItemStackTo(stack, UPGRADE_SLOT + 1, UPGRADE_SLOT + 1 + PLAYER_INVENTORY_SIZE, true)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (getSlot(clickedSlotId) instanceof VirtualSlot) {
                int[] slotRange = findSlotRangeForItem(stack);

                if (slotRange == null || !this.moveItemStackTo(stack, slotRange[0], slotRange[1] + 1, false)) {
                    if (!this.moveItemStackTo(stack, UPGRADE_SLOT + 1, UPGRADE_SLOT + 1 + PLAYER_INVENTORY_SIZE, true)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                int[] slotRange = findSlotRangeForItem(stack);

                if (slotRange == null || !this.moveItemStackTo(stack, slotRange[0], slotRange[1] + 1, false)) {
                    if (!mergeStackIntoBookshelves(stack)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (stack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == remainder.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return remainder;
    }

    private int @Nullable [] findSlotRangeForItem(ItemStack stack) {
        if (this.getSlot(0).mayPlace(stack)) {
            ItemStack centreStack = getSlot(CENTRE_SLOT).getItem();

            if (centreStack.getItem() instanceof IWorkbenchItem) {
                int spellSlots = ((IWorkbenchItem) centreStack.getItem()).getSpellSlotCount(centreStack);
                if (spellSlots > 0) {
                    return new int[]{0, spellSlots - 1};
                }
            }

        } else if (getSlot(CRYSTAL_SLOT).mayPlace(stack)) {
            return new int[]{CRYSTAL_SLOT, CRYSTAL_SLOT};
        } else if (getSlot(CENTRE_SLOT).mayPlace(stack)) {
            return new int[]{CENTRE_SLOT, CENTRE_SLOT};
        } else if (getSlot(UPGRADE_SLOT).mayPlace(stack)) {
            return new int[]{UPGRADE_SLOT, UPGRADE_SLOT};
        }
        return null;
    }

    @Override
    public void clicked(int slotId, int p_150401_, ClickType p_150402_, Player player) {
        if (slotId > 0 && getSlot(slotId) instanceof SlotBookList) {
            ItemStack stack = player.getInventory().getSelected();

            if (!stack.isEmpty() && !getBookshelfSlots().isEmpty()) {
                mergeStackIntoBookshelves(stack);
            }
        }
        super.clicked(slotId, p_150401_, p_150402_, player);
    }

    @SuppressWarnings("resource")
    private boolean mergeStackIntoBookshelves(ItemStack stack) {
        // TODO
        //if (container.getLevel().isClientSide) this.needsRefresh = true;

        Set<VirtualSlot> slots = new LinkedHashSet<>(bookshelfSlots.size());

        slots.addAll(bookshelfSlots.stream().filter(s -> InventoryUtil.canMerge(stack, s.getItem())).collect(Collectors.toList()));
        slots.addAll(bookshelfSlots.stream().filter(s -> InventoryUtil.canMerge(stack, s.getPrevStack())).collect(Collectors.toList()));
        slots.addAll(bookshelfSlots.stream().filter(s -> s.getPrevStack().isEmpty()).collect(Collectors.toList()));
        slots.addAll(bookshelfSlots.stream().filter(s -> !s.hasItem()).collect(Collectors.toList()));
        slots.removeIf(s -> !s.mayPlace(stack));

        for (Slot slot : slots) {
            ItemStack contents = slot.getItem();

            if (contents.isEmpty()) {
                slot.set(stack.split(contents.getMaxStackSize()));

                if (stack.isEmpty()) return true;
            } else {
                int totalItemCount = contents.getCount() + stack.getCount();
                int maxSize = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());

                if (totalItemCount <= maxSize) {
                    stack.setCount(0);
                    contents.setCount(totalItemCount);
                    slot.setChanged();
                    return true;
                } else if (contents.getCount() < maxSize) {
                    stack.shrink(maxSize - contents.getCount());
                    contents.setCount(maxSize);
                    slot.setChanged();
                }
            }
        }

        return false;
    }

    public void onApplyButtonPressed(Player player) {
        // TODO SPELL BIND EVENT
        //if (MinecraftForge.EVENT_BUS.post(new SpellBindEvent(player, this))) return;

        Slot centre = this.getSlot(CENTRE_SLOT);

        if (centre.getItem().getItem() instanceof IWorkbenchItem) {
            Slot[] spellBooks = this.slots.subList(0, 8).toArray(new Slot[8]);

            if (((IWorkbenchItem) centre.getItem().getItem()).onApplyButtonPressed(player, centre, this.getSlot(CRYSTAL_SLOT), this.getSlot(UPGRADE_SLOT), spellBooks)) {
                if (player instanceof ServerPlayer) {
                    // TODO ADVANCEMENT
                    //WizardryAdvancementTriggers.ARCANE_WORKBENCH.trigger((ServerPlayer) player, centre.getItem());
                }
            }
        }
    }

    public void onClearButtonPressed(Player player) {
        Slot centre = this.getSlot(CENTRE_SLOT);

        if (centre.getItem().getItem() instanceof IWorkbenchItem) {
            Slot[] spellBooks = this.slots.subList(0, 8).toArray(new Slot[8]);

            ((IWorkbenchItem) centre.getItem().getItem()).onClearButtonPressed(player, centre, this.getSlot(CRYSTAL_SLOT), this.getSlot(UPGRADE_SLOT), spellBooks);
        }
    }

    public void scrollTo(int row) {
        this.scroll = row;
    }

    public void setSortType(ISpellSortable.SortType sortType) {
        if (this.sortType == sortType) {
            this.sortDescending = !this.sortDescending;
        } else {
            this.sortType = sortType;
            this.sortDescending = false;
        }

        updateActiveBookshelfSlots();
    }

    @Override
    public ISpellSortable.SortType getSortType() {
        return sortType;
    }

    @Override
    public boolean isSortDescending() {
        return sortDescending;
    }

    public void setSearchText(@NotNull String searchText) {
        this.searchText = searchText;
        this.scrollTo(0);
        updateActiveBookshelfSlots();
    }

    public List<VirtualSlot> getBookshelfSlots() {
        List<VirtualSlot> validSlots = new ArrayList<>(bookshelfSlots);
        validSlots.removeIf(s -> !s.isValid());
        return validSlots;
    }

    public void updateActiveBookshelfSlots() {
        // TODO BOOK SHELFS
//        activeBookshelfSlots = bookshelfSlots.stream().filter(s -> s.isValid() && !s.getItem().isEmpty()
//                        && this.getSlot(0).mayPlace(s.getItem())
//                        && Spell.byMetadata(((IMetadata) s.getItem().getItem()).getMetadata(s.getItem())).matches(searchText))
//                .sorted(Comparator.comparing(s -> Spell.byMetadata(((IMetadata) ((Slot) s).getItem().getItem()).getMetadata(s.getItem())),
//                        sortDescending ? sortType.comparator.reversed() : sortType.comparator))
//                .collect(Collectors.toList());
    }

    public List<VirtualSlot> getActiveBookshelfSlots() {
        return activeBookshelfSlots;
    }

    public List<VirtualSlot> getVisibleBookshelfSlots() {
        List<VirtualSlot> activeSlots = getActiveBookshelfSlots();
        return activeSlots.subList(BOOKSHELF_SLOTS_X * scroll, activeSlots.size());
    }

    public boolean hasBookshelves() {
        return hasBookshelves;
    }

    public void refreshBookshelfSlots() {
        this.slots.removeIf(s -> s instanceof VirtualSlot && !((VirtualSlot) s).isValid());
        bookshelfSlots.removeIf(s -> !s.isValid());
        // TODO BOOK SHELFS

//        List<Container> bookshelves = BlockBookshelf.findNearbyBookshelves(blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity);
//
//        if (bookshelves.isEmpty() == hasBookshelves) {
//            for (Slot slot : this.slots) {
//                if (!(slot instanceof SlotBookList || slot instanceof VirtualSlot)) {
//                    int slotX = ObfuscationReflectionHelper.getPrivateValue(Slot.class, slot, "f_40220_");
//                    int width = bookshelves.isEmpty() ? -BOOKSHELF_UI_WIDTH : BOOKSHELF_UI_WIDTH;
//                    ObfuscationReflectionHelper.setPrivateValue(Slot.class, slot, slotX + width, "f_40220_");
//                }
//            }
//
//            hasBookshelves = !bookshelves.isEmpty();
//        }

//        bookshelves.removeIf(b -> bookshelfSlots.stream().anyMatch(s -> s.container == b));
//
//        if (!bookshelves.isEmpty()) {
//            for (Container bookshelf : bookshelves) {
//                for (int i = 0; i < bookshelf.getContainerSize(); i++) {
//                    VirtualSlot slot = new VirtualSlot(bookshelf, i);
//                    bookshelfSlots.add(slot);
//                    this.addSlot(slot);
//                }
//            }
//        }

        //TODO
        //if (container.getLevel().isClientSide) updateActiveBookshelfSlots();
    }
}
