package com.electroblob.wizardry.api.content.event;

import com.electroblob.wizardry.api.content.event.abstr.WizardryEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This applies to loaders with the Mixin
 * {@link com.electroblob.wizardry.core.mixin.MinecraftMixin#EBWIZARDRY$clientTick(CallbackInfo) MinecraftMixin#EBWIZARDRY$clientTick}
 * */
public final class EBClientTickEvent extends WizardryEvent {
    Minecraft minecraft;

    public EBClientTickEvent(Minecraft mc){
        this.minecraft = mc;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }
}
