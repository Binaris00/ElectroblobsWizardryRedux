package com.binaris.wizardry.client.compat.jei;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.recipe.ImbuementAltarRecipe;
import com.binaris.wizardry.setup.registries.EBBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/// JEI recipe category implementation for all 'recipes' in the imbuement altar.
public class ImbuementAltarCategory implements IRecipeCategory<ImbuementAltarRecipe> {
    public static final ResourceLocation TEXTURE = WizardryMainMod.location("textures/integration/jei/imbuement_altar_background.png");
    public static final RecipeType<ImbuementAltarRecipe> IMBUE_TYPE = new RecipeType<>(WizardryMainMod.location("imbuement_altar"), ImbuementAltarRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ImbuementAltarCategory(IGuiHelper helper) {
        // Imbuement Altar Texture Display
        this.background = helper.createDrawable(TEXTURE, 0, 0, 134, 74);
        // Imbuement Altar Block
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EBBlocks.IMBUEMENT_ALTAR.get()));
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull RecipeType<ImbuementAltarRecipe> getRecipeType() {
        return IMBUE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.better_ebwizardry.imbuement_altar.title");
    }

    /// Imbuement Altar Block Icon
    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(ImbuementAltarRecipe recipe, IRecipeSlotsView view, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ImbuementAltarRecipe recipe, IFocusGroup group) {
        // Output Material
        builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 29)
                .addItemStack(recipe.getResultItem(null));
        // Center Material
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 29)
                .addIngredients(recipe.getCenterIngredient());
        // All Receptacle Ingredients
        NonNullList<Ingredient> list = recipe.getReceptacleIngredients();
        // Round Material
        if (list != null && list.size() == 4) {
            builder.addSlot(RecipeIngredientRole.INPUT, 29, 1)
                    .addIngredients(list.get(0));
            builder.addSlot(RecipeIngredientRole.INPUT, 57, 29)
                    .addIngredients(list.get(1));
            builder.addSlot(RecipeIngredientRole.INPUT, 29, 57)
                    .addIngredients(list.get(2));
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 29)
                    .addIngredients(list.get(3));
        }
    }
}
