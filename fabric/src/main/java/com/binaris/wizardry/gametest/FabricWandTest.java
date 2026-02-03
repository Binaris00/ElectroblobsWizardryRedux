package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.WandTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class FabricWandTest {
    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void wandBasicMovement(GameTestHelper helper) {
        WandTest.wandBasicMovement(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void wandPartiallyEmpty(GameTestHelper helper) {
        WandTest.wandPartiallyEmpty(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void wandCircularSelection(GameTestHelper helper) {
        WandTest.wandCircularSelection(helper);
        helper.succeed();
    }
}
