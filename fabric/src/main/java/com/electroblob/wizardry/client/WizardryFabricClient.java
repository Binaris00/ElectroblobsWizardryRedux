package com.electroblob.wizardry.client;

import com.electroblob.wizardry.setup.registries.client.EBClientRegister;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import com.electroblob.wizardry.setup.registries.client.EBRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;

public final class WizardryFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EBRenderers.register();
        EBRenderers.getRenderers().forEach((entity, renderer) -> {
            EntityRendererRegistry.register(entity.get(), (EntityRendererProvider<Entity>) renderer);
        });
        EBParticles.registerType(Registry::register);

        EBClientRegister.registerParticleProviders(collection ->
                collection.forEach((type, provider) -> {
                    var reg = ParticleFactoryRegistry.getInstance();
                    reg.register(type.get(), provider::apply);
                })
        );
    }
}
