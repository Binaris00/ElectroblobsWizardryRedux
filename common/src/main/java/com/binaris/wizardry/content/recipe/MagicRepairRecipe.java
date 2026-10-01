package com.binaris.wizardry.content.recipe;

import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.content.item.ManaFlaskItem;
import com.binaris.wizardry.setup.registries.EBRecipeTypes;
import com.binaris.wizardry.setup.registries.EBTags;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/// @see net.minecraft.world.item.crafting.RepairItemRecipe RepairItemRecipe
public class MagicRepairRecipe extends CustomRecipe {

    public MagicRepairRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, @NotNull Level level) {
        List<ItemStack> inputs = getInputs(container);
        if (inputs.size() != 2) return false;
        return !sort(inputs).isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, @NotNull RegistryAccess access) {
        List<ItemStack> inputs = getInputs(container);
        if (inputs.size() != 2) return ItemStack.EMPTY;
        List<ItemStack> list = sort(inputs);
        if (list.isEmpty()) return ItemStack.EMPTY;
        // !list.isEmpty()
        ManaFlaskItem manaFlask = ((ManaFlaskItem) list.get(0).getItem());
        ItemStack manaItem = list.get(1).copy();
        if (manaItem.getItem() instanceof IManaItem) {
            ((IManaItem) manaItem.getItem()).setMana(manaItem, manaFlask.size.capacity);
            return manaItem;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height == 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return EBRecipeTypes.MAGIC_REPAIR_SERIALIZER;
    }

    public List<ItemStack> getInputs(CraftingContainer container) {
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (container.getItem(i).is(Items.AIR)) continue;
            inputs.add(container.getItem(i));
        }
        return inputs;
    }

    /// Index 1: "#ebwizardry:mana_flask"
    /// Index 2: "#ebwizardry:mana_item"
    public List<ItemStack> sort(List<ItemStack> inputs) {
        List<ItemStack> list = new ArrayList<>();
        // (1): ManaFlask + ManaItem
        if (inputs.get(0).is(EBTags.MANA_FLASK) && inputs.get(1).is(EBTags.MANA_ITEM)) {
            list.add(inputs.get(0));
            list.add(inputs.get(1));
            return list;
        }
        // (2): ManaItem + ManaFlask
        else if (inputs.get(0).is(EBTags.MANA_ITEM) && inputs.get(1).is(EBTags.MANA_FLASK)) {
            list.add(inputs.get(1));
            list.add(inputs.get(0));
            return list;
        }
        return list;
    }
}
