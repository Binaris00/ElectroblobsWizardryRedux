package com.binaris.wizardry.content.blockentity;

import com.binaris.wizardry.setup.registries.EBBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEntityTimer extends BlockEntity {
    public int timer = 0;
    public int maxTimer;

    public BlockEntityTimer(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public BlockEntityTimer(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_, int maxTimer) {
        this(p_155228_, p_155229_, p_155230_);
        this.maxTimer = maxTimer;
    }

    public static void update(Level p_155014_, BlockPos p_155015_, BlockState p_155016_, BlockEntityTimer p_155017_) {
        p_155017_.timer++;

        if (p_155017_.maxTimer > 0 && p_155017_.timer > p_155017_.maxTimer && !p_155014_.isClientSide) {
            if (p_155017_.getBlockState() == EBBlocks.VANISHING_COBWEB.get().defaultBlockState()) {
                p_155014_.destroyBlock(p_155015_, false);
            } else {
                p_155014_.setBlockAndUpdate(p_155015_, Blocks.AIR.defaultBlockState());
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
    public void load(CompoundTag tagCompound) {
        super.load(tagCompound);
        timer = tagCompound.getInt("timer");
        maxTimer = tagCompound.getInt("maxTimer");
    }

    @Override
    public void saveAdditional(CompoundTag tagCompound) {
        super.saveAdditional(tagCompound);
        tagCompound.putInt("timer", timer);
        tagCompound.putInt("maxTimer", maxTimer);
    }
}
