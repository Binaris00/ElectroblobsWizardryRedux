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
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

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

        // Item Info
        register.addIngredientInfo(EBItems.MAGIC_CRYSTAL.get(), Component.translatable("item.ebwizardry.magic_crystal.desc"));
        register.addIngredientInfo(EBItems.WIZARD_HANDBOOK.get(), Component.translatable("item.ebwizardry.wizard_handbook.desc"));
        register.addIngredientInfo(EBItems.MAGIC_CRYSTAL_SHARD.get(), Component.translatable("item.ebwizardry.magic_crystal_shard.desc"));
        register.addIngredientInfo(EBItems.MAGIC_CRYSTAL_GRAND.get(), Component.translatable("item.ebwizardry.magic_crystal_grand.desc"));
        register.addIngredientInfo(EBItems.IDENTIFICATION_SCROLL.get(), Component.translatable("item.ebwizardry.identification_scroll.desc_extended"));
        register.addIngredientInfo(EBItems.SPELL_BOOK.get(), Component.translatable("item.ebwizardry.spell_book.desc"));
        register.addIngredientInfo(EBItems.SCROLL.get(), Component.translatable("item.ebwizardry.scroll.desc"));
        register.addIngredientInfo(EBItems.RESPLENDENT_THREAD.get(), Component.translatable("item.ebwizardry.resplendent_thread.desc_extended"));
        register.addIngredientInfo(EBItems.CRYSTAL_SILVER_PLATING.get(), Component.translatable("item.ebwizardry.crystal_silver_plating.desc_extended"));
        register.addIngredientInfo(EBItems.ETHEREAL_CRYSTAL_WEAVE.get(), Component.translatable("item.ebwizardry.ethereal_crystal_weave.desc_extended"));
        register.addIngredientInfo(EBItems.PURIFYING_ELIXIR.get(), Component.translatable("item.ebwizardry.purifying_elixir.desc_extended"));
        register.addIngredientInfo(EBItems.ASTRAL_DIAMOND.get(), Component.translatable("item.ebwizardry.astral_diamond.desc"));
        register.addIngredientInfo(EBItems.RUINED_SPELL_BOOK.get(), Component.translatable("item.ebwizardry.ruined_spell_book.desc"));

        EBItems.getArtifacts().forEach((item, type) ->
                register.addIngredientInfo(item.get(), Component.translatable("item.ebwizardry." + type.toString().toLowerCase(Locale.ROOT) + ".generic.desc"))
        );

        addWandUpgradeInfo(register, EBItems.BLAST_UPGRADE.get());
        addWandUpgradeInfo(register, EBItems.ATTUNEMENT_UPGRADE.get());
        addWandUpgradeInfo(register, EBItems.BLAST_UPGRADE.get());
        addWandUpgradeInfo(register, EBItems.CONDENSER_UPGRADE.get());
        addWandUpgradeInfo(register, EBItems.COOLDOWN_UPGRADE.get());
        addWandUpgradeInfo(register, EBItems.DURATION_UPGRADE.get());
        addWandUpgradeInfo(register, EBItems.MELEE_UPGRADE.get());
        addWandUpgradeInfo(register, EBItems.RANGE_UPGRADE.get());
        addWandUpgradeInfo(register, EBItems.SIPHON_UPGRADE.get());
        addWandUpgradeInfo(register, EBItems.STORAGE_UPGRADE.get());
        addWandUpgradeInfo(register, EBItems.ARCANE_TOME.get());
        addWandUpgradeInfo(register, EBItems.APPRENTICE_ARCANE_TOME.get());
        addWandUpgradeInfo(register, EBItems.ADVANCED_ARCANE_TOME.get());
        addWandUpgradeInfo(register, EBItems.MASTER_ARCANE_TOME.get());
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

    private void addWandUpgradeInfo(IRecipeRegistration register, Item item) {
        register.addIngredientInfo(item, Component.translatable("item.ebwizardry.wand_upgrade.generic.desc"));
    }

    private void addEnchantmentInfo(IRecipeRegistration register, Enchantment enchantment, Component description) {
        register.addIngredientInfo(enchantment, () -> Enchantment.class, description);
    }
}
