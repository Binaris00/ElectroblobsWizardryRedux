package com.binaris.wizardry.client.particle;

import com.binaris.wizardry.api.client.particle.ParticleTargeted;
import com.binaris.wizardry.api.client.particle.ParticleWizardry;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleBeam extends ParticleTargeted {
    private static final float THICKNESS = 0.1f;

    public ParticleBeam(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setColor(1, 1, 1);
        this.setLifetime(0);
        this.quadSize = 1;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    protected void draw(PoseStack stack, Tesselator tesselator, float length, float tickDelta) {
        float scale = this.quadSize;

        if (this.lifetime > 0) {
            float ageFraction = (age + tickDelta - 1) / lifetime;
            scale = this.quadSize * (1 - ageFraction * ageFraction);
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShaderColor(240f / 255f, 240f / 255f, 240f / 255f, 1f);

        for (int layer = 0; layer < 3; layer++) {
            drawSegment(stack, tesselator, layer, length, THICKNESS * scale);
        }

        RenderSystem.disableBlend();
    }

    private void drawSegment(PoseStack stack, Tesselator tesselator, int layer, float v, float thickness) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = tesselator.getBuilder();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        switch (layer) {
            case 0:
                drawShearedBox(stack, buffer, v, 0.25f * thickness, 1, 1, 1, 1);
                break;

            case 1:
                drawShearedBox(stack, buffer, v, 0.6f * thickness, (rCol + 1) / 2, (gCol + 1) / 2, (bCol + 1) / 2, 0.65f);
                break;

            case 2:
                drawShearedBox(stack, buffer, v, thickness, rCol, gCol, bCol, 0.3f);
                break;
        }

        BufferUploader.drawWithShader(buffer.end());
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
    }

    private void drawShearedBox(PoseStack stack, BufferBuilder buffer, float length, float width, float r, float g, float b, float a) {
        buffer.vertex(stack.last().pose(), (float) 0.0 - width, (float) 0.0 - width, (float) 0.0).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), (float) 0.0 - width, (float) 0.0 - width, length).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), (float) 0.0 - width, (float) 0.0 + width, (float) 0.0).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), (float) 0.0 - width, (float) 0.0 + width, length).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), (float) 0.0 + width, (float) 0.0 + width, (float) 0.0).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), (float) 0.0 + width, (float) 0.0 + width, length).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), (float) 0.0 + width, (float) 0.0 - width, (float) 0.0).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), (float) 0.0 + width, (float) 0.0 - width, length).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), (float) 0.0 - width, (float) 0.0 - width, (float) 0.0).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), (float) 0.0 - width, (float) 0.0 - width, length).color(r, g, b, a).endVertex();
    }

    public static class BeamProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public BeamProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleBeam(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleBeam(world, x, y, z, spriteProvider);
        }
    }
}
