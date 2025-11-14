package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.api.client.particle.ParticleTargeted;
import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
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

public class ParticleLightning extends ParticleTargeted {
    private static final float THICKNESS = 0.04f;

    private static final float MAX_SEGMENT_LENGTH = 0.6f;

    private static final float MIN_SEGMENT_LENGTH = 0.2f;

    private static final float VERTEX_JITTER = 0.15f;

    private static final int MAX_FORK_SEGMENTS = 3;

    private static final float FORK_CHANCE = 0.3f;

    private static final int UPDATE_PERIOD = 1;

    public ParticleLightning(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        seed = this.random.nextLong();
        this.setColor(0.2f, 0.6f, 1);
        this.setLifetime(3);
        this.quadSize = 2.4f;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    protected void draw(PoseStack stack, Tesselator tessellator, float length, float tickDelta) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        boolean freeEnd = this.target == null;

        int numberOfSegments = Math.round(length / MAX_SEGMENT_LENGTH);

        for (int layer = 0; layer < 3; layer++) {
            float px = 0, py = 0, pz = 0;

            random.setSeed(this.seed + this.age / UPDATE_PERIOD);

            for (int i = 0; i < numberOfSegments - 1; i++) {
                float px2 = (random.nextFloat() * 2 - 1) * VERTEX_JITTER * quadSize;
                float py2 = (random.nextFloat() * 2 - 1) * VERTEX_JITTER * quadSize;
                float pz2 = pz + length / numberOfSegments;

                drawSegment(stack, tessellator, layer, px, py, pz, px2, py2, pz2, THICKNESS * quadSize);

                if (random.nextFloat() < FORK_CHANCE) {
                    float px3 = px, py3 = py, pz3 = pz;

                    for (int j = 0; j < random.nextInt(MAX_FORK_SEGMENTS - 1) + 1; j++) {
                        float px4 = px3 + (random.nextFloat() * 2 - 1) * VERTEX_JITTER * quadSize;
                        float py4 = py3 + (random.nextFloat() * 2 - 1) * VERTEX_JITTER * quadSize;
                        float pz4 = pz3 + MIN_SEGMENT_LENGTH + random.nextFloat() * (MAX_SEGMENT_LENGTH - MIN_SEGMENT_LENGTH);

                        drawSegment(stack, tessellator, layer, px3, py3, pz3, px4, py4, pz4, THICKNESS * 0.8f * quadSize);

                        if (random.nextFloat() < FORK_CHANCE) {
                            float px5 = px3 + (random.nextFloat() * 2 - 1) * VERTEX_JITTER * quadSize;
                            float py5 = py3 + (random.nextFloat() * 2 - 1) * VERTEX_JITTER * quadSize;
                            float pz5 = pz3 + MIN_SEGMENT_LENGTH + random.nextFloat() * (MAX_SEGMENT_LENGTH - MIN_SEGMENT_LENGTH);

                            drawSegment(stack, tessellator, layer, px3, py3, pz3, px5, py5, pz5, THICKNESS * 0.6f * quadSize);
                        }

                        px3 = px4;
                        py3 = py4;
                        pz3 = pz4;
                    }
                }

                px = px2;
                py = py2;
                pz = pz2;
            }

            float px2 = freeEnd ? (random.nextFloat() * 2 - 1) * VERTEX_JITTER * quadSize : 0;
            float py2 = freeEnd ? (random.nextFloat() * 2 - 1) * VERTEX_JITTER * quadSize : 0;
            drawSegment(stack, tessellator, layer, px, py, pz, px2, py2, length, THICKNESS * quadSize);

        }

        RenderSystem.disableBlend();
    }

    private void drawSegment(PoseStack stack, Tesselator tessellator, int layer, float x1, float y1, float z1, float x2, float y2, float z2, float thickness) {
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        switch (layer) {
            case 0:
                drawShearedBox(stack, buffer, x1, y1, z1, x2, y2, z2, 0.25f * thickness, 1, 1, 1, 1);
                break;

            case 1:
                drawShearedBox(stack, buffer, x1, y1, z1, x2, y2, z2, 0.6f * thickness, (rCol + 1) / 2, (gCol + 1) / 2, (bCol + 1) / 2, 0.65f);
                break;

            case 2:
                drawShearedBox(stack, buffer, x1, y1, z1, x2, y2, z2, thickness, rCol, gCol, bCol, 0.3f);
                break;
        }

        BufferUploader.drawWithShader(buffer.end());
    }

    private void drawShearedBox(PoseStack stack, BufferBuilder buffer, float x1, float y1, float z1, float x2, float y2, float z2, float width, float r, float g, float b, float a) {
        buffer.vertex(stack.last().pose(), x1 - width, y1 - width, z1).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), x2 - width, y2 - width, z2).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), x1 - width, y1 + width, z1).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), x2 - width, y2 + width, z2).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), x1 + width, y1 + width, z1).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), x2 + width, y2 + width, z2).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), x1 + width, y1 - width, z1).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), x2 + width, y2 - width, z2).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), x1 - width, y1 - width, z1).color(r, g, b, a).endVertex();
        buffer.vertex(stack.last().pose(), x2 - width, y2 - width, z2).color(r, g, b, a).endVertex();
    }

    public static class LightningProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public LightningProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleLightning(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleLightning(world, x, y, z, spriteProvider);
        }
    }
}
