package com.electroblob.wizardry.setup.registries.client;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import com.electroblob.wizardry.api.common.DeferredObject;
import com.electroblob.wizardry.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
public class EBParticles {

    /** 3D-rendered light-beam particle.<p></p><b>Defaults:</b><br>Lifetime: 1 tick<br> Colour: white */
    @Deprecated
    public static final DeferredObject<SimpleParticleType> BEAM;
    /** Helical animated 'buffing' particle.<p></p><b>Defaults:</b><br>Lifetime: 15 ticks
     * <br>Velocity: (0, 0.27, 0)<br>Colour: white */
    public static final DeferredObject<SimpleParticleType> BUFF;
    /** 3D-rendered lightning particle.<p></p><b>Defaults:</b><br>Lifetime: 3 ticks<br> Colour: blue */
    public static final DeferredObject<SimpleParticleType> LIGHTNING;
    /** Animated flame.<p></p><b>Defaults:</b><br>Lifetime: 12-16 ticks<br> */
    public static final DeferredObject<SimpleParticleType> MAGIC_FIRE;
    /** Animated sparkle particle.<p></p><b>Defaults:</b><<br>Lifetime: 48-60 ticks<br>Colour: white */
    public static final DeferredObject<SimpleParticleType> SPARKLE;
    /** Spiral particle, like potions.<p></p><b>Defaults:</b><br>Lifetime: 8-40 ticks<br>Colour: white */
    public static final DeferredObject<SimpleParticleType> DARK_MAGIC;
    /** Snowflake particle.<p></p><b>Defaults:</b><br>Lifetime: 40-50 ticks<br>Velocity: (0, -0.02, 0) */
    public static final DeferredObject<SimpleParticleType> SNOW;
    /** Scorch mark.<p></p><b>Defaults:</b><br>Lifetime: 100-140 ticks<br>Colour: black<br>Fade: black */
    public static final DeferredObject<SimpleParticleType> SCORCH;
    /** Soft-edged round particle.<p></p><b>Defaults:</b><br>Lifetime: 8-40 ticks<br>Colour: white */
    public static final DeferredObject<SimpleParticleType> PATH;
    /** Single leaf.<p></p><b>Defaults:</b><br>Lifetime: 10-15 ticks<br>Velocity: (0, -0.03, 0)
     * <br>Colour: green/brown */
    public static final DeferredObject<SimpleParticleType> LEAF;
    /** Small shard of ice.<p></p><b>Defaults:</b><br>Lifetime: 8-40 ticks<br>Gravity: true */
    public static final DeferredObject<SimpleParticleType> ICE;
    /** Large, thick cloud.<p></p><b>Defaults:</b><br>Lifetime: 48-60 ticks<br> Colour: dark grey */
    public static final DeferredObject<SimpleParticleType> CLOUD;
    /** Bubble that doesn't burst in air.<p></p><b>Defaults:</b><br>Lifetime: 8-40 ticks */
    public static final DeferredObject<SimpleParticleType> MAGIC_BUBBLE;
    /** Animated lightning particle.<p></p><b>Defaults:</b><br>Lifetime: 3 ticks */
    public static final DeferredObject<SimpleParticleType> SPARK;
    /** Single pixel particle.<p></p><b>Defaults:</b><br>Lifetime: 16-80 ticks<br>Colour: white */
    public static final DeferredObject<SimpleParticleType> DUST;
    /** Rapid flash, like fireworks.<p></p><b>Defaults:</b><br>Lifetime: 6 ticks<br>Colour: white */
    public static final DeferredObject<SimpleParticleType> FLASH;
    /** Particle that looks like the guardian's beam attack.<p></p><b>Defaults:</b><br>Lifetime: 1 tick */
    public static final DeferredObject<SimpleParticleType> GUARDIAN_BEAM;
    /** 2D lightning effect, normally on the ground.<p></p><b>Defaults:</b><br>Lifetime: 7 ticks
     * <br>Facing: up */
    public static final DeferredObject<SimpleParticleType> LIGHTNING_PULSE;
    /** 3D-rendered expanding sphere.<p></p><b>Defaults:</b><<br>Lifetime: 6 ticks<br>Colour: white */
    public static final DeferredObject<SimpleParticleType> SPHERE;


    static {
        BEAM            = Register.register("beam", ParticleBeam.BeamProvider::createParticle, ParticleBeam.BeamProvider::new);
        BUFF            = Register.register("buff", ParticleBuff.BuffProvider::createParticle, ParticleBuff.BuffProvider::new);
        LIGHTNING       = Register.register("lightning", ParticleLightning.LightningProvider::createParticle, ParticleLightning.LightningProvider::new);
        MAGIC_FIRE      = Register.register("magic_fire", ParticleMagicFire.MagicFireProvider::createParticle, ParticleMagicFire.MagicFireProvider::new);
        SPARKLE         = Register.register("sparkle", ParticleSparkle.SparkleProvider::createParticle, ParticleSparkle.SparkleProvider::new);
        DARK_MAGIC      = Register.register("dark_magic", ParticleDarkMagic.DarkMagicProvider::createParticle, ParticleDarkMagic.DarkMagicProvider::new);
        SNOW            = Register.register("snow", ParticleSnow.SnowProvider::createParticle, ParticleSnow.SnowProvider::new);
        SCORCH          = Register.register("scorch", ParticleScorch.ScorchProvider::createParticle, ParticleScorch.ScorchProvider::new);
        PATH            = Register.register("path", ParticlePath.PathProvider::createParticle, ParticlePath.PathProvider::new);
        LEAF            = Register.register("leaf", ParticleLeaf.LeafProvider::createParticle, ParticleLeaf.LeafProvider::new);
        ICE             = Register.register("ice", ParticleIce.IceProvider::createParticle, ParticleIce.IceProvider::new);
        CLOUD           = Register.register("cloud", ParticleCloud.CloudProvider::createParticle, ParticleCloud.CloudProvider::new);
        MAGIC_BUBBLE    = Register.register("magic_bubble", ParticleMagicBubble.MagicBubbleProvider::createParticle, ParticleMagicBubble.MagicBubbleProvider::new);
        SPARK           = Register.register("spark", ParticleSpark.SparkProvider::createParticle, ParticleSpark.SparkProvider::new);
        DUST            = Register.register("dust", ParticleDust.DustProvider::createParticle, ParticleDust.DustProvider::new);
        FLASH           = Register.register("flash", ParticleFlash.FlashProvider::createParticle, ParticleFlash.FlashProvider::new);
        GUARDIAN_BEAM   = Register.register("guardian_beam", ParticleGuardianBeam.GuardianBeamProvider::createParticle, ParticleGuardianBeam.GuardianBeamProvider::new);
        LIGHTNING_PULSE = Register.register("lightning_pulse", ParticleLightningPulse.LightningPulseProvider::createParticle, ParticleLightningPulse.LightningPulseProvider::new);
        SPHERE          = Register.register("sphere", ParticleSphere.SphereProvider::createParticle, ParticleSphere.SphereProvider::new);
    }

    static void handleParticleTypeRegistration(Consumer<Map<String, DeferredObject<SimpleParticleType>>> handler) {
        handler.accept(Register.PARTICLE_TYPES);
    }

    static void handleParticleProviderRegistration(Consumer<Map<DeferredObject<SimpleParticleType>, Function<SpriteSet, ParticleProvider<SimpleParticleType>>>> handler) {
        handler.accept(Register.PARTICLE_PROVIDERS);
    }

    static class Register {

        static final Map<String, DeferredObject<SimpleParticleType>> PARTICLE_TYPES = new HashMap<>();
        static final Map<DeferredObject<SimpleParticleType>, Function<SpriteSet, ParticleProvider<SimpleParticleType>>> PARTICLE_PROVIDERS = new HashMap<>();

        static <T extends ParticleType<?>> DeferredObject<SimpleParticleType> register(String name, BiFunction<ClientLevel, Vec3, ParticleWizardry> factory, Function<SpriteSet, ParticleProvider<SimpleParticleType>> provider) {
            DeferredObject<SimpleParticleType> ret = new DeferredObject<>(() -> new SimpleParticleType(false){});
            PARTICLE_TYPES.put(name, ret);
            PARTICLE_PROVIDERS.put(ret, provider);
            ParticleWizardry.PROVIDERS.put(ret, factory);
            return ret;
        }
    }

    private EBParticles(){}
}
