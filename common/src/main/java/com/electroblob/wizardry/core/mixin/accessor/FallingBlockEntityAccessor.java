package com.electroblob.wizardry.core.mixin.accessor;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FallingBlockEntity.class)
public interface FallingBlockEntityAccessor {
    @Invoker("<init>")
    static FallingBlockEntity createFallingBlockEntity(Level level, double x, double y, double z, BlockState state) {
        // This method body is ignored, the implementation is provided by Mixin
        throw new UnsupportedOperationException();
    }
}
