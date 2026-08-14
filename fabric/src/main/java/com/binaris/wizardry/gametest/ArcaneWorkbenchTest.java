package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.EBTestCentral;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class ArcaneWorkbenchTest {

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void applySpellsToWand(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.applySpellsToWand(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void canUpgradeToNextTier(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.canUpgradeToNextTier(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void putSpellOnBlankScroll(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.putSpellOnBlankScroll(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void putSpellOnScroll(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.putSpellOnScrollFilled(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void upgradeWizardArmor(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.upgradeNormalArmor(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void cannotUpgradeMaxedArmor(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.cannotUpgradeMaxedArmor(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void repairWand(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.repairWand(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void cannotExceedBlankScrollLimit(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.cannotExceedBlankScrollLimit(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void cannotExceedSpellBookLimit(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.cannotExceedSpellBookLimit(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void cannotExceedUpgradeLimit(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.cannotExceedUpgradeLimit(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void upgradeWandNextTierNBT(GameTestHelper helper) {
        EBTestCentral.ArcaneWorkbench.upgradeWandNextTierNBT(helper);
    }
}