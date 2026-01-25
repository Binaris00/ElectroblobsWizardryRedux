package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.ArtifactTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class FabricArtifactTest {

    @GameTest(template = "ebwizardry:empty")
    public static void condensingRingRechargesManaAt150(GameTestHelper helper) {
        ArtifactTest.condensingRingRecharge(helper); // success on helper
    }

    @GameTest(template = "ebwizardry:empty")
    public static void condensingRingRechargeMultipleItems(GameTestHelper helper) {
        ArtifactTest.condensingRingRechargeMultipleItems(helper); // success on helper
    }

    @GameTest(template = "ebwizardry:empty")
    public static void amuletArcaneDefenseRecharge(GameTestHelper helper) {
        ArtifactTest.arcaneDefenseAmuletRecharge(helper); // success on helper
    }

    @GameTest(template = "ebwizardry:empty")
    public static void arcaneDefenseAmuletRechargesMultipleArmor(GameTestHelper helper) {
        ArtifactTest.arcaneDefenseAmuletRechargesMultipleArmor(helper); // success on helper
    }
}
