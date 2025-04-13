package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.event.EBPlayerJoinServerEvent;
import com.electroblob.wizardry.core.event.WizardryEventBus;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class WizardryFabricEvents {

    public static void onServer(){
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            WizardryEventBus.getInstance().fire(new EBPlayerJoinServerEvent(handler.getPlayer(), server));
        });
    }
}
