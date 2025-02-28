package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ParticleSpark extends ParticleWizardry {
    public ParticleSpark(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, true);
        this.scale(1.4f);
        this.setColor(1, 1, 1);
        this.shaded = false;
        this.hasPhysics = false;
        this.setLifetime(3);
        this.setSpriteFromAge(spriteProvider);
    }

    public static class SparkProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteSet;

        public SparkProvider(SpriteSet sprite) {
            spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleSpark(world, x, y, z, spriteSet);
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleSpark(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteSet);
        }
    }
}
