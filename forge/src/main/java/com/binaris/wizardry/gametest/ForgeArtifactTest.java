package com.binaris.wizardry.gametest;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.gametest.ArtifactTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@SuppressWarnings("unused")
@PrefixGameTestTemplate(false)
@GameTestHolder(WizardryMainMod.MOD_ID)
public class ForgeArtifactTest {

    @GameTest(template = "empty")
    public static void condensingRingRechargeMultipleItems(GameTestHelper helper) {
        ArtifactTest.condensingRingRecharge(helper);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void arcaneDefenseAmuletRechargesMultipleArmor(GameTestHelper helper) {
        ArtifactTest.arcaneDefenseAmuletRecharge(helper);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void ringCombustionExplosionOnFireDeath(GameTestHelper helper) {
        ArtifactTest.ringCombustion(helper);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void ringArcaneFrostSpawnsIceShardsOnFrostKill(GameTestHelper helper) {
        ArtifactTest.ringArcaneFrost(helper);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void ringEarthMeleeAppliesPoison(GameTestHelper helper) {
        ArtifactTest.ringEarthMeleeAppliesPoison(helper);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void ringFireMeleeSetsOnFire(GameTestHelper helper) {
        ArtifactTest.ringFireMeleeSetsOnFire(helper);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void ringIceMeleeAppliesFrost(GameTestHelper helper) {
        ArtifactTest.ringIceMeleeAppliesFrost(helper);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void ringLightningMeleeChainLightning(GameTestHelper helper) {
        ArtifactTest.ringLightningMelee(helper);
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void ringNecromancyMeleeAppliesWither(GameTestHelper helper) {
        ArtifactTest.ringNecromancyMelee(helper);
        helper.succeed();
    }
}
