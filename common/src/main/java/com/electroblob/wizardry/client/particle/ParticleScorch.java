package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ParticleScorch extends ParticleWizardry {
    public ParticleScorch(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.gravity = 0;
        this.setLifetime(100 + random.nextInt(40));
        this.scale(2);
        this.setColor(0, 0, 0);
        this.shaded = false;
        this.adjustQuadSize = false;

        // Set a random sprite from the spriteProvider
        this.setSprite(spriteProvider.get(world.random));
    }

    @Override
    public void tick() {
        super.tick();

        float ageFraction = Math.min((float) this.age / ((float) this.lifetime * 0.5f), 1);

        this.rCol = this.initialRed + (this.fadeRed - this.initialRed) * ageFraction;
        this.gCol = this.initialGreen + (this.fadeGreen - this.initialGreen) * ageFraction;
        this.bCol = this.initialBlue + (this.fadeBlue - this.initialBlue) * ageFraction;

        if (this.age > this.lifetime / 2) {
            this.setAlpha(1 - ((float) this.age - this.lifetime / 2f) / (this.lifetime / 2f));
        }

        Direction facing = Direction.fromYRot(yaw);
        if (pitch == 90) facing = Direction.UP;
        if (pitch == -90) facing = Direction.DOWN;

        if (!level.getBlockState(new BlockPos((int) x, (int) y, (int) z).relative(facing.getOpposite())).isSolid()) {
            this.remove();
        }
    }

    public static class ScorchProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteSet;

        public ScorchProvider(SpriteSet sprite) {
            spriteSet = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleScorch(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteSet);
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleScorch(world, x, y, z, spriteSet);
        }
    }
}
