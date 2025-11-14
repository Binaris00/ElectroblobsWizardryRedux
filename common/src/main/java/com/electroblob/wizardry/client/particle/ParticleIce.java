package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleIce extends ParticleWizardry {
    public ParticleIce(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.hasPhysics = true;

        this.setColor(1, 1, 1);
        this.scale(1.2f);
        this.setGravity(true);
        this.shaded = false;

        // Set a random sprite from the spriteProvider
        this.setSprite(spriteProvider.get(world.random));
    }

    public static class IceProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public IceProvider(SpriteSet spriteProvider) {
            IceProvider.spriteProvider = spriteProvider;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleIce(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleIce(world, x, y, z, spriteProvider);
        }
    }
}
