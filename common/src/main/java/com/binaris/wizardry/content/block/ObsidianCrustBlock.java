package com.binaris.wizardry.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

public class ObsidianCrustBlock extends Block {
    public static final IntegerProperty CRUSH_LEVEL = IntegerProperty.create("crush_level", 0, 3);

    public ObsidianCrustBlock() {
        super(Block.Properties.copy(Blocks.OBSIDIAN).strength(50.0F, 1200.0F).randomTicks());
        this.registerDefaultState(this.stateDefinition.any().setValue(CRUSH_LEVEL, 0));
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        this.tick(state, level, blockPos, random);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (this.countNeighbors(level, pos) < 4) {
            this.slightlyMelt(level, pos, state, random, true);
        } else {
            level.scheduleTick(pos, this, Mth.nextInt(random, 20, 40));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean p_60514_) {
        if (neighborBlock == this) {
            int i = this.countNeighbors(level, blockPos);

            if (i < 2) {
                this.melt(level, blockPos);
            }
        }
        super.neighborChanged(state, level, blockPos, neighborBlock, neighborPos, p_60514_);
    }

    private int countNeighbors(Level world, BlockPos pos) {
        int i = 0;

        for (Direction direction : Direction.values()) {
            if (world.getBlockState(pos.relative(direction)).getBlock() == this) {
                ++i;

                if (i >= 4) {
                    return i;
                }
            }
        }

        return i;
    }

    protected void slightlyMelt(Level world, BlockPos pos, BlockState state, RandomSource random, boolean meltNeighbours) {
        int i = state.getValue(CRUSH_LEVEL);

        if (i < 3) {
            world.setBlock(pos, state.setValue(CRUSH_LEVEL, i + 1), 2);
            world.scheduleTick(pos, this, Mth.nextInt(random, 20, 40));

        } else {
            this.melt(world, pos);

            if (meltNeighbours) {
                for (Direction direction : Direction.values()) {
                    BlockPos blockpos = pos.relative(direction);
                    BlockState iblockstate = world.getBlockState(blockpos);
                    if (iblockstate.getBlock() == this) {
                        this.slightlyMelt(world, blockpos, iblockstate, random, false);
                    }
                }
            }
        }
    }

    protected void melt(Level world, BlockPos pos) {
        world.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
        world.neighborChanged(pos, Blocks.LAVA, pos);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CRUSH_LEVEL);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return ItemStack.EMPTY;
    }
}
