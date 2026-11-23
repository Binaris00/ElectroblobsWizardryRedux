package com.binaris.wizardry.gametest;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.gametest.EBTestCentral;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@SuppressWarnings("unused")
@PrefixGameTestTemplate(false)
@GameTestHolder(WizardryMainMod.MOD_ID)
public class BookshelfLecternTest {

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void bookshelfAcceptsOnlyBooks(GameTestHelper helper) {
        EBTestCentral.BookshelfLectern.bookshelfAcceptsOnlyBooks(helper);
    }

    @GameTest(template = "arcane_workbench_3x3x3")
    public static void lecternFindsNearbySpells(GameTestHelper helper) {
        EBTestCentral.BookshelfLectern.lecternFindsNearbySpells(helper);
    }
}
