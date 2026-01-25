package com.binaris.wizardry.gametest;

import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.content.item.WizardArmorItem;
import com.binaris.wizardry.content.item.WizardArmorType;
import com.binaris.wizardry.core.gametest.ArcaneWorkbenchTest;
import com.binaris.wizardry.setup.datagen.EBDataGenProcessor;
import com.binaris.wizardry.setup.registries.EBItems;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;

import java.util.List;

@SuppressWarnings("unused")
public class FabricArcaneWorkbenchTest {

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void applySpellsToWand(GameTestHelper helper) {
        EBDataGenProcessor.wandItems().values().forEach(wand ->
                ArcaneWorkbenchTest.applySpellsToWand(helper, wand.get(), Spells.COBWEBS, Spells.FIREBALL));
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void canUpgradeToNextTier(GameTestHelper helper) {
        EBDataGenProcessor.wandItems().values().forEach(wand ->
                ArcaneWorkbenchTest.canUpgradeToNextTier(helper, wand.get()));
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void putSpellOnBlankScroll(GameTestHelper helper) {
        ArcaneWorkbenchTest.putSpellOnBlankScroll(helper, Spells.ARCANE_LOCK);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void putSpellOnScroll(GameTestHelper helper) {
        ArcaneWorkbenchTest.putSpellOnScrollFilled(helper);
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
                        ArcaneWorkbenchTest.upgradeNormalArmor(helper, armor, upgrade)));
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
                        ArcaneWorkbenchTest.cannotUpgradeMaxedArmor(helper, armor, upgrade)));
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void repairWand(GameTestHelper helper) {
        EBDataGenProcessor.wandItems().values().forEach(wand ->
                ArcaneWorkbenchTest.repairWand(helper, wand.get(), EBItems.MAGIC_CRYSTAL.get()));
        helper.succeed();
    }
}