package com.binaris.wizardry.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DryFrostedIceBlock extends FrostedIceBlock {
    public DryFrostedIceBlock() {
        super(Properties.copy(Blocks.ICE).noOcclusion().randomTicks().friction(0.98f).sound(SoundType.GLASS));
    }

    @Override
    protected void melt(@NotNull BlockState state, Level level, @NotNull BlockPos pos) {
        level.destroyBlock(pos, false);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel worldIn, @NotNull BlockPos pos, RandomSource rand) {
        if (rand.nextInt(3) == 0) {
            this.slightlyMelt(state, worldIn, pos);
        } else {
            worldIn.scheduleTick(pos, this, Mth.nextInt(rand, 20, 40));
        }
    }

    private void slightlyMelt(BlockState pState, Level pLevel, BlockPos pPos) {
        int i = pState.getValue(AGE);
        if (i < 3) {
            pLevel.setBlock(pPos, pState.setValue(AGE, i + 1), 2);
        } else {
            this.melt(pState, pLevel, pPos);
        }
    }
}
