package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryEvent;
import com.binaris.wizardry.core.mixin.MinecraftMixin;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This applies to loaders with the Mixin
 * {@link MinecraftMixin#EBWIZARDRY$clientTick(CallbackInfo) MinecraftMixin#EBWIZARDRY$clientTick}
 *
 */
public final class EBClientTickEvent extends WizardryEvent {
    Minecraft minecraft;

    public EBClientTickEvent(Minecraft mc) {
        this.minecraft = mc;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }
}
