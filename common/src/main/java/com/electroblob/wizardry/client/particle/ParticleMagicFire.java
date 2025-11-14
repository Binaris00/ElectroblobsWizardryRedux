package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ParticleMagicFire extends ParticleWizardry {
    public ParticleMagicFire(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, true);
        this.setColor(1, 1, 1);
        this.alpha = 1;
        this.lifetime = 12 + random.nextInt(4);
        this.shaded = false;
        this.hasPhysics = true;

        // Set a random sprite from the spriteProvider
        this.setSprite(spriteProvider.get(world.random));
    }

    public static class MagicFireProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public MagicFireProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleMagicFire(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleMagicFire(world, x, y, z, spriteProvider);
        }
    }
}
