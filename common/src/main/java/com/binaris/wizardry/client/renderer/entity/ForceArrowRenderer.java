package com.binaris.wizardry.client.renderer.entity;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.entity.projectile.ForceArrow;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ForceArrowRenderer extends EntityRenderer<ForceArrow> {
    private static final ResourceLocation arrowTextures = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/force_arrow.png");

    public ForceArrowRenderer(Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(ForceArrow arrow, float p_114486_, float p_114487_, PoseStack p_114488_, MultiBufferSource p_114489_, int p_114490_) {
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, arrowTextures);
        p_114488_.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        p_114488_.mulPose(Axis.YP.rotationDegrees(Mth.lerp(p_114487_, arrow.yRotO, arrow.getYRot()) - 90.0F));
        p_114488_.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(p_114487_, arrow.xRotO, arrow.getXRot())));
        p_114488_.mulPose(Axis.YP.rotationDegrees(180));

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        float pixel = 1.0f / 32.0f;
        float u1 = 0.0f;
        float u2 = pixel * 14;
        float v1 = 0.0f;
        float v2 = pixel * 7;
        float u3 = pixel * 16;
        float u4 = 1.0f;
        float v3 = 0.0f;
        float v4 = pixel * 16;
        float u5 = 0.0f;
        float u6 = pixel * 7;
        float v5 = pixel * 25;
        float v6 = 1.0f;
        float scale = 0.05625F;
        float f11 = 0.0f;
        if (f11 > 0.0F) {
            float f12 = -Mth.sin(f11 * 3.0F) * f11;
            p_114488_.mulPose(Axis.ZP.rotationDegrees(f12));
        }

        scale *= 0.8f;

        p_114488_.mulPose(Axis.XP.rotationDegrees(45.0F));
        p_114488_.scale(scale, scale, scale);
        p_114488_.translate(-4.0F, 0.0F, 0.0F);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(p_114488_.last().pose(), -5, 3.5F, -3.5F).uv(u5, v5).endVertex();
        buffer.vertex(p_114488_.last().pose(), -5, 3.5F, 3.5F).uv(u6, v5).endVertex();
        buffer.vertex(p_114488_.last().pose(), -5, -3.5F, 3.5F).uv(u6, v6).endVertex();
        buffer.vertex(p_114488_.last().pose(), -5, -3.5F, -3.5F).uv(u5, v6).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(p_114488_.last().pose(), -5, -3.5F, -3.5F).uv(u5, v6).endVertex();
        buffer.vertex(p_114488_.last().pose(), -5, -3.5F, 3.5F).uv(u6, v6).endVertex();
        buffer.vertex(p_114488_.last().pose(), -5, 3.5F, 3.5F).uv(u6, v5).endVertex();
        buffer.vertex(p_114488_.last().pose(), -5, 3.5F, -3.5F).uv(u5, v5).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        for (int i = 0; i < 5; i++) {
            RenderSystem.setShaderColor(1, 1, 1, 1 - i * 0.2f);
            float j = i + (arrow.tickCount % 3) / 3;
            float width = (float) (2.0d + (Math.sqrt(j * 2) - 0.6) * 2);

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.vertex(p_114488_.last().pose(), -10 + j * 4, -width, -width).uv(u3, v3).endVertex();
            buffer.vertex(p_114488_.last().pose(), -10 + j * 4, -width, width).uv(u4, v3).endVertex();
            buffer.vertex(p_114488_.last().pose(), -10 + j * 4, width, width).uv(u4, v4).endVertex();
            buffer.vertex(p_114488_.last().pose(), -10 + j * 4, width, -width).uv(u3, v4).endVertex();
            BufferUploader.drawWithShader(buffer.end());

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.vertex(p_114488_.last().pose(), -10 + j * 4, width, -width).uv(u3, v3).endVertex();
            buffer.vertex(p_114488_.last().pose(), -10 + j * 4, width, width).uv(u4, v3).endVertex();
            buffer.vertex(p_114488_.last().pose(), -10 + j * 4, -width, width).uv(u4, v4).endVertex();
            buffer.vertex(p_114488_.last().pose(), -10 + j * 4, -width, -width).uv(u3, v4).endVertex();
            BufferUploader.drawWithShader(buffer.end());
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);

        for (int i = 0; i < 4; ++i) {
            p_114488_.mulPose(Axis.XP.rotationDegrees(90.0F));
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.vertex(-10.0D, -4.0D, 0.0F).uv(u1, v1).endVertex();
            buffer.vertex(10.0D, -4.0D, 0.0F).uv(u2, v1).endVertex();
            buffer.vertex(10.0D, 4.0D, 0.0F).uv(u2, v2).endVertex();
            buffer.vertex(-10.0D, 4.0D, 0.0F).uv(u1, v2).endVertex();
            BufferUploader.drawWithShader(buffer.end());
        }

        RenderSystem.disableBlend();

        p_114488_.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ForceArrow forceArrow) {
        return arrowTextures;
    }
}
