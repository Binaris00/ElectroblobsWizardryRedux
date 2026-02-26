package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.ConjureItemTestHandler;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class ConjureItemTest {
    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void spawnConjureItem(GameTestHelper helper) {
        ConjureItemTestHandler.spawnConjureItem(helper);
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void conjureItemDespawn(GameTestHelper helper) {
        ConjureItemTestHandler.conjureItemDespawn(helper);
    }
}
