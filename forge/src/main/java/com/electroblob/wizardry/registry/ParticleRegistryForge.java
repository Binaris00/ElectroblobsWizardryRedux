package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.client.EBClientRegister;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = WizardryMainMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ParticleRegistryForge {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, WizardryMainMod.MOD_ID);

    public static void register() {
        EBClientRegister.registerParticleTypes(collection -> {
            collection.forEach(PARTICLE_TYPES::register);
        });
    }

    @SubscribeEvent
    public static void registerProviders(RegisterParticleProvidersEvent event) {
        EBClientRegister.registerParticleProviders(collection -> {
            collection.forEach((type, provider)
                    -> event.registerSpriteSet(type.get(), provider::apply)
            );
        });
    }

    private ParticleRegistryForge() {}
}
