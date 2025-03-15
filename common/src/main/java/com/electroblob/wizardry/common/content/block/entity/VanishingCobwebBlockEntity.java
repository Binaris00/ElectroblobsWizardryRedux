package com.electroblob.wizardry.common.content.block.entity;

import com.electroblob.wizardry.setup.registries.EBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VanishingCobwebBlockEntity extends BlockEntityTimer {
	public VanishingCobwebBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
	}

//	public VanishingCobwebBlockEntity(BlockPos p_155229_, BlockState p_155230_, int maxTimer) {
//		super(EBBlockEntities.VANISHING_COBWEB_BLOCK_ENTITY.get(), p_155229_, p_155230_);
//	}
//
//	public VanishingCobwebBlockEntity(BlockPos pos, BlockState state) {
//		super(EBBlockEntities.ARCANE_WORKBENCH_BE.get(), pos, state);
//	}
}
