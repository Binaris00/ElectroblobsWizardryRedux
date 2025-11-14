package com.electroblob.wizardry.client.particle;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.particle.ParticleWizardry;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParticleBuff extends ParticleWizardry {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/particle/buff.png");
    private final boolean mirror;


    public ParticleBuff(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z, spriteProvider, false);
        this.setParticleSpeed(0, 0.162, 0);
        this.mirror = random.nextBoolean();
        this.setLifetime(15);
        this.setGravity(false);
        this.hasPhysics = false;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        PoseStack stack = RenderSystem.getModelViewStack();
        updateEntityLinking(camera.getEntity(), tickDelta);

        stack.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);


        stack.translate((this.age + tickDelta) / (float) this.lifetime * -2, 0, 0);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);

        buffer.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);

        float x = (float) (this.xo + (this.x - this.xo) * (double) tickDelta);
        float y = (float) (this.yo + (this.y - this.zo) * (double) tickDelta);
        float z = (float) (this.zo + (this.z - this.yo) * (double) tickDelta);

        float f = 0.875f - 0.125f * Mth.floor((float) this.age / (float) this.lifetime * 8 - 0.000001f);
        float g = f + 0.125f;
        float hrepeat = 1;
        float scale = 0.6f;
        float yScale = 0.7f * scale;
        float dx = mirror ? -scale : scale;

        buffer.vertex(stack.last().pose(), x - dx, y - yScale, z - scale).uv(0, g).color(rCol, gCol, bCol, alpha).endVertex();
        buffer.vertex(stack.last().pose(), x - dx, y + yScale, z - scale).uv(0, f).color(rCol, gCol, bCol, alpha).endVertex();
        buffer.vertex(stack.last().pose(), x + dx, y - yScale, z - scale).uv(0.25F * hrepeat, g).color(rCol, gCol, bCol, alpha).endVertex();
        buffer.vertex(stack.last().pose(), x + dx, y + yScale, z - scale).uv(0.25F * hrepeat, f).color(rCol, gCol, bCol, alpha).endVertex();
        buffer.vertex(stack.last().pose(), x + dx, y - yScale, z + scale).uv(0.5F * hrepeat, g).color(rCol, gCol, bCol, alpha).endVertex();
        buffer.vertex(stack.last().pose(), x + dx, y + yScale, z + scale).uv(0.5F * hrepeat, f).color(rCol, gCol, bCol, alpha).endVertex();
        buffer.vertex(stack.last().pose(), x - dx, y - yScale, z + scale).uv(0.75F * hrepeat, g).color(rCol, gCol, bCol, alpha).endVertex();
        buffer.vertex(stack.last().pose(), x - dx, y + yScale, z + scale).uv(0.75F * hrepeat, f).color(rCol, gCol, bCol, alpha).endVertex();
        buffer.vertex(stack.last().pose(), x - dx, y - yScale, z - scale).uv(hrepeat, g).color(rCol, gCol, bCol, alpha).endVertex();
        buffer.vertex(stack.last().pose(), x - dx, y + yScale, z - scale).uv(hrepeat, f).color(rCol, gCol, bCol, alpha).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.enableCull();

        stack.popPose();
    }

    public static class BuffProvider implements ParticleProvider<SimpleParticleType> {
        static SpriteSet spriteProvider;

        public BuffProvider(SpriteSet sprite) {
            spriteProvider = sprite;
        }

        public static ParticleWizardry createParticle(ClientLevel clientWorld, Vec3 vec3d) {
            return new ParticleBuff(clientWorld, vec3d.x, vec3d.y, vec3d.z, spriteProvider);
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ParticleBuff(world, x, y, z, spriteProvider);
        }
    }
}
