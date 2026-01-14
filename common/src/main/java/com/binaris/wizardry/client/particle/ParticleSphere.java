package com.binaris.wizardry.client.particle;

import com.binaris.wizardry.api.client.particle.ParticleWizardry;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleSphere extends ParticleWizardry {
    public ParticleSphere(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setColor(1, 1, 1);
        this.lifetime = 5;
        this.alpha = 0.8f;
    }

    private static void drawSphere(PoseStack stack, BufferBuilder buffer, float radius, float latStep, float longStep, boolean inside, float r, float g, float b, float a) {
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        boolean goingUp = inside;

        buffer.vertex(stack.last().pose(), 0, goingUp ? -radius : radius, 0).color(r, g, b, a).endVertex();

        for (float longitude = -(float) Math.PI; longitude <= (float) Math.PI; longitude += longStep) {
            for (float theta = (float) Math.PI / 2 - latStep; theta >= -(float) Math.PI / 2 + latStep; theta -= latStep) {
                float latitude = goingUp ? -theta : theta;

                float hRadius = radius * Mth.cos(latitude);
                float vy = radius * Mth.sin(latitude);
                float vx = hRadius * Mth.sin(longitude);
                float vz = hRadius * Mth.cos(longitude);

                buffer.vertex(stack.last().pose(), vx, vy, vz).color(r, g, b, a).endVertex();

                vx = hRadius * Mth.sin(longitude + longStep);
                vz = hRadius * Mth.cos(longitude + longStep);

                buffer.vertex(stack.last().pose(), vx, vy, vz).color(r, g, b, a).endVertex();
            }

            buffer.vertex(stack.last().pose(), 0, goingUp ? radius : -radius, 0).color(r, g, b, a).endVertex();

            goingUp = !goingUp;
        }

        BufferUploader.drawWithShader(buffer.end());
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(@NotNull VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        PoseStack stack = new PoseStack();

        updateEntityLinking(tickDelta);

        float x = (float) (this.xo + (this.x - this.xo) * (double) tickDelta);
        float y = (float) (this.yo + (this.y - this.yo) * (double) tickDelta);
        float z = (float) (this.zo + (this.z - this.zo) * (double) tickDelta);

        stack.pushPose();
        stack.translate(x - camera.getPosition().x, y - camera.getPosition().y, z - camera.getPosition().z);

        RenderSystem.enableBlend();
        RenderSystem.enableCull();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        float latStep = (float) Math.PI / 20;
        float longStep = (float) Math.PI / 20;

        float size = this.quadSize * 10.0f;

        float sphereRadius = size * (this.age + tickDelta - 1) / this.lifetime;
        float alpha = this.alpha * (1 - (this.age + tickDelta - 1) / this.lifetime);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder buffer = tess.getBuilder();
        drawSphere(stack, buffer, sphereRadius, latStep, longStep, true, rCol, gCol, bCol, alpha);
        drawSphere(stack, buffer, sphereRadius, latStep, longStep, false, rCol, gCol, bCol, alpha);

        RenderSystem.disableCull();
        RenderSystem.disableBlend();

        stack.popPose();
    }

    public static class SphereProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteSet;

        public SphereProvider(SpriteSet sprite) {
            spriteSet = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleSphere(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteSet);
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleSphere(world, x, y, z, spriteSet);
        }
    }
}
