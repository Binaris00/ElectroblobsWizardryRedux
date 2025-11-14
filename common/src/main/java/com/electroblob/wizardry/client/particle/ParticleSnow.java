package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ParticleSnow extends ParticleWizardry {
    public ParticleSnow(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, true);
        this.setParticleSpeed(0, -0.02, 0);
        this.scale(0.6f);
        this.gravity = 0;
        this.hasPhysics = true;
        this.setLifetime(40 + random.nextInt(10));
        this.setColor(0.9f + 0.1f * random.nextFloat(), 0.95f + 0.05f * random.nextFloat(), 1);

        // Set a random sprite from the spriteProvider
        this.setSprite(spriteProvider.get(world.random));
    }

    public static class SnowProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteSet;

        public SnowProvider(SpriteSet sprite) {
            spriteSet = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleSnow(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteSet);
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleSnow(world, x, y, z, spriteSet);
        }
    }
}
