package com.binaris.wizardry.client.compat.jei;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.content.recipe.ArcaneWorkbenchRecipe;
import com.binaris.wizardry.setup.registries.EBMenus;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/// JEI recipe transfer handler for the arcane workbench. This differs from a standard recipe handler in that it returns
/// the active bookshelf (virtual) slots from the workbench as part of the inventory slots, and does not require complete
/// sets of ingredients.
public class ArcaneWorkbenchTransferHandler implements IRecipeTransferInfo<ArcaneWorkbenchMenu, ArcaneWorkbenchRecipe> {
    @Override
    public @NotNull Class<? extends ArcaneWorkbenchMenu> getContainerClass() {
        return ArcaneWorkbenchMenu.class;
    }

    @Override
    public @NotNull Optional<MenuType<ArcaneWorkbenchMenu>> getMenuType() {
        return Optional.of(EBMenus.ARCANE_WORKBENCH_MENU.get());
    }

    @Override
    public @NotNull RecipeType<ArcaneWorkbenchRecipe> getRecipeType() {
        return new RecipeType<>(WizardryMainMod.location("arcane_workbench"), ArcaneWorkbenchRecipe.class);
    }

    @Override
    public boolean canHandle(@NotNull ArcaneWorkbenchMenu menu, @NotNull ArcaneWorkbenchRecipe recipe) {
        return true;
    }

    @Override
    public boolean requireCompleteSets(@NotNull ArcaneWorkbenchMenu container, @NotNull ArcaneWorkbenchRecipe recipe) {
        return IRecipeTransferInfo.super.requireCompleteSets(container, recipe);
    }

    @Override
    public @NotNull List<Slot> getRecipeSlots(ArcaneWorkbenchMenu menu, ArcaneWorkbenchRecipe recipe) {
        return menu.slots.subList(0, ArcaneWorkbenchMenu.UPGRADE_SLOT + 1);
    }

    @Override
    public @NotNull List<Slot> getInventorySlots(ArcaneWorkbenchMenu menu, @NotNull ArcaneWorkbenchRecipe recipe) {
        List<Slot> slots = new ArrayList<>(menu.slots.subList(ArcaneWorkbenchMenu.UPGRADE_SLOT + 1, ArcaneWorkbenchMenu.UPGRADE_SLOT + 37));
        // slots.add(menu.getSlot(0));
        return slots;
    }
}