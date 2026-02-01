package com.binaris.wizardry.core.mixin;

import com.binaris.wizardry.api.content.data.ArcaneLockData;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {
    @Shadow
    @Final
    private Level level;

    @Shadow
    public abstract List<BlockPos> getToBlow();

    /**
     * Removes blocks with ArcaneLock from the explosion's target list.
     * This prevents explosions from destroying locked containers.
     */
    @Inject(method = "explode", at = @At("TAIL"))
    private void EBWIZARDRY$protectArcaneLockBlocks(CallbackInfo ci) {
        getToBlow().removeIf(pos -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BaseContainerBlockEntity containerBlock) {
                ArcaneLockData data = Services.OBJECT_DATA.getArcaneLockData(containerBlock);
                if (data == null) return false;
                return data.isArcaneLocked();
            }
            return false;
        });
    }
}
