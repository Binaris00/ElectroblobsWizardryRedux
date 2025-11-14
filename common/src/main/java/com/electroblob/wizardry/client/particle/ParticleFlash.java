package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Based on the vanilla {@code ParticleParticle}, but with modified rendering to allow for a pulsing scale and a fade out
 */
public class ParticleFlash extends ParticleWizardry {

    public ParticleFlash(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setColor(1, 1, 1);
        this.lifetime = 6;
    }

    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        float ageProgress = ((float) this.age + partialTicks - 1.0F) / (float) this.lifetime;
        float alpha = 0.6F - ageProgress * 0.5F;
        this.setAlpha(Math.max(0, alpha));
        super.render(buffer, renderInfo, partialTicks);
    }

    public float getQuadSize(float scaleFactor) {
        return 0.17F * Mth.sin(((float) this.age + scaleFactor - 1.0F) * 0.25F * (float) Math.PI);
    }

    public static class FlashProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public FlashProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleFlash(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleFlash(world, x, y, z, spriteProvider);
        }
    }
}