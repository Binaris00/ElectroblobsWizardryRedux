package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.ConjureItemSpellsTestHandler;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class ConjureItemTest {
    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void spawnConjureItem(GameTestHelper helper) {
        ConjureItemSpellsTestHandler.spawnConjureItem(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void conjureItemDespawn(GameTestHelper helper) {
        ConjureItemSpellsTestHandler.conjureItemDespawn(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void balanceTable(GameTestHelper helper) {
        ConjureItemSpellsTestHandler.buildTable(helper);
    }
}
