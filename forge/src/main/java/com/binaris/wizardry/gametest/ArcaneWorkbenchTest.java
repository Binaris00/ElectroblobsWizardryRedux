package com.binaris.wizardry.gametest;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.core.gametest.EBTestCentral;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@SuppressWarnings("unused")
@PrefixGameTestTemplate(false)
@GameTestHolder(WizardryMainMod.MOD_ID)
public class ArcaneWorkbenchTest {

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void applySpellsToWand(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.applySpellsToWand(helper);
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void canUpgradeToNextTier(GameTestHelper helper) {
        EBLogger.warn("canUpgradeToNextTier not implemented on Forge due to ServerPlayer cast limitations.");
        helper.succeed();
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void putSpellOnBlankScroll(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.putSpellOnBlankScroll(helper);
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void putSpellOnScroll(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.putSpellOnScrollFilled(helper);
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void upgradeWizardArmor(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.upgradeNormalArmor(helper);
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void cannotUpgradeMaxedArmor(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.cannotUpgradeMaxedArmor(helper);
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void repairWand(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.repairWand(helper);
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void cannotExceedBlankScrollLimit(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.cannotExceedBlankScrollLimit(helper);
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void cannotExceedSpellBookLimit(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.cannotExceedSpellBookLimit(helper);
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void cannotExceedUpgradeLimit(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.cannotExceedUpgradeLimit(helper);
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void upgradeWandNextTierNBT(GameTestHelper helper) {
        EBLogger.warn("upgradeWandNextTierNBT not implemented on Forge due to ServerPlayer cast limitations.");
        //        EBDataGenProcessor.wandItems().values().forEach(wand -> ArcaneWorkbenchTest.upgradeWandNextTierNBT(helper, wand.get()));
        helper.succeed();
    }
}