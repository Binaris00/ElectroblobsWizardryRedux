package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ParticleLightningPulse extends ParticleWizardry {
    public ParticleLightningPulse(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, true);
        this.quadSize = 6f;
        this.setColor(1, 1, 1);
        this.shaded = false;
        this.hasPhysics = false;
        this.setLifetime(7);
        this.pitch = 90;
        this.yaw = 0;
    }

    public static class LightningPulseProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public LightningPulseProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleLightningPulse(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleLightningPulse(world, x, y, z, spriteProvider);
        }
    }
}
