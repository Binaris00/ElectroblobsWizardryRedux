package com.electroblob.wizardry.core.mixin.client;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"))
    public void EBWIZARDRY$onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        // TODO :P IM LAZY AAAA (Mouse handler for spells on client)
    }
}
