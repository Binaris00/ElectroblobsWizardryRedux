package com.electroblob.wizardry.client.renderer.entity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.common.util.DrawingUtils;
import com.electroblob.wizardry.common.content.entity.construct.DecayConstruct;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DecayRenderer extends EntityRenderer<DecayConstruct> {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[10];

    public DecayRenderer(Context context) {
        super(context);
        for (int i = 0; i < 10; i++) {
            TEXTURES[i] = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/decay/decay_" + i + ".png");
        }
    }

    @Override
    public void render(DecayConstruct entity, float p_114486_, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int p_114490_) {
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURES[entity.textureIndex]);

        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.5F;

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));

        float s = 2 * DrawingUtils.smoothScaleFactor(entity.lifetime, entity.tickCount, partialTicks, 10, 50);
        poseStack.scale(s, s, s);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 0.0F - f8, 0.01F).uv(0, 1).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 0.0F - f8, 0.01F).uv(1, 1).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 1.0F - f8, 0.01F).uv(1, 0).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 1.0F - f8, 0.01F).uv(0, 0).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DecayConstruct entityDecay) {
        return null;
    }
}
