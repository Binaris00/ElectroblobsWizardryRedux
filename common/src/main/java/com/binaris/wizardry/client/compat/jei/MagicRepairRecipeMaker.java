package com.binaris.wizardry.client.compat.jei;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.content.item.ManaFlaskItem;
import com.binaris.wizardry.setup.registries.EBTags;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class MagicRepairRecipeMaker {
    private static final String group = "jei.ebwizardry.magic_repair";

    public static List<CraftingRecipe> createRecipes() {
        List<CraftingRecipe> recipes = new ArrayList<>();
        ItemStack[] items1 = Ingredient.of(EBTags.MANA_FLASK).getItems();
        ItemStack[] items2 = Ingredient.of(EBTags.MANA_ITEM).getItems();
        for (ItemStack manaFlask : items1) {
            for (ItemStack manaItem : items2) {
                recipes.add(createRecipe(manaFlask, manaItem));
            }
        }
        return recipes;
    }

    private static CraftingRecipe createRecipe(ItemStack manaFlask, ItemStack manaItem) {
        NonNullList<Ingredient> inputs = NonNullList.create();
        Item item = manaItem.getItem();
        if (manaFlask.getItem() instanceof ManaFlaskItem && item instanceof IManaItem) {
            ((IManaItem) item).setMana(manaFlask, ((ManaFlaskItem) manaFlask.getItem()).getSize().capacity);
            inputs.add(0, Ingredient.of(manaFlask.getItem()));
            inputs.add(1, Ingredient.of(manaItem.getItem()));
        }
        ResourceLocation id = WizardryMainMod.location("magic_repair." + manaItem.getDescriptionId());
        return new ShapelessRecipe(id, group, CraftingBookCategory.MISC, manaItem, inputs);
    }
}
