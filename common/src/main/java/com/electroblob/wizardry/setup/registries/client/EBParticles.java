package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.RegisterFunction;
import com.electroblob.wizardry.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class EBParticles {
    static final Map<String, DeferredObject<SimpleParticleType>> PARTICLE_TYPES = new HashMap<>();
    static final Map<DeferredObject<SimpleParticleType>, Function<SpriteSet, ParticleProvider<SimpleParticleType>>> PARTICLE_PROVIDERS = new HashMap<>();

    private EBParticles(){}

    @Deprecated
    public static final DeferredObject<SimpleParticleType> BEAM = particle("beam", ParticleBeam.BeamProvider::createParticle, ParticleBeam.BeamProvider::new);;
    public static final DeferredObject<SimpleParticleType> BUFF = particle("buff", ParticleBuff.BuffProvider::createParticle, ParticleBuff.BuffProvider::new);;
    public static final DeferredObject<SimpleParticleType> LIGHTNING = particle("lightning", ParticleLightning.LightningProvider::createParticle, ParticleLightning.LightningProvider::new);;
    public static final DeferredObject<SimpleParticleType> MAGIC_FIRE = particle("magic_fire", ParticleMagicFire.MagicFireProvider::createParticle, ParticleMagicFire.MagicFireProvider::new);;
    public static final DeferredObject<SimpleParticleType> SPARKLE = particle("sparkle", ParticleSparkle.SparkleProvider::createParticle, ParticleSparkle.SparkleProvider::new);
    public static final DeferredObject<SimpleParticleType> DARK_MAGIC = particle("dark_magic", ParticleDarkMagic.DarkMagicProvider::createParticle, ParticleDarkMagic.DarkMagicProvider::new);;
    public static final DeferredObject<SimpleParticleType> SNOW = particle("snow", ParticleSnow.SnowProvider::createParticle, ParticleSnow.SnowProvider::new);;
    public static final DeferredObject<SimpleParticleType> SCORCH = particle("scorch", ParticleScorch.ScorchProvider::createParticle, ParticleScorch.ScorchProvider::new);;
    public static final DeferredObject<SimpleParticleType> PATH = particle("path", ParticlePath.PathProvider::createParticle, ParticlePath.PathProvider::new);;
    public static final DeferredObject<SimpleParticleType> LEAF = particle("leaf", ParticleLeaf.LeafProvider::createParticle, ParticleLeaf.LeafProvider::new);;
    public static final DeferredObject<SimpleParticleType> ICE = particle("ice", ParticleIce.IceProvider::createParticle, ParticleIce.IceProvider::new);;
    public static final DeferredObject<SimpleParticleType> CLOUD = particle("cloud", ParticleCloud.CloudProvider::createParticle, ParticleCloud.CloudProvider::new);;
    public static final DeferredObject<SimpleParticleType> MAGIC_BUBBLE = particle("magic_bubble", ParticleMagicBubble.MagicBubbleProvider::createParticle, ParticleMagicBubble.MagicBubbleProvider::new);;
    public static final DeferredObject<SimpleParticleType> SPARK = particle("spark", ParticleSpark.SparkProvider::createParticle, ParticleSpark.SparkProvider::new);;
    public static final DeferredObject<SimpleParticleType> DUST = particle("dust", ParticleDust.DustProvider::createParticle, ParticleDust.DustProvider::new);;
    public static final DeferredObject<SimpleParticleType> FLASH = particle("flash", ParticleFlash.FlashProvider::createParticle, ParticleFlash.FlashProvider::new);;
    public static final DeferredObject<SimpleParticleType> GUARDIAN_BEAM = particle("guardian_beam", ParticleGuardianBeam.GuardianBeamProvider::createParticle, ParticleGuardianBeam.GuardianBeamProvider::new);;
    public static final DeferredObject<SimpleParticleType> LIGHTNING_PULSE = particle("lightning_pulse", ParticleLightningPulse.LightningPulseProvider::createParticle, ParticleLightningPulse.LightningPulseProvider::new);;
    public static final DeferredObject<SimpleParticleType> SPHERE = particle("sphere", ParticleSphere.SphereProvider::createParticle, ParticleSphere.SphereProvider::new);;

    static void handleParticleTypeRegistration(Consumer<Map<String, DeferredObject<SimpleParticleType>>> handler) {
        handler.accept(PARTICLE_TYPES);
    }

    // ======= Registry =======
    public static void registerType(RegisterFunction<ParticleType<?>> function){
        PARTICLE_TYPES.forEach(((id, particle) -> {
            function.register(BuiltInRegistries.PARTICLE_TYPE, WizardryMainMod.location(id), particle.get());
        }));
    }

    static void registerProvider(Consumer<Map<DeferredObject<SimpleParticleType>, Function<SpriteSet, ParticleProvider<SimpleParticleType>>>> handler) {
        handler.accept(PARTICLE_PROVIDERS);
    }

    // ======= Helpers =======
    static <T extends ParticleType<?>> DeferredObject<SimpleParticleType> particle(String name, BiFunction<ClientLevel, Vec3, ParticleWizardry> factory, Function<SpriteSet, ParticleProvider<SimpleParticleType>> provider) {
        DeferredObject<SimpleParticleType> ret = new DeferredObject<>(() -> new SimpleParticleType(false){});
        PARTICLE_TYPES.put(name, ret);
        PARTICLE_PROVIDERS.put(ret, provider);
        ParticleWizardry.PROVIDERS.put(ret, factory);
        return ret;
    }
}
