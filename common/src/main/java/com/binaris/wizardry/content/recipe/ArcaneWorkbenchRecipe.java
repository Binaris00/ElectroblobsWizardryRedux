package com.binaris.wizardry.content.recipe;

import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArcaneWorkbenchRecipe {
    private final ItemStack centreStack;
    private final List<ItemStack> books;
    private final List<ItemStack> crystals;
    private final List<ItemStack> upgrades;
    private final ItemStack result;

    private final int bookSlots;

    private final List<List<ItemStack>> inputs;

    public ArcaneWorkbenchRecipe(ItemStack centreStack, List<ItemStack> books, List<ItemStack> crystals, List<ItemStack> upgrades, ItemStack result) {
        this.centreStack = centreStack;
        this.books = books; // CAUTION! This list is an OUTER LIST!
        this.crystals = crystals;
        this.upgrades = upgrades;
        this.result = result;

        this.inputs = new ArrayList<>();
        for(ItemStack book : books)
            this.inputs.add(Collections.singletonList(book));
        this.inputs.add(crystals);
        this.inputs.add(Collections.singletonList(centreStack));
        this.inputs.add(upgrades);

        if (centreStack.getItem() instanceof IWorkbenchItem) {
            bookSlots = ((IWorkbenchItem)centreStack.getItem()).getSpellSlotCount(centreStack);
        } else {
            bookSlots = 0;
        }
    }

    public ArcaneWorkbenchRecipe(ItemStack centreStack, List<ItemStack> books, int mana, List<ItemStack> upgrades, ItemStack result){
        this(centreStack, books, generateCrystalStacks(mana), upgrades, result);
    }

    /**
     * Returns a list of item stacks, one for each type of crystal (regular, elemental, grand and shards), each with
     * the minimum quantity needed to supply the given amount of mana. Types of crystal for which more than the max.
     * stack size would be needed are ignored.
     */
    public static List<ItemStack> generateCrystalStacks(int mana) {
        if (mana < 0)
            throw new IllegalArgumentException("Cannot create an arcane workbench recipe with negative mana!");

        if (mana == 0) return Collections.emptyList();

        List<ItemStack> crystalStacks = new ArrayList<>();

        // Normal Crystal
        int count = Mth.ceil((float) mana / EBServerConfig.MANA_PER_CRYSTAL.get());
        // A stack of crystals will almost certainly be enough mana, but you never know!
        // Using ItemStack.EMPTY to avoid deprecated method; crystals' stack size is not stack-sensitive so it doesn't matter
        if (count <= EBItems.MAGIC_CRYSTAL.get().getMaxStackSize()){
            getAllCrystal(crystalStacks);
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
        return centreStack;
    }

    public int getBookSlots() {
        return bookSlots;
    }

    public List<ItemStack> getCrystals() {
        return crystals;
    }

    public List<ItemStack> getUpgrades() {
        return upgrades;
    }

    public ItemStack getResult() {
        return result;
    }

    public List<List<ItemStack>> getInputs() {
        return inputs;
    }

    public List<ItemStack> getBooks() {
        return books;
    }

    public static void getAllCrystal(List<ItemStack> crystals) {
        crystals.add(new ItemStack(EBItems.MAGIC_CRYSTAL.get()));
        crystals.add(new ItemStack(EBItems.MAGIC_CRYSTAL_FIRE.get()));
        crystals.add(new ItemStack(EBItems.MAGIC_CRYSTAL_LIGHTNING.get()));
        crystals.add(new ItemStack(EBItems.MAGIC_CRYSTAL_NECROMANCY.get()));
        crystals.add(new ItemStack(EBItems.MAGIC_CRYSTAL_EARTH.get()));
        crystals.add(new ItemStack(EBItems.MAGIC_CRYSTAL_SORCERY.get()));
        crystals.add(new ItemStack(EBItems.MAGIC_CRYSTAL_HEALING.get()));
        crystals.add(new ItemStack(EBItems.MAGIC_CRYSTAL_ICE.get()));
    }
}
