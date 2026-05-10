package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryEvent;
import net.minecraft.server.MinecraftServer;

public class EBServerLoad extends WizardryEvent {
    MinecraftServer server;

    public EBServerLoad(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }
}
