package com.binaris.wizardry.gametest;

import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.content.item.WizardArmorItem;
import com.binaris.wizardry.content.item.WizardArmorType;
import com.binaris.wizardry.core.gametest.AWTestHandler;
import com.binaris.wizardry.setup.datagen.EBDataGenProcessor;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;

import java.util.List;

@SuppressWarnings("unused")
public class ArcaneWorkbenchTest {

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void applySpellsToWand(GameTestHelper helper) {
        EBDataGenProcessor.wandItems().values().forEach(wand ->
                AWTestHandler.applySpellsToWand(helper, wand.get(), Spells.COBWEBS, Spells.FIREBALL));
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void canUpgradeToNextTier(GameTestHelper helper) {
        EBDataGenProcessor.wandItems().values().forEach(wand ->
                AWTestHandler.canUpgradeToNextTier(helper, wand.get()));
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void putSpellOnBlankScroll(GameTestHelper helper) {
        AWTestHandler.putSpellOnBlankScroll(helper, Spells.ARCANE_LOCK);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void putSpellOnScroll(GameTestHelper helper) {
        AWTestHandler.putSpellOnScrollFilled(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void upgradeWizardArmor(GameTestHelper helper) {
        List<Item> upgrades = List.of(
                EBItems.CRYSTAL_SILVER_PLATING.get(),
                EBItems.ETHEREAL_CRYSTAL_WEAVE.get(),
                EBItems.RESPLENDENT_THREAD.get()
        );

        EBItems.getArmors().stream()
                .map(DeferredObject::get)
                .filter(item -> ((WizardArmorItem) item).getWizardArmorType() == WizardArmorType.WIZARD)
                .forEach(armor -> upgrades.forEach(upgrade ->
                        AWTestHandler.upgradeNormalArmor(helper, armor, upgrade)));
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void cannotUpgradeMaxedArmor(GameTestHelper helper) {
        List<Item> upgrades = List.of(
                EBItems.CRYSTAL_SILVER_PLATING.get(),
                EBItems.ETHEREAL_CRYSTAL_WEAVE.get(),
                EBItems.RESPLENDENT_THREAD.get()
        );

        EBItems.getArmors().stream()
                .map(DeferredObject::get)
                .filter(item -> ((WizardArmorItem) item).getWizardArmorType() != WizardArmorType.WIZARD)
                .forEach(armor -> upgrades.forEach(upgrade ->
                        AWTestHandler.cannotUpgradeMaxedArmor(helper, armor, upgrade)));
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void repairWand(GameTestHelper helper) {
        EBDataGenProcessor.wandItems().values().forEach(wand ->
                AWTestHandler.repairWand(helper, wand.get(), EBItems.MAGIC_CRYSTAL.get()));
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void cannotExceedBlankScrollLimit(GameTestHelper helper) {
        AWTestHandler.cannotExceedBlankScrollLimit(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void cannotExceedSpellBookLimit(GameTestHelper helper) {
        AWTestHandler.cannotExceedSpellBookLimit(helper, Spells.FIREBALL);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void cannotExceedUpgradeLimit(GameTestHelper helper) {
        List<Item> upgrades = List.of(
                EBItems.ARCANE_TOME.get(),
                EBItems.APPRENTICE_ARCANE_TOME.get(),
                EBItems.ADVANCED_ARCANE_TOME.get(),
                EBItems.MASTER_ARCANE_TOME.get(),
                EBItems.CRYSTAL_SILVER_PLATING.get(),
                EBItems.ETHEREAL_CRYSTAL_WEAVE.get(),
                EBItems.RESPLENDENT_THREAD.get()
        );

        upgrades.forEach(upgrade ->
                AWTestHandler.cannotExceedUpgradeLimit(helper, upgrade));
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void upgradeWandNextTierNBT(GameTestHelper helper) {
        EBDataGenProcessor.wandItems().values().forEach(wand -> AWTestHandler.upgradeWandNextTierNBT(helper, wand.get()));
        helper.succeed();
    }
}