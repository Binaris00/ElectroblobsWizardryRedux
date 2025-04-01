package com.electroblob.wizardry.client;

import com.electroblob.wizardry.registry.ParticleRegistryFabric;
import com.electroblob.wizardry.setup.registries.client.EBRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;

public final class WizardryFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EBRenderers.register();
        EBRenderers.getRenderers().forEach((entity, renderer) -> {
            EntityRendererRegistry.register(entity.get(), (EntityRendererProvider<Entity>) renderer);
        });
        ParticleRegistryFabric.register();
    }
}
