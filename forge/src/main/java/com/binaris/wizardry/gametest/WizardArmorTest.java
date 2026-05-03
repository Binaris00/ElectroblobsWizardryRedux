package com.binaris.wizardry.gametest;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.gametest.WizardArmorTestHandler;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@SuppressWarnings("unused")
@PrefixGameTestTemplate(false)
@GameTestHolder(WizardryMainMod.MOD_ID)
public class WizardArmorTest {

    @GameTest(template = "empty_3x3x3")
    public static void armorNeverBreaks(GameTestHelper helper) {
        WizardArmorTestHandler.armorNeverBreaks(helper);
    }

    @GameTest(template = "empty_3x3x3")
    public static void armorAttributesWithMana(GameTestHelper helper) {
        WizardArmorTestHandler.armorAttributesWithMana(helper);
    }

    @GameTest(template = "empty_3x3x3")
    public static void armorNoAttributesWithoutMana(GameTestHelper helper) {
        WizardArmorTestHandler.armorNoAttributesWithoutMana(helper);
    }
}
