package com.electroblob.wizardry.client.renderer.entity;

import com.electroblob.wizardry.api.common.entity.construct.MagicConstructEntity;
import com.electroblob.wizardry.api.common.util.DrawingUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class SigilRenderer extends EntityRenderer<MagicConstructEntity> {
    private final ResourceLocation texture;
    private float rotationSpeed;
    private boolean invisibleToEnemies;

    public SigilRenderer(Context p_174008_, ResourceLocation texture, float rotationSpeed, boolean invisibleToEnemies) {
        super(p_174008_);
        this.texture = texture;
        this.rotationSpeed = rotationSpeed;
        this.invisibleToEnemies = invisibleToEnemies;
    }


    @Override
    public void render(@NotNull MagicConstructEntity entity, float p_114486_, float partialTicks, @NotNull PoseStack poseStack, MultiBufferSource p_114489_, int p_114490_) {
        if (this.invisibleToEnemies) {
            // TODO !AllyDesignationSystem.isPlayerAlly((Player) entity.getCaster(), Minecraft.getInstance().player)
            if (entity.getCaster() != Minecraft.getInstance().player && entity.getCaster()
                    instanceof Player) {
                return;
            }
        }

        poseStack.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float yOffset = 0;

        poseStack.translate(0, yOffset, 0);

        RenderSystem.setShaderTexture(0, texture);
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.5F;

        poseStack.mulPose(Axis.XP.rotationDegrees(-90));

        if (rotationSpeed != 0) poseStack.mulPose(Axis.ZP.rotationDegrees(entity.tickCount * rotationSpeed));

        float s = entity.getBbWidth() * DrawingUtils.smoothScaleFactor(entity.lifetime, entity.tickCount, partialTicks, 10, 10);
        poseStack.scale(s, s, s);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 0.0F - f8, 0.01f).uv(0, 1).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 0.0F - f8, 0.01f).uv(1, 1).endVertex();
        buffer.vertex(poseStack.last().pose(), f6 - f7, 1.0F - f8, 0.01f).uv(1, 0).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.0F - f7, 1.0F - f8, 0.01f).uv(0, 0).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicConstructEntity entity) {
        return null;
    }
}
