package com.binaris.wizardry.core.gametest;

import com.binaris.wizardry.api.content.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@SuppressWarnings("unused")
public class EBUtilTest {
    private static final Vec3 BLOCK_POS = new Vec3(1, 2, 1);
    private static final Vec3 ENTITY_POS = new Vec3(2, 2, 2);

    public static final List<Block> POWERFUL_BLOCKS = List.of(
            Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN,
            Blocks.NETHERITE_BLOCK, Blocks.ANCIENT_DEBRIS
    );

    public static final List<Block> NON_POWERFUL_BLOCKS = List.of(
            Blocks.ACACIA_WOOD, Blocks.BIRCH_WOOD, Blocks.DARK_OAK_WOOD,
            Blocks.JUNGLE_WOOD, Blocks.END_STONE,
            Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD,
            Blocks.MANGROVE_WOOD, Blocks.DIRT,
            Blocks.COBBLESTONE, Blocks.CALCITE
    );

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void playerBreakBlockNoPowerful(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, ENTITY_POS);
        for (Block block : NON_POWERFUL_BLOCKS) {
            GST.placeBlock(helper, BLOCK_POS, block);
            boolean canBreak = BlockUtil.canBreak(player, player.level(), helper.absolutePos(BlockPos.containing(BLOCK_POS)), false);
            GST.assertTrue(helper, "Player should be able to break non-powerful block: " + block, canBreak);
            player.level().destroyBlock(helper.absolutePos(BlockPos.containing(BLOCK_POS)), false);
        }

        helper.succeed();
    }

    @GameTest(template = "ebwizardry:empty_3x3x3")
    public static void playerBreakBlockWithPowerful(GameTestHelper helper) {
        Player player = GST.mockServerPlayer(helper, ENTITY_POS);
        for (Block block : POWERFUL_BLOCKS) {
            GST.placeBlock(helper, BLOCK_POS, block);
            boolean canBreak = BlockUtil.canBreak(player, player.level(), helper.absolutePos(BlockPos.containing(BLOCK_POS)), true);
            GST.assertTrue(helper, "Player should be able to break powerful block with power: " + block, canBreak);
            player.level().destroyBlock(helper.absolutePos(BlockPos.containing(BLOCK_POS)), false);
        }

        helper.succeed();
    }

    // For some reason mob block breaking tests aren't working in GameTest framework, the blocks just never place and it
    // always finds air blocks.

//    @GameTest(template = "ebwizardry:empty_3x3x3")
//    public static void mobBreakBlockNoPowerful(GameTestHelper helper) {
//        Wizard wizard = (Wizard) GST.mockEntity(helper, ENTITY_POS, EBEntities.WIZARD.get());
//        for (Block block : NON_POWERFUL_BLOCKS) {
//            GST.placeBlock(helper, BLOCK_POS, block);
//            boolean canBreak = BlockUtil.canBreak(wizard, helper.getLevel(), helper.absolutePos(BlockPos.containing(BLOCK_POS)));
//            GST.assertTrue(helper, "mob should be able to break non-powerful block: " + block, canBreak);
//            helper.getLevel().destroyBlock(helper.absolutePos(BlockPos.containing(BLOCK_POS)), false);
//        }
//
//        helper.succeed();
//    }
//
//    @GameTest(template = "ebwizardry:empty_3x3x3")
//    public static void mobBreakBlockWithPowerful(GameTestHelper helper) {
//        Wizard wizard = (Wizard) GST.mockEntity(helper, ENTITY_POS, EBEntities.WIZARD.get());
//        for (Block block : POWERFUL_BLOCKS) {
//            GST.placeBlock(helper, BLOCK_POS, block);
//            boolean canBreak = BlockUtil.canBreak(wizard, helper.getLevel(), helper.absolutePos(BlockPos.containing(BLOCK_POS)));
//            GST.assertFalse(helper, "mob should NOT be able to break powerful block with power: " + block, canBreak);
//            helper.getLevel().destroyBlock(helper.absolutePos(BlockPos.containing(BLOCK_POS)), false);
//        }
//
//        helper.succeed();
//    }
}
