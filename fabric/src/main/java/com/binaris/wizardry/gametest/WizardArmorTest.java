package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.EBTestCentral;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class WizardArmorTest {
    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void armorNeverBreaks(GameTestHelper helper) {
        EBTestCentral.WizardArmor.armorNeverBreaks(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void armorAttributesWithMana(GameTestHelper helper) {
        EBTestCentral.WizardArmor.armorAttributesWithMana(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void armorNoAttributesWithoutMana(GameTestHelper helper) {
        EBTestCentral.WizardArmor.armorNoAttributesWithoutMana(helper);
    }
}
