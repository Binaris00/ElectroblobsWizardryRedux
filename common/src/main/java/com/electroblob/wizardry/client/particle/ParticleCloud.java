package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleCloud extends ParticleWizardry {
    public ParticleCloud(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setColor(1, 1, 1);
        this.lifetime = 48 + this.random.nextInt(12);
        this.scale(3);
        this.setGravity(false);
        this.setAlpha(0);
        this.hasPhysics = false;
        this.shaded = true;
    }

    @Override
    public void tick() {
        super.tick();

        float fadeTime = this.lifetime * 0.3f;
        this.setAlpha(Mth.clamp(Math.min(this.age / fadeTime, (this.lifetime - this.age) / fadeTime), 0, 1));
    }

    public static class CloudProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public CloudProvider(SpriteSet spriteProvider) {
            CloudProvider.spriteProvider = spriteProvider;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleCloud(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleCloud(world, x, y, z, spriteProvider);
        }
    }
}
