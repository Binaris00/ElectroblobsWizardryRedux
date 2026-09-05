package com.binaris.wizardry.content.recipe;

import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.content.item.BlankScrollItem;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class ArcaneWorkbenchRecipe {
    /// Center Slot (Only one Item)
    private final ItemStack centreStack;
    private final ItemStack book;
    /// Crystal Slot
    private final Ingredient crystals;
    /// Upgrade Slot
    private final Ingredient upgrades;
    /// Result Slot (Only one item)
    private final ItemStack result;
    /// Five Input Slots
    private final List<ItemStack> inputs;
    /// Input Slot Number (slot <= 5)
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
        if (origin.getItem() instanceof IWorkbenchItem) {
            slots = ((IWorkbenchItem) origin.getItem()).getSpellSlotCount(origin);
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

    /// Returns a list of item stacks, one for each type of crystal (regular, elemental, grand and shards), each with
    /// the minimum quantity needed to supply the given amount of mana. Types of crystal for which more than the max.
    /// stack size would be needed are ignored.
    @Deprecated
    public static List<ItemStack> generateCrystalStacks(int mana) {
        if (mana < 0)
            throw new IllegalArgumentException("Cannot create an arcane workbench recipe with negative mana!");

        if (mana == 0) return Collections.emptyList();

        List<ItemStack> crystalStacks = new ArrayList<>();

        // Normal Crystal
        int count = Mth.ceil((float) mana / EBServerConfig.MANA_PER_CRYSTAL.get());
        // A stack of crystals will almost certainly be enough mana, but you never know!
        // Using ItemStack.EMPTY to avoid deprecated method; crystals' stack size is not stack-sensitive so it doesn't matter
        if (count <= EBItems.MAGIC_CRYSTAL.get().getMaxStackSize()) {
            crystalStacks.addAll(Arrays.stream(Ingredient.of(EBTags.NORMAL_MAGIC_CRYSTAL).getItems()).toList());
        }

        // Small Crystal
        count = Mth.ceil((float) mana / EBServerConfig.MANA_PER_SHARD.get());

        if (count <= EBItems.MAGIC_CRYSTAL_SHARD.get().getMaxStackSize()) {
            crystalStacks.add(new ItemStack(EBItems.MAGIC_CRYSTAL_SHARD.get(), count));
        }

        // Grand Crystal
        count = Mth.ceil((float) mana / EBServerConfig.GRAND_CRYSTAL_MANA.get());

        if(count <= EBItems.MAGIC_CRYSTAL_GRAND.get().getMaxStackSize()){
            crystalStacks.add(new ItemStack(EBItems.MAGIC_CRYSTAL_GRAND.get(), count));
        }

        return crystalStacks;
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
