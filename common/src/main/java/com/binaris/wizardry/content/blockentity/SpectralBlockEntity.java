package com.binaris.wizardry.content.blockentity;

import com.binaris.wizardry.setup.registries.EBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SpectralBlockEntity extends BlockEntityTimer {
    public SpectralBlockEntity(BlockPos pos, BlockState state) {
        super(EBBlockEntities.SPECTRAL_BLOCK.get(), pos, state, 1200);
    }
}
