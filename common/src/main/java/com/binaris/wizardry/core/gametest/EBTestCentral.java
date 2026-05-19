package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.spell.Spell;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;

public final class EBTestCentral {

    public static class ArcaneWorkbench {

        public static void applySpellsToWand(GameTestHelper helper, Item wand, Spell... spells) {
            AWTestHandler.applySpellsToWand(helper, wand, spells);
        }
    }

    private EBTestCentral() {
    }
}
