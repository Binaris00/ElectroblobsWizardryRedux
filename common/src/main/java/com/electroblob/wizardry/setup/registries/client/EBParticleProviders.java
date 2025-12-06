package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class EBParticleProviders {
    static final Map<DeferredObject<SimpleParticleType>, Function<SpriteSet, ParticleProvider<SimpleParticleType>>> PARTICLE_PROVIDERS = new HashMap<>();

    private EBParticleProviders() {
    }

    // Register all particle providers - this is called during static initialization
    static {
        registerParticle(EBParticles.BEAM, ParticleBeam.BeamProvider::createParticle, ParticleBeam.BeamProvider::new);
        registerParticle(EBParticles.GUARDIAN_BEAM, ParticleGuardianBeam.GuardianBeamProvider::createParticle, ParticleGuardianBeam.GuardianBeamProvider::new);
        registerParticle(EBParticles.BUFF, ParticleBuff.BuffProvider::createParticle, ParticleBuff.BuffProvider::new);
        registerParticle(EBParticles.MAGIC_FIRE, ParticleMagicFire.MagicFireProvider::createParticle, ParticleMagicFire.MagicFireProvider::new);
        registerParticle(EBParticles.SPARKLE, ParticleSparkle.SparkleProvider::createParticle, ParticleSparkle.SparkleProvider::new);
        registerParticle(EBParticles.DARK_MAGIC, ParticleDarkMagic.DarkMagicProvider::createParticle, ParticleDarkMagic.DarkMagicProvider::new);
        registerParticle(EBParticles.SNOW, ParticleSnow.SnowProvider::createParticle, ParticleSnow.SnowProvider::new);
        registerParticle(EBParticles.LEAF, ParticleLeaf.LeafProvider::createParticle, ParticleLeaf.LeafProvider::new);
        registerParticle(EBParticles.ICE, ParticleIce.IceProvider::createParticle, ParticleIce.IceProvider::new);
        registerParticle(EBParticles.CLOUD, ParticleCloud.CloudProvider::createParticle, ParticleCloud.CloudProvider::new);
        registerParticle(EBParticles.MAGIC_BUBBLE, ParticleMagicBubble.MagicBubbleProvider::createParticle, ParticleMagicBubble.MagicBubbleProvider::new);
        registerParticle(EBParticles.SPARK, ParticleSpark.SparkProvider::createParticle, ParticleSpark.SparkProvider::new);
        registerParticle(EBParticles.DUST, ParticleDust.DustProvider::createParticle, ParticleDust.DustProvider::new);
        registerParticle(EBParticles.LIGHTNING_PULSE, ParticleLightningPulse.LightningPulseProvider::createParticle, ParticleLightningPulse.LightningPulseProvider::new);
        registerParticle(EBParticles.SPHERE, ParticleSphere.SphereProvider::createParticle, ParticleSphere.SphereProvider::new);
        registerParticle(EBParticles.FLASH, ParticleFlash.FlashProvider::createParticle, ParticleFlash.FlashProvider::new);

        // Deprecated particles
        registerParticle(EBParticles.SCORCH, ParticleScorch.ScorchProvider::createParticle, ParticleScorch.ScorchProvider::new);
        registerParticle(EBParticles.PATH, ParticlePath.PathProvider::createParticle, ParticlePath.PathProvider::new);
        registerParticle(EBParticles.LIGHTNING, ParticleLightning.LightningProvider::createParticle, ParticleLightning.LightningProvider::new);
    }

    private static void registerParticle(DeferredObject<SimpleParticleType> particleType, BiFunction<ClientLevel, Vec3, ParticleWizardry> factory, Function<SpriteSet, ParticleProvider<SimpleParticleType>> provider) {
        PARTICLE_PROVIDERS.put(particleType, provider);
        ParticleWizardry.PROVIDERS.put(particleType.get(), factory);
    }

    // ====== Registry =======
    public static void registerProvider(BiConsumer<DeferredObject<SimpleParticleType>, Function<SpriteSet, ParticleProvider<SimpleParticleType>>> function) {
        PARTICLE_PROVIDERS.forEach(function);
    }
}
