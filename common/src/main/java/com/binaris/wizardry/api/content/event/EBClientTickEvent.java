package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryEvent;
import com.binaris.wizardry.core.mixin.MinecraftMixin;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// This applies to loaders with the Mixin
/// [`MinecraftMixin#EBWIZARDRY$clientTick`][MinecraftMixin#EBWIZARDRY$clientTick(CallbackInfo)]
public final class EBClientTickEvent extends WizardryEvent {
    Minecraft minecraft;

    public EBClientTickEvent(Minecraft mc) {
        this.minecraft = mc;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }
}
