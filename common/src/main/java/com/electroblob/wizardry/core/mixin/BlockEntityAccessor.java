package com.electroblob.wizardry.core.mixin;

import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.gen.Invoker;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.block.entity.BlockEntity.class)
public interface BlockEntityAccessor {
    @Invoker
    void callSaveAdditional(CompoundTag tag);
}
