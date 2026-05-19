package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.ImbuementEnchantsTestHandler;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class ImbuementEnchantsTest {

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public void simpleEnchantTest(GameTestHelper helper) {
        ImbuementEnchantsTestHandler.simpleEnchantTest(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public void imbuementTickTest(GameTestHelper helper) {
        ImbuementEnchantsTestHandler.imbuementTickTest(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public void simpleEnchantCompanyTest(GameTestHelper helper) {
        ImbuementEnchantsTestHandler.simpleEnchantCompanyTest(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public void imbuementTickCompanyTest(GameTestHelper helper) {
        ImbuementEnchantsTestHandler.imbuementTickCompanyTest(helper);
    }
}
