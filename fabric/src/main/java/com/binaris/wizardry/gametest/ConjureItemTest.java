package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.EBTestCentral;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class ConjureItemTest {
    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void spawnConjureItem(GameTestHelper helper) {
        EBTestCentral.ConjureSpells.spawnConjureItem(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void conjureItemDespawn(GameTestHelper helper) {
        EBTestCentral.ConjureSpells.conjureItemDespawn(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void balanceTable(GameTestHelper helper) {
        EBTestCentral.ConjureSpells.buildTable(helper);
    }
}
