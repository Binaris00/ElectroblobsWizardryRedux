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

    @GameTest(template = "ebwizardry:empty")
    public static void ringCombustionExplosionOnFireDeath(GameTestHelper helper) {
        ArtifactTest.ringCombustion(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:empty")
    public static void ringArcaneFrostSpawnsIceShardsOnFrostKill(GameTestHelper helper) {
        ArtifactTest.ringArcaneFrost(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:empty")
    public static void ringEarthMeleeAppliesPoison(GameTestHelper helper) {
        ArtifactTest.ringEarthMeleeAppliesPoison(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:empty")
    public static void ringFireMeleeSetsOnFire(GameTestHelper helper) {
        ArtifactTest.ringFireMeleeSetsOnFire(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:empty")
    public static void ringIceMeleeAppliesFrost(GameTestHelper helper) {
        ArtifactTest.ringIceMeleeAppliesFrost(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:empty")
    public static void ringLightningMeleeChainLightning(GameTestHelper helper) {
        ArtifactTest.ringLightningMelee(helper);
        helper.succeed();
    }

    @GameTest(template = "ebwizardry:empty")
    public static void ringNecromancyMeleeAppliesWither(GameTestHelper helper) {
        ArtifactTest.ringNecromancyMelee(helper);
        helper.succeed();
    }
}
