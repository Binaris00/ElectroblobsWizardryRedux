package com.electroblob.wizardry.core.gametest;

import com.electroblob.wizardry.content.blockentity.ArcaneWorkbenchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class EBGameTest {

    @GameTest(template = "add_spells_to_wand")
    public static void applySpellsToWand(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 2, 1);
        var level = helper.getLevel();

        ArcaneWorkbenchBlockEntity workbench = (ArcaneWorkbenchBlockEntity) helper.getBlockEntity(pos);
        if (workbench == null) {
            helper.fail("Arcane Workbench BlockEntity is null");
            return;
        }

        helper.succeed();
    }

    public EBGameTest() {
    }
}