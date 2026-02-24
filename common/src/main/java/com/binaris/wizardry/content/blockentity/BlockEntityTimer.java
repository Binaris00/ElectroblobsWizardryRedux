package com.binaris.wizardry.content.blockentity;

import com.binaris.wizardry.setup.registries.EBBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockEntityTimer extends BlockEntity {
    public int timer = 0;
    public int maxTimer;

    public BlockEntityTimer(BlockEntityType<?> type, BlockPos blockPos, BlockState state) {
        super(type, blockPos, state);
    }

    public BlockEntityTimer(BlockEntityType<?> type, BlockPos blockPos, BlockState state, int maxTimer) {
        this(type, blockPos, state);
        this.maxTimer = maxTimer;
    }

    public static void update(Level level, BlockPos blockPos, BlockState state, BlockEntityTimer entityTimer) {
        entityTimer.timer++;

        if (entityTimer.maxTimer > 0 && entityTimer.timer > entityTimer.maxTimer && !level.isClientSide) {
            if (entityTimer.getBlockState() == EBBlocks.VANISHING_COBWEB.get().defaultBlockState()) {
                level.destroyBlock(blockPos, false);
            } else {
                level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
            }
        }
    }

    public int getLifetime() {
        return maxTimer;
    }

    public void setLifetime(int lifetime) {
        this.maxTimer = lifetime;
    }

    @Override
    public void load(@NotNull CompoundTag tagCompound) {
        super.load(tagCompound);
        timer = tagCompound.getInt("timer");
        maxTimer = tagCompound.getInt("maxTimer");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putInt("timer", timer);
        tagCompound.putInt("maxTimer", maxTimer);
    }
}
