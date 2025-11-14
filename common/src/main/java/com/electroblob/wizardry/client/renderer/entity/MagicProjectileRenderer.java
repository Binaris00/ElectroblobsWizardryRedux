package com.electroblob.wizardry.client.renderer.entity;

import com.electroblob.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MagicProjectileRenderer<T extends MagicProjectileEntity> extends EntityRenderer<T> {
    private final float scale = 0.7f;
    private final boolean blend = false;
    private final ResourceLocation texture;

    public MagicProjectileRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context);
        this.texture = texture;
    }

    @Override
    public void render(T entity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        poseStack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderTexture(0, this.getTextureLocation(entity));
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (blend) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        }

        float f2 = this.scale;
        poseStack.scale(f2, f2, f2);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        float f3 = 0.0f;
        float f4 = 1.0f;
        float f5 = 0.0f;
        float f6 = 1.0f;
        float f7 = 1.0F;
        float f8 = 0.5F;
        float f9 = 0.25F;


        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(poseStack.last().pose(), (0.0F - f8), (0.0F - f9), 0.0F).uv(f3, f6).endVertex();
        buffer.vertex(poseStack.last().pose(), (f7 - f8), (0.0F - f9), 0.0F).uv(f4, f6).endVertex();
        buffer.vertex(poseStack.last().pose(), (f7 - f8), (1.0F - f9), 0.0F).uv(f4, f5).endVertex();
        buffer.vertex(poseStack.last().pose(), (0.0F - f8), (1.0F - f9), 0.0F).uv(f3, f5).endVertex();
        tessellator.end();

        if (blend) {
            RenderSystem.disableBlend();
        }
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}
