package com.binaris.wizardry.gametest;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.gametest.ConjureItemTestHandler;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@SuppressWarnings("unused")
@PrefixGameTestTemplate(false)
@GameTestHolder(WizardryMainMod.MOD_ID)
public class ConjureItemTest {

    @GameTest(template = "empty_3x3x3")
    public static void spawnConjureItem(GameTestHelper helper) {
        ConjureItemTestHandler.spawnConjureItem(helper);
    }

    @GameTest(template = "empty_3x3x3")
    public static void conjureItemDespawn(GameTestHelper helper) {
        ConjureItemTestHandler.conjureItemDespawn(helper);
    }
}
