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
}
