package com.electroblob.wizardry.content.blockentity;

import com.electroblob.wizardry.setup.registries.EBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VanishingCobwebBlockEntity extends BlockEntityTimer {
	public VanishingCobwebBlockEntity(BlockPos pos, BlockState state) {
		super(EBBlockEntities.VANISHING_COBWEB.get(), pos, state);
	}

//	public VanishingCobwebBlockEntity(BlockPos p_155229_, BlockState p_155230_, int maxTimer) {
//		super(EBBlockEntities.VANISHING_COBWEB_BLOCK_ENTITY.get(), p_155229_, p_155230_);
//	}
//
//	public VanishingCobwebBlockEntity(BlockPos pos, BlockState state) {
//		super(EBBlockEntities.ARCANE_WORKBENCH_BE.get(), pos, state);
//	}
}
