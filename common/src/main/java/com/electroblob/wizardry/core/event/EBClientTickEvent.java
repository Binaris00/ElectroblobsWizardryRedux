package com.electroblob.wizardry.core.event;

import net.minecraft.client.Minecraft;

public final class EBClientTickEvent {
    Minecraft minecraft;

    private EBClientTickEvent(Minecraft mc){
        this.minecraft = mc;
    }

    public static EBClientTickEvent create(Minecraft mc){
        return new EBClientTickEvent(mc);
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }
}
