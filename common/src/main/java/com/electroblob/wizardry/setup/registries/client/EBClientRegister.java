package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.api.content.DeferredObject;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

// TODO Something about the old particle registry that I need to fix...
public final class EBClientRegister {
    private EBClientRegister() {}

    public static void registerParticleProviders(Consumer<Map<DeferredObject<SimpleParticleType>, Function<SpriteSet, ParticleProvider<SimpleParticleType>>>> handler) {
        EBParticles.registerProvider(handler);
    }
}
