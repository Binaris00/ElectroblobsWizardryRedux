package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.MinionTestHandler;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class MinionTest {

    @GameTest(template = "ebwizardry:empty_3x3x3", timeoutTicks = 500)
    public static void minionCopyMobOwnerTarget(GameTestHelper helper) {
        MinionTestHandler.minionCopyMobOwnerTarget(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3", timeoutTicks = 500)
    public static void minionAttackMobOwnerLastDamagedEntity(GameTestHelper helper) {
        MinionTestHandler.minionAttackMobOwnerLastDamagedEntity(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3", timeoutTicks = 500)
    public static void minionAttackMobOwnerDamagedByEntity(GameTestHelper helper) {
        MinionTestHandler.minionAttackMobOwnerDamagedByEntity(helper);
    }
}
