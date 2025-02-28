package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ParticlePath extends ParticleWizardry {
    public ParticlePath(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.quadSize = 0.1f * 1.25f;
        this.gravity = 0;
        this.shaded = false;
        this.hasPhysics = false;
        this.setColor(1, 1, 1);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        this.move(this.xd, this.yd, this.zd);

        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - 2 * (((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime));
        }
    }

    public static class PathProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteSet;

        public PathProvider(SpriteSet sprite) {
            spriteSet = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticlePath(world, x, y, z, spriteSet);
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticlePath(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteSet);
        }
    }
}
