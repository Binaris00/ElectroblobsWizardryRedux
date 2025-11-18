package com.electroblob.wizardry.client.renderer.blockentity;

import com.electroblob.wizardry.content.blockentity.ImbuementAltarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.Random;

public class ImbuementAltarRenderer implements BlockEntityRenderer<ImbuementAltarBlockEntity> {

    public ImbuementAltarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ImbuementAltarBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        poseStack.pushPose();

        poseStack.translate(0.5F, 1.4F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));

        float t = (blockEntity.getLevel().getGameTime() + partialTick);
        float bobAmount = 0.05f * Mth.sin(t / 15);
        poseStack.translate(0, bobAmount, 0);

        this.renderItem(blockEntity, t, poseStack, bufferSource, packedLight, packedOverlay);
        this.renderRays(blockEntity, partialTick, poseStack, bufferSource);

        poseStack.popPose();
    }

    private void renderItem(ImbuementAltarBlockEntity blockEntity, float t, PoseStack poseStack,
                            MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ItemStack stack = blockEntity.getStack();

        if (!stack.isEmpty()) {
            poseStack.pushPose();

            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(t));
            poseStack.scale(0.85F, 0.85F, 0.85F);

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight,
                    packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);

            poseStack.popPose();
        }
    }

    private void renderRays(ImbuementAltarBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (blockEntity.getStack().isEmpty()) return;
        if (blockEntity.getImbuementProgress() <= 0) return;
        if (blockEntity.getElement() == null) return; // No element, no rays, normally if the imbuement progress > 0 there will be an element tho

        float t = (blockEntity.getLevel().getGameTime() + partialTick);
        Random random = new Random(blockEntity.getBlockPos().asLong());
        int[] colors = blockEntity.getElement().getColors();

        int r1 = (colors[1] >> 16) & 255;
        int g1 = (colors[1] >> 8) & 255;
        int b1 = colors[1] & 255;

        int r2 = (colors[2] >> 16) & 255;
        int g2 = (colors[2] >> 8) & 255;
        int b2 = colors[2] & 255;

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lightning());

        for (int j = 0; j < 30; j++) {
            int m = random.nextInt(10);
            int n = random.nextInt(10);
            int sliceAngle = 20 + m;
            float scale = 0.5f;

            poseStack.pushPose();

            float rawProgress = blockEntity.getImbuementProgress();
            float progress = Math.min(rawProgress + partialTick / 141, 1);
            float s = 1 - progress;
            s = 1 - s * s;

            poseStack.scale(s, s, s);

            float rotationVariation = Mth.sin((t + j * 10) * 0.01f) * 5;
            poseStack.mulPose(Axis.XP.rotationDegrees(31 * m + rotationVariation));
            poseStack.mulPose(Axis.ZP.rotationDegrees(31 * n + rotationVariation));

            float fade = (Math.min(1, 1.9f - progress) - 0.9f) * 10;
            Matrix4f matrix = poseStack.last().pose();

            double x1 = scale * Mth.sin((t + 40 * j) * ((float) Math.PI / 180));
            double z1 = scale * Mth.cos((t + 40 * j) * ((float) Math.PI / 180));
            double x2 = scale * Mth.sin((t + 40 * j - sliceAngle) * ((float) Math.PI / 180));
            double z2 = scale * Mth.cos((t + 40 * j - sliceAngle) * ((float) Math.PI / 180));

            int alpha1 = (int) (255 * fade);
            int alpha2 = (int) (200 * fade);

            buffer.vertex(matrix, 0, 0, 0).color(r1, g1, b1, alpha1).endVertex();
            buffer.vertex(matrix, 0, 0, 0).color(r1, g1, b1, alpha1).endVertex();
            buffer.vertex(matrix, (float) x1, 0, (float) z1).color(r2, g2, b2, alpha2).endVertex();
            buffer.vertex(matrix, (float) x2, 0, (float) z2).color(r2, g2, b2, alpha2).endVertex();

            poseStack.popPose();
        }
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}