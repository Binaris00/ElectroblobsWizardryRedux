package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.EBTestCentral;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class ImbuementEnchantsTest {

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public void simpleEnchantTest(GameTestHelper helper) {
        EBTestCentral.TempEnchants.simpleEnchantTest(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public void imbuementTickTest(GameTestHelper helper) {
        EBTestCentral.TempEnchants.imbuementTickTest(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public void simpleEnchantCompanyTest(GameTestHelper helper) {
        EBTestCentral.TempEnchants.simpleEnchantCompanyTest(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public void imbuementTickCompanyTest(GameTestHelper helper) {
        EBTestCentral.TempEnchants.imbuementTickCompanyTest(helper);
    }
}
