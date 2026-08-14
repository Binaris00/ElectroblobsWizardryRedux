package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.EBTestCentral;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class WandTest {
    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void wandBasicMovement(GameTestHelper helper) {
        EBTestCentral.Wand.wandBasicMovement(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void wandPartiallyEmpty(GameTestHelper helper) {
        EBTestCentral.Wand.wandPartiallyEmpty(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void wandCircularSelection(GameTestHelper helper) {
        EBTestCentral.Wand.wandCircularSelection(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void wandLiteralIndex(GameTestHelper helper) {
        EBTestCentral.Wand.wandLiteralIndex(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void wandLiteralIndexPartiallyEmpty(GameTestHelper helper) {
        EBTestCentral.Wand.wandLiteralIndexPartiallyEmpty(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:empty")
    public static void siphonUpgradePlayerKillMob(GameTestHelper helper) {
        EBTestCentral.Wand.siphonUpgradePlayerKillMob(helper);
        helper.succeed();
    }
}
