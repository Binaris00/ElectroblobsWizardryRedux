package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.ArtifactTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class FabricArtifactTest {

    @GameTest(template = "ebwizardry:empty")
    public static void condensingRingRecharge(GameTestHelper helper) {
        ArtifactTest.condensingRingRecharge(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:empty")
    public static void arcaneDefenseAmuletRecharge(GameTestHelper helper) {
        ArtifactTest.arcaneDefenseAmuletRecharge(helper);
        helper.succeed();
    }
}
