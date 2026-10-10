package com.binaris.wizardry.client.compat.tag;

import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class TagTypes {
    private TagTypes() {}

    public static final IIngredientTypeWithSubtypes<Ingredient, ItemTag> TAG_TYPE = new IIngredientTypeWithSubtypes<>() {
        @Override
        public @NotNull Class<? extends ItemTag> getIngredientClass() {
            return ItemTag.class;
        }

        @Override
        public @NotNull Class<? extends Ingredient> getIngredientBaseClass() {
            return Ingredient.class;
        }

        @Override
        public @NotNull Ingredient getBase(ItemTag itemTag) {
            return Ingredient.of(itemTag.tag());
        }
    };
}
