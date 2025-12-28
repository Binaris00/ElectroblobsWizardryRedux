package com.electroblob.wizardry.content.blockentity;

import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBBlockEntities;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class VanishingCobwebBlockEntity extends BlockEntityTimer {
    public VanishingCobwebBlockEntity(BlockPos pos, BlockState state) {
        super(EBBlockEntities.VANISHING_COBWEB.get(), pos, state, Spells.COBWEBS.property(DefaultProperties.DURATION));
    }
}
