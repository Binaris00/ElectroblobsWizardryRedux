package com.electroblob.wizardry.content.menu.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;


/** Slot to save a list of available items to add with it and a custom no-item-texture */
public class SlotItemList extends Slot {
    private final Item[] items;
    private final int stackLimit;
    private final Pair<ResourceLocation, ResourceLocation> locations;

    public SlotItemList(Container inventory, int index, int x, int y, int stackLimit, ResourceLocation location, Item... allowedItems) {
        super(inventory, index, x, y);
        this.items = allowedItems;
        this.stackLimit = stackLimit;
        this.locations = new Pair<>(InventoryMenu.BLOCK_ATLAS, location);
    }

    @Override
    public int getMaxStackSize() {
        return this.stackLimit;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return Arrays.stream(items).anyMatch(item -> stack.getItem() == item);
    }

    @Nullable
    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return locations;
    }
}
