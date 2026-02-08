package com.binaris.wizardry.client.particle;

import com.binaris.wizardry.api.client.particle.ParticleWizardry;
import com.binaris.wizardry.core.platform.Services;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleCloud extends ParticleWizardry {
    ParticleRenderType renderType = new ParticleRenderType() {
        @Override
        public void begin(@NotNull BufferBuilder bufferBuilder, @NotNull TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            textureManager.getTexture(TextureAtlas.LOCATION_PARTICLES).setFilter(false, false);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);

            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }


        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        @Override
        public String toString() {
            return "PARTICLE_CLOUD_TRANSLUCENT";
        }
    };

    public ParticleCloud(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setColor(1, 1, 1);
        this.lifetime = 48 + this.random.nextInt(12);
        this.scale(3);
        this.setGravity(false);
        this.setAlpha(0);
        this.hasPhysics = false;
        this.shaded = false;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return Services.PLATFORM.getPlatformName().equals("Forge") ? renderType : ParticleRenderType.PARTICLE_SHEET_LIT;
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
