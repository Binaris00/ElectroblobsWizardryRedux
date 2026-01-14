package com.binaris.wizardry.client.renderer.entity;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.entity.construct.BubbleConstruct;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class BubbleRenderer extends EntityRenderer<BubbleConstruct> {
    private static final ResourceLocation BUBBLE_TEXTURE = new ResourceLocation("textures/particle/bubble.png");
    private static final ResourceLocation ENTRAPMENT_TEXTURE = WizardryMainMod.location("textures/entity/entrapment.png");
    private static final float BUBBLE_SIZE_MULTIPLIER = 1.1f;

    public BubbleRenderer(Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull BubbleConstruct entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();

        // Billboard towards camera
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        ResourceLocation tex = getTextureLocation(entity);
        VertexConsumer vc = buffers.getBuffer(RenderType.entityTranslucent(tex));

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        float size = calculateBubbleSize(entity);
        float top = size * 2f;

        // Four quad vertices (bottom-left, bottom-right, top-right, top-left)
        addVertex(vc, pose, normal, -size, 0f, 0f, 1f, packedLight);
        addVertex(vc, pose, normal, size, 0f, 1f, 1f, packedLight);
        addVertex(vc, pose, normal, size, top, 1f, 0f, packedLight);
        addVertex(vc, pose, normal, -size, top, 0f, 0f, packedLight);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BubbleConstruct construct) {
        return construct.isDarkOrb() ? ENTRAPMENT_TEXTURE : BUBBLE_TEXTURE;
    }

    private float calculateBubbleSize(BubbleConstruct entity) {
        Entity trapped = EntityUtil.getRider(entity);
        if (trapped != null) {
            float width = trapped.getBbWidth();
            float height = trapped.getBbHeight() / 2F;
            return Math.max(width, height) * BUBBLE_SIZE_MULTIPLIER;
        }
        return 1.0f;
    }

    private void addVertex(VertexConsumer vc, Matrix4f pose, Matrix3f normal, float x, float y, float u, float v, int packedLight) {
        vc.vertex(pose, x, y, 0f)
                .color(255, 255, 255, 200)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0f, 1f, 0f)
                .endVertex();
    }
}