package com.binaris.wizardry.client.particle;

import com.binaris.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleSparkle extends ParticleWizardry {
    public ParticleSparkle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, true);

        this.setColor(1, 1, 1);
        this.lifetime = 48 + this.random.nextInt(12);
        this.scale(0.75f);
        this.gravity = 0;
        this.hasPhysics = false;
        this.shaded = false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age > this.lifetime / 2) {
            this.setAlpha(1 - ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime);
        }
    }

    public static class SparkleProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteSet;

        public SparkleProvider(SpriteSet sprite) {
            spriteSet = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleSparkle(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteSet);
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleSparkle(world, x, y, z, spriteSet);
        }
    }

}
