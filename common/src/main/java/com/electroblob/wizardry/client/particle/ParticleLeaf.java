package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ParticleLeaf extends ParticleWizardry {
    public ParticleLeaf(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setParticleSpeed(0, -0.03, 0);
        this.setLifetime(10 + random.nextInt(5));
        this.scale(1.8f);
        this.gravity = 0;
        this.hasPhysics = true;
        this.setColor(0.1f + 0.3f * random.nextFloat(), 0.5f + 0.3f * random.nextFloat(), 0.1f);

        // Set a random sprite from the spriteProvider
        this.setSprite(spriteProvider.get(world.random));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age > this.lifetime / 2) {
            this.setAlpha(1 - ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime);
        }
    }

    public static class LeafProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public LeafProvider(SpriteSet spriteProvider) {
            LeafProvider.spriteProvider = spriteProvider;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleLeaf(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleLeaf(world, x, y, z, spriteProvider);
        }
    }
}
