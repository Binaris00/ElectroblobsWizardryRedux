package com.binaris.wizardry.client.particle;

import com.binaris.wizardry.api.client.particle.ParticleWizardry;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Based on the vanilla {@code ParticleParticle}, but with modified rendering to allow for a pulsing scale and a fade out.
 */
public class ParticleFlash extends ParticleWizardry {

    public ParticleFlash(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setColor(1, 1, 1);
        this.lifetime = 24;
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        float ageProgress = ((float) this.age + partialTicks) / (float) this.lifetime;
        float exponentialFade = (float) Math.pow(1.0F - ageProgress, 3.5);
        float maxAlpha = 0.6F;
        float fadeIn = 1.0F;
        if (ageProgress < 0.2F) {
            fadeIn = Mth.sin((ageProgress / 0.2F) * 0.5F * (float) Math.PI);
        }

        float alpha = maxAlpha * exponentialFade * fadeIn;

        this.setAlpha(Math.max(0, alpha));

        super.render(buffer, renderInfo, partialTicks);
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float ageProgress = ((float) this.age + scaleFactor) / (float) this.lifetime;
        float baseScale = 0.4F;

        float growthProgress = Math.min(ageProgress * 2.5F, 1.0F);
        float sizeMultiplier = Mth.sin(growthProgress * 0.5F * (float) Math.PI);

        if (ageProgress > 0.3F) {
            float shrinkProgress = (ageProgress - 0.3F) / 0.7F;
            float shrinkFactor = 1.0F - (shrinkProgress * 0.6F);
            sizeMultiplier *= shrinkFactor;
        }

        return baseScale * sizeMultiplier;
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