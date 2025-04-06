package com.electroblob.wizardry.setup;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.setup.registries.client.EBRenderers;
import net.minecraft.network.chat.Component;

public final class ClientSetup {

    public static void init() {
        loadParticles();
        //EBRenderers.loadEntitiesRenderer();
    }

    public static void setup() {
        EBLogger.info(Component.translatable("logger.info.ebwizardry.client_started"));
        EBRenderers.register();
    }

    public static void loadParticles(){
        //EBParticles.load();
        //EBParticles.Register.load();
        //EBParticles.Register.loadFactories();
    }


    private ClientSetup() {}
}
