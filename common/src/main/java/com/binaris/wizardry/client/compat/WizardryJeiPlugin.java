package com.binaris.wizardry.client.compat;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.client.compat.jei.ArcaneWorkbenchCategory;
import com.binaris.wizardry.client.compat.jei.ImbuementAltarCategory;
import com.binaris.wizardry.client.compat.jei.SpellSubtypeInterpreter;
import com.binaris.wizardry.content.recipe.ImbuementAltarRecipe;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.EBRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class WizardryJeiPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return WizardryMainMod.location("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration register) {
        IModPlugin.super.registerCategories(register);
        register.addRecipeCategories(new ImbuementAltarCategory(register.getJeiHelpers().getGuiHelper()));
        register.addRecipeCategories(new ArcaneWorkbenchCategory(register.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration register) {
        IModPlugin.super.registerRecipes(register);
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<ImbuementAltarRecipe> imbuementAltarRecipes = recipeManager.getAllRecipesFor(EBRecipeTypes.IMBUEMENT_ALTAR);

        register.addRecipes(ImbuementAltarCategory.IMBUE_TYPE, imbuementAltarRecipes);
        register.addRecipes(ArcaneWorkbenchCategory.ARCANE_WORKBENCH, ArcaneWorkbenchCategory.generateRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration register) {
        IModPlugin.super.registerRecipeCatalysts(register);
        register.addRecipeCatalyst(new ItemStack(EBBlocks.IMBUEMENT_ALTAR.get()), ImbuementAltarCategory.IMBUE_TYPE);
        register.addRecipeCatalyst(new ItemStack(EBBlocks.ARCANE_WORKBENCH.get()), ArcaneWorkbenchCategory.ARCANE_WORKBENCH);
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration register) {
        IModPlugin.super.registerItemSubtypes(register);
        register.registerSubtypeInterpreter(EBItems.SCROLL.get(), SpellSubtypeInterpreter.INSTANCE);
        register.registerSubtypeInterpreter(EBItems.SPELL_BOOK.get(), SpellSubtypeInterpreter.INSTANCE);
    }
}
