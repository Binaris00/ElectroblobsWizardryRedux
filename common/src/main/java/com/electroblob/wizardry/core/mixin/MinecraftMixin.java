package com.electroblob.wizardry.core.mixin;

import com.electroblob.wizardry.api.content.event.EBClientTickEvent;
import com.electroblob.wizardry.client.SpellGUIDisplay;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    public void EBWIZARDRY$clientInit(CallbackInfo ci) {
        SpellGUIDisplay.init();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void EBWIZARDRY$clientTick(CallbackInfo ci) {
        Minecraft minecraft = ((Minecraft) (Object) this);
        WizardryEventBus.getInstance().fire(new EBClientTickEvent(minecraft));
    }
}
