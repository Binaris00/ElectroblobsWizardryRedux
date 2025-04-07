package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.EBEventHelper;
import com.electroblob.wizardry.core.event.EBClientTickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    public void EBWIZARDRY$clientTick(CallbackInfo ci){
        Minecraft minecraft = ((Minecraft) (Object) this);
        EBEventHelper.onClientTick(EBClientTickEvent.create(minecraft));
    }
}
