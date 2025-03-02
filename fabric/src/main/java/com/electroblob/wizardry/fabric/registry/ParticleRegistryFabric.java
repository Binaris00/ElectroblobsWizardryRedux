package com.electroblob.wizardry.fabric.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.client.EBClientRegister;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ParticleRegistryFabric {

    public static void register() {
        EBClientRegister.registerParticleTypes(collection ->
                collection.forEach((name, type) ->
                        Registry.register(BuiltInRegistries.PARTICLE_TYPE, WizardryMainMod.location(name), type.get())
                )
        );

        EBClientRegister.registerParticleProviders(collection ->
                collection.forEach((type, provider) -> {
                    var reg = ParticleFactoryRegistry.getInstance();
                    reg.register(type.get(), provider::apply);
                })
        );


    }

    private ParticleRegistryFabric() {}
}
