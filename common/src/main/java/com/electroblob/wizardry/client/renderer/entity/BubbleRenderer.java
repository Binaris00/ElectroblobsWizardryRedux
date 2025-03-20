package com.electroblob.wizardry.client.renderer.entity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.common.util.DrawingUtils;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.entity.construct.BubbleConstruct;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class BubbleRenderer extends EntityRenderer<BubbleConstruct> {
    private static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("textures/atlas/particles.png");
    private static final ResourceLocation ENTRAPMENT_TEXTURE = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/entrapment.png");

    public BubbleRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull BubbleConstruct entity, float p_114486_, float partialTicks, PoseStack p_114488_, @NotNull MultiBufferSource p_114489_, int p_114490_) {
        p_114488_.pushPose();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float yOffset = 0;

        Entity rider = EntityUtil.getRider(entity);

        if (rider != null) {
            yOffset = rider.getBbHeight() / 2;
        }

        p_114488_.translate(0, yOffset, 0);

        RenderSystem.setShaderTexture(0, entity.isDarkOrb ? ENTRAPMENT_TEXTURE : PARTICLE_TEXTURES);
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.5F;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        p_114488_.mulPose(this.entityRenderDispatcher.cameraOrientation());
        p_114488_.mulPose(Axis.YP.rotationDegrees(180.0F));

        float s = 3 * DrawingUtils.smoothScaleFactor(entity.isDarkOrb ? entity.lifetime : -1, entity.tickCount, partialTicks, 10, 10);
        p_114488_.scale(s, s, s);

        float pixelwidth = (1.0F / 128);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        if (entity.isDarkOrb) {
            buffer.vertex(p_114488_.last().pose(), 0.0F - f7, 0.0F - f8, 0.0F).uv(0, 1).endVertex();
            buffer.vertex(p_114488_.last().pose(), f6 - f7, 0.0F - f8, 0.0F).uv(1, 1).endVertex();
            buffer.vertex(p_114488_.last().pose(), f6 - f7, 1.0F - f8, 0.0F).uv(1, 0).endVertex();
            buffer.vertex(p_114488_.last().pose(), 0.0F - f7, 1.0F - f8, 0.0F).uv(0, 0).endVertex();
        } else {
            buffer.vertex(p_114488_.last().pose(), 0.0F - f7, 0.0F - f8, 0.0F).uv(pixelwidth, pixelwidth * 24).endVertex();
            buffer.vertex(p_114488_.last().pose(), f6 - f7, 0.0F - f8, 0.0F).uv(pixelwidth * 8, pixelwidth * 24).endVertex();
            buffer.vertex(p_114488_.last().pose(), f6 - f7, 1.0F - f8, 0.0F).uv(pixelwidth * 8, pixelwidth * 17).endVertex();
            buffer.vertex(p_114488_.last().pose(), 0.0F - f7, 1.0F - f8, 0.0F).uv(pixelwidth, pixelwidth * 17).endVertex();
        }

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
        p_114488_.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BubbleConstruct p_114482_) {
        return ENTRAPMENT_TEXTURE;
    }
}
