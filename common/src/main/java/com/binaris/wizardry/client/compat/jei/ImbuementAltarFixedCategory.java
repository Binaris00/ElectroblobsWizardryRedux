package com.binaris.wizardry.client.compat.jei;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.client.compat.tag.ItemTag;
import com.binaris.wizardry.client.compat.tag.TagTypes;
import com.binaris.wizardry.content.recipe.ImbuementAltarRecipe;
import com.binaris.wizardry.content.recipe.ImbuementAltarRecipe.Category;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ImbuementAltarFixedCategory implements IRecipeCategory<ImbuementAltarRecipe> {
    public static final ResourceLocation TEXTURE = WizardryMainMod.location("textures/integration/jei/imbuement_altar_background.png");
    public static final RecipeType<ImbuementAltarRecipe> FIX_TYPE = new RecipeType<>(WizardryMainMod.location("imbuement_altar_fixed"), ImbuementAltarRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public ImbuementAltarFixedCategory(IGuiHelper helper) {
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
        return FIX_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.better_ebwizardry.imbuement_altar_fixed.title");
    }

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
        if (!Category.FIX.equals(recipe.getCategory())) return;
        // Output Materials (Results)
        builder.addSlot(RecipeIngredientRole.OUTPUT,  113, 29)
                .addItemStacks(recipe.getResults().stream().toList());
        // Center Material
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 29)
                .addIngredients(recipe.getCenterIngredient());
        // All Poll Ingredients
        List<TagKey<Item>> polls = Objects.requireNonNull(recipe.getPoll());
        // Four Receptacle Ingredients
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 1)
                .addIngredients(TagTypes.TAG_TYPE, ItemTag.of(polls));
        builder.addSlot(RecipeIngredientRole.INPUT, 57, 29)
                .addIngredients(TagTypes.TAG_TYPE, ItemTag.of(polls));
        builder.addSlot(RecipeIngredientRole.INPUT, 29, 57)
                .addIngredients(TagTypes.TAG_TYPE, ItemTag.of(polls));
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 29)
                .addIngredients(TagTypes.TAG_TYPE, ItemTag.of(polls));
    }
}
