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

public class ParticleMagicBubble extends ParticleWizardry {
    public ParticleMagicBubble(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setColor(1, 1, 1);
        this.setSize(0.02F, 0.02F);
        this.scale(this.random.nextFloat() * 0.6F + 0.2F);
        this.lifetime = (int) (8.0D / (Math.random() * 0.8D + 0.2D));
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.yo += 0.002D;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.8500000238418579D;
        this.yd *= 0.8500000238418579D;
        this.zd *= 0.8500000238418579D;

        if (this.lifetime-- <= 0) {
            this.remove();
        }
    }

    public static class MagicBubbleProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public MagicBubbleProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleMagicBubble(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleMagicBubble(world, x, y, z, spriteProvider);
        }
    }
}
