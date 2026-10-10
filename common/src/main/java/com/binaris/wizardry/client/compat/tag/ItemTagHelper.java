package com.binaris.wizardry.client.compat.tag;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class ItemTagHelper implements IIngredientHelper<ItemTag> {
    @Override
    public IIngredientType<ItemTag> getIngredientType() {
        return TagTypes.TAG_TYPE;
    }

    @Override
    public String getDisplayName(ItemTag itemTag) {
        ItemStack[] items = Ingredient.of(itemTag.tag()).getItems();
        for (ItemStack item : items) {
            return getDisplayName(item);
        }
        return "";
    }

    public String getDisplayName(ItemStack ingredient) {
        Component displayNameTextComponent = ingredient.getHoverName();
        return displayNameTextComponent.getString();
    }

    @Override
    public String getUniqueId(ItemTag itemTag, UidContext uidContext) {
        return "itemTagHelper";
    }

    @Override
    public ResourceLocation getResourceLocation(ItemTag itemTag) {
        return itemTag.tag().location();
    }

    @Override
    public ItemTag copyIngredient(ItemTag itemTag) {
        return itemTag;
    }

    @Override
    public String getErrorInfo(@Nullable ItemTag itemTag) {
        return "";
    }
}
