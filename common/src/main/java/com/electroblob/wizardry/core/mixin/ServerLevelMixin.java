package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.core.SpellEngine;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    // After entities and block entities tick
    @Inject(
            method = "Lnet/minecraft/server/level/ServerLevel;tick(Ljava/util/function/BooleanSupplier;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;tickBlockEntities()V"
            )
    )
    public void tick(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        SpellEngine.run((ServerLevel)((Object)this));
    }


}
