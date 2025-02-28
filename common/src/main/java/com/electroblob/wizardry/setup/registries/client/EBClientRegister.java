package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.api.common.DeferredObject;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class EBClientRegister {

    public static void registerParticleTypes(Consumer<Map<String, DeferredObject<SimpleParticleType>>> handler) {
        EBParticles.handleParticleTypeRegistration(handler);
    }

    public static void registerParticleProviders(Consumer<Map<DeferredObject<SimpleParticleType>, Function<SpriteSet, ParticleProvider<SimpleParticleType>>>> handler) {
        EBParticles.handleParticleProviderRegistration(handler);
    }

    private EBClientRegister() {}
}
