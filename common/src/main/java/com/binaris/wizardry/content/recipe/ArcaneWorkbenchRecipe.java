package com.binaris.wizardry.content.recipe;

import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.content.item.BlankScrollItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class ArcaneWorkbenchRecipe {
    /// Center Slot (Only one Item)
    private final ItemStack centreStack;
    // Spell Book Slot (A book corresponds to one scroll.)
    private final ItemStack book;
    /// Crystal Slot
    private final Ingredient crystals;
    /// Upgrade Slot
    private final Ingredient upgrades;
    /// Result Slot (Only one item)
    private final ItemStack result;
    /// Five Input Slots
    private final List<ItemStack> inputs;
    /// Input Slot Number
    private final int slots;

    public ArcaneWorkbenchRecipe(ItemStack centreStack, List<ItemStack> inputs, ItemStack book, Ingredient crystals, Ingredient upgrades, ItemStack result, int slots) {
        this.centreStack = centreStack;
        this.inputs = inputs;
        this.book = book;
        this.crystals = crystals;
        this.upgrades = upgrades;
        this.result = result;
        this.slots = slots;
    }

    public static ArcaneWorkbenchRecipe recipe(ItemStack origin, Ingredient upgrades, Ingredient crystals, ItemStack result) {
        List<ItemStack> inputs = new ArrayList<>();
        int slots = 0;
        if (origin.getItem() instanceof IWorkbenchItem workbenchItem) {
            slots = workbenchItem.getSpellSlotCount(origin);
            for (int i = 0; i < slots; i++) inputs.add(ItemStack.EMPTY);
        }
        return new ArcaneWorkbenchRecipe(origin, inputs, ItemStack.EMPTY, crystals, upgrades, result, slots);
    }

    public static ArcaneWorkbenchRecipe recipe(ItemStack origin, ItemStack book, Ingredient crystals, ItemStack result) {
        List<ItemStack> inputs = new ArrayList<>();
        if (origin.getItem() instanceof BlankScrollItem)
            inputs.add(0, book);
        return new ArcaneWorkbenchRecipe(origin, inputs, book, crystals, Ingredient.EMPTY, result, 1);
    }

    public static ArcaneWorkbenchRecipe addUpgradeItem(ItemStack origin, Ingredient upgrades, ItemStack result) {
        return recipe(origin, upgrades, Ingredient.EMPTY, result);
    }

    public static ArcaneWorkbenchRecipe addMagicValue(ItemStack origin, Ingredient crystals, ItemStack result) {
        return recipe(origin, Ingredient.EMPTY, crystals, result);
    }

    public ItemStack getCentreStack() {
        return this.centreStack;
    }

    public Ingredient getCrystals() {
        return this.crystals;
    }

    public Ingredient getUpgrades() {
        return this.upgrades;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public List<ItemStack> getInputs() {
        return this.inputs;
    }

    public int getSlots() {
        return this.slots;
    }

    public ItemStack getBooks() {
        return this.book;
    }
}
