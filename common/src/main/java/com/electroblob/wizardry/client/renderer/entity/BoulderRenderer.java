package com.electroblob.wizardry.client.renderer.entity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.client.model.BoulderModel;
import com.electroblob.wizardry.content.entity.construct.BoulderConstruct;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BoulderRenderer extends EntityRenderer<BoulderConstruct> {
    private static final ResourceLocation TEXTURE = WizardryMainMod.location("textures/entity/boulder.png");
    private final BoulderModel model;

    public BoulderRenderer(Context context) {
        super(context);
        this.model = new BoulderModel(context.bakeLayer(BoulderModel.LAYER_LOCATION));
    }

    @Override
    public void render(BoulderConstruct entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() / 2, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks));
        poseStack.translate(0, -entity.getBbHeight() / 2, 0);

        float s = DrawingUtils.smoothScaleFactor(-1, entity.tickCount, partialTicks, 10, 10);
        s *= entity.getSizeMultiplier();
        poseStack.scale(s, s, s);

        poseStack.translate(0, 0.875, 0);

        this.model.renderToBuffer(poseStack, buffer.getBuffer(RenderType.entityTranslucentCull(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BoulderConstruct construct) {
        return TEXTURE;
    }
}
