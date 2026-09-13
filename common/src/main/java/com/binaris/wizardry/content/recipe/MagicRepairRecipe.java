package com.binaris.wizardry.content.recipe;

import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.content.item.ManaFlaskItem;
import com.binaris.wizardry.setup.registries.EBRecipeTypes;
import com.binaris.wizardry.setup.registries.EBTags;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// TODO The magic repair formula has not been implemented.
/// @see net.minecraft.world.item.crafting.RepairItemRecipe RepairItemRecipe
public class MagicRepairRecipe extends CustomRecipe {

    public MagicRepairRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, @NotNull Level level) {
        if (container.getContainerSize() > 2) return false;
        if (container.getItem(0).isEmpty() && container.getItem(1).isEmpty()) return false;
        List<ItemStack> list = sort(container);
        return !list.isEmpty();
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, @NotNull RegistryAccess access) {
        if (container.getContainerSize() > 2) return ItemStack.EMPTY;
        if (container.getItem(0).isEmpty() && container.getItem(1).isEmpty()) return ItemStack.EMPTY;
        List<ItemStack> list = sort(container);
        if (list.isEmpty()) return ItemStack.EMPTY;
        // !list.isEmpty()
        ManaFlaskItem manaFlask = ((ManaFlaskItem) list.get(0).getItem());
        ItemStack manaItem = list.get(1);
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

    /// Index 1: "#ebwizardry:mana_flask"
    /// Index 2: "#ebwizardry:mana_item"
    public List<ItemStack> sort(CraftingContainer container) {
        List<ItemStack> list = new ArrayList<>();
        // (1): ManaFlask + ManaItem
        if (container.getItem(0).is(EBTags.MANA_FLASK) && container.getItem(1).is(EBTags.MANA_ITEM)) {
            list.add(container.getItem(0));
            list.add(container.getItem(1));
            return list;
        }
        // (2): ManaItem + ManaFlask
        else if (container.getItem(0).is(EBTags.MANA_ITEM) && container.getItem(1).is(EBTags.MANA_FLASK)) {
            list.add(container.getItem(1));
            list.add(container.getItem(0));
            return list;
        }
        return list;
    }
}
