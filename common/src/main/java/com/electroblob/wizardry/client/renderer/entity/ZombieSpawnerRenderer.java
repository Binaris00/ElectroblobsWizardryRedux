package com.electroblob.wizardry.client.renderer.entity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.api.content.util.GeometryUtil;
import com.electroblob.wizardry.content.entity.construct.ZombieSpawnerConstruct;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ZombieSpawnerRenderer extends EntityRenderer<ZombieSpawnerConstruct> {
    private static final ResourceLocation TEXTURE = WizardryMainMod.location("textures/entity/zombie_spawner.png");
    private static final Vec3[] HIDDEN_BOX = GeometryUtil.getVertices(new AABB(-1, 0, -1, 1, 2.5, 1));

    public ZombieSpawnerRenderer(Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ZombieSpawnerConstruct construct) {
        return TEXTURE;
    }

    @Override
    public void render(ZombieSpawnerConstruct entity, float entityYaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        poseStack.pushPose();

        float s = DrawingUtils.smoothScaleFactor(entity.lifetime, entity.tickCount, partialTicks, 10, 10);
        poseStack.scale(s, s, s);

        poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * 2));

        RenderSystem.setShaderTexture(0, TEXTURE);

        Vec3[] vertices = GeometryUtil.getVertices(entity.getBoundingBox().move(entity.position().scale(-1)));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        drawFace(poseStack, buffer, vertices[0], vertices[1], vertices[3], vertices[2]);
        drawFace(poseStack, buffer, vertices[1], vertices[0], vertices[2], vertices[3]);
        BufferUploader.drawWithShader(buffer.end());

        poseStack.popPose();

        RenderSystem.colorMask(false, false, false, false);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        drawFaceColour(poseStack, buffer, HIDDEN_BOX[6], HIDDEN_BOX[7], HIDDEN_BOX[2], HIDDEN_BOX[3]);
        drawFaceColour(poseStack, buffer, HIDDEN_BOX[5], HIDDEN_BOX[6], HIDDEN_BOX[1], HIDDEN_BOX[2]);
        drawFaceColour(poseStack, buffer, HIDDEN_BOX[4], HIDDEN_BOX[5], HIDDEN_BOX[0], HIDDEN_BOX[1]);
        drawFaceColour(poseStack, buffer, HIDDEN_BOX[7], HIDDEN_BOX[4], HIDDEN_BOX[3], HIDDEN_BOX[0]);
        drawFaceColour(poseStack, buffer, HIDDEN_BOX[5], HIDDEN_BOX[4], HIDDEN_BOX[6], HIDDEN_BOX[7]);

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.colorMask(true, true, true, true);
        poseStack.popPose();
    }

    private static void drawFace(PoseStack stack, BufferBuilder buffer, Vec3 topLeft, Vec3 topRight, Vec3 bottomLeft, Vec3 bottomRight) {
        buffer.vertex(stack.last().pose(), (float) topLeft.x, (float) topLeft.y, (float) topLeft.z).uv((float) 0, (float) 0).endVertex();
        buffer.vertex(stack.last().pose(), (float) topRight.x, (float) topRight.y, (float) topRight.z).uv((float) 1, (float) 0).endVertex();
        buffer.vertex(stack.last().pose(), (float) bottomRight.x, (float) bottomRight.y, (float) bottomRight.z).uv((float) 1, (float) 1).endVertex();
        buffer.vertex(stack.last().pose(), (float) bottomLeft.x, (float) bottomLeft.y, (float) bottomLeft.z).uv((float) 0, (float) 1).endVertex();
    }

    private static void drawFaceColour(PoseStack stack, BufferBuilder buffer, Vec3 topLeft, Vec3 topRight, Vec3 bottomLeft, Vec3 bottomRight) {
        buffer.vertex(stack.last().pose(), (float) topLeft.x, (float) topLeft.y, (float) topLeft.z).color((float) 0, (float) 0, (float) 0, (float) 1).endVertex();
        buffer.vertex(stack.last().pose(), (float) topRight.x, (float) topRight.y, (float) topRight.z).color((float) 0, (float) 0, (float) 0, (float) 1).endVertex();
        buffer.vertex(stack.last().pose(), (float) bottomRight.x, (float) bottomRight.y, (float) bottomRight.z).color((float) 0, (float) 0, (float) 0, (float) 1).endVertex();
        buffer.vertex(stack.last().pose(), (float) bottomLeft.x, (float) bottomLeft.y, (float) bottomLeft.z).color((float) 0, (float) 0, (float) 0, (float) 1).endVertex();
    }
}
