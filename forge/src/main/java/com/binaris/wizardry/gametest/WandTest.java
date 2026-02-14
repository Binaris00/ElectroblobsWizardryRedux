package com.binaris.wizardry.gametest;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.gametest.WandTestHandler;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@SuppressWarnings("unused")
@PrefixGameTestTemplate(false)
@GameTestHolder(WizardryMainMod.MOD_ID)
public class WandTest {

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void wandBasicMovement(GameTestHelper helper) {
        WandTestHandler.wandBasicMovement(helper);
        helper.succeed();
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void wandPartiallyEmpty(GameTestHelper helper) {
        WandTestHandler.wandPartiallyEmpty(helper);
        helper.succeed();
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void wandCircularSelection(GameTestHelper helper) {
        WandTestHandler.wandCircularSelection(helper);
        helper.succeed();
    }


    @GameTest(template = "arcane_workbench_3x3x3")
    public static void wandLiteralIndex(GameTestHelper helper) {
        WandTestHandler.wandLiteralIndex(helper);
        helper.succeed();
    }


    @GameTest(template = "arcane_workbench_3x3x3")
    public static void wandLiteralIndexPartiallyEmpty(GameTestHelper helper) {
        WandTestHandler.wandLiteralIndexPartiallyEmpty(helper);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void siphonUpgradePlayerKillMob(GameTestHelper helper) {
        WandTestHandler.siphonUpgradePlayerKillMob(helper);
        helper.succeed();
    }
}
