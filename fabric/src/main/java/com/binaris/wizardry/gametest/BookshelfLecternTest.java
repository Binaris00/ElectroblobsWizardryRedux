package com.binaris.wizardry.gametest;

import com.binaris.wizardry.core.gametest.EBTestCentral;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("unused")
public class BookshelfLecternTest {

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void bookshelfAcceptsOnlyBooks(GameTestHelper helper) {
        EBTestCentral.BookshelfLectern.bookshelfAcceptsOnlyBooks(helper);
    }

    @GameTest(template = "ebwizardry:arcane_workbench_3x3x3")
    public static void lecternFindsNearbySpells(GameTestHelper helper) {
        EBTestCentral.BookshelfLectern.lecternFindsNearbySpells(helper);
    }
}
