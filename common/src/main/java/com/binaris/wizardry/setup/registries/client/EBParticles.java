package com.binaris.wizardry.setup.registries.client;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.api.content.util.RegisterFunction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class EBParticles {
    static final Map<String, DeferredObject<SimpleParticleType>> PARTICLE_TYPES = new HashMap<>();
    public static final DeferredObject<SimpleParticleType> BEAM = particle("beam");
    public static final DeferredObject<SimpleParticleType> GUARDIAN_BEAM = particle("guardian_beam");
    public static final DeferredObject<SimpleParticleType> BUFF = particle("buff");
    public static final DeferredObject<SimpleParticleType> MAGIC_FIRE = particle("magic_fire");
    public static final DeferredObject<SimpleParticleType> SPARKLE = particle("sparkle");
    public static final DeferredObject<SimpleParticleType> DARK_MAGIC = particle("dark_magic");
    public static final DeferredObject<SimpleParticleType> SNOW = particle("snow");
    public static final DeferredObject<SimpleParticleType> LEAF = particle("leaf");
    public static final DeferredObject<SimpleParticleType> ICE = particle("ice");
    public static final DeferredObject<SimpleParticleType> CLOUD = particle("cloud");
    public static final DeferredObject<SimpleParticleType> MAGIC_BUBBLE = particle("magic_bubble");
    public static final DeferredObject<SimpleParticleType> SPARK = particle("spark");
    public static final DeferredObject<SimpleParticleType> DUST = particle("dust");
    public static final DeferredObject<SimpleParticleType> LIGHTNING_PULSE = particle("lightning_pulse");
    public static final DeferredObject<SimpleParticleType> SPHERE = particle("sphere");
    public static final DeferredObject<SimpleParticleType> FLASH = particle("flash");
    @Deprecated
    public static final DeferredObject<SimpleParticleType> SCORCH = particle("scorch");
    @Deprecated
    public static final DeferredObject<SimpleParticleType> PATH = particle("path");
    @Deprecated
    public static final DeferredObject<SimpleParticleType> LIGHTNING = particle("lightning");

    private EBParticles() {
    }

    // ======= Registry =======
    public static void registerType(RegisterFunction<ParticleType<?>> function) {
        PARTICLE_TYPES.forEach(((id, particle) -> {
            function.register(BuiltInRegistries.PARTICLE_TYPE, WizardryMainMod.location(id), particle.get());
        }));
    }

    // ======= Helpers =======
    static DeferredObject<SimpleParticleType> particle(String name) {
        DeferredObject<SimpleParticleType> ret = new DeferredObject<>(() -> new SimpleParticleType(false) {
        });
        PARTICLE_TYPES.put(name, ret);
        return ret;
    }

    public static Collection<String> getParticleNames() {
        Collection<String> ret = new ArrayList<>();

        PARTICLE_TYPES.forEach((k, v) -> ret.add(WizardryMainMod.location(k).toString()));
        return ret;
    }
}
