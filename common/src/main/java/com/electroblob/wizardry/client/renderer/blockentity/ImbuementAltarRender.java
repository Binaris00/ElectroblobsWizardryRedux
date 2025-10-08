package com.electroblob.wizardry.client.renderer.blockentity;

import com.electroblob.wizardry.content.blockentity.ImbuementAltarBlockEntity;
import com.electroblob.wizardry.content.item.ReceptacleItemValue;
import com.electroblob.wizardry.setup.registries.Elements;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ImbuementAltarRender implements BlockEntityRenderer<ImbuementAltarBlockEntity> {
    public ImbuementAltarRender(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public void render(@NotNull ImbuementAltarBlockEntity entity, float partialTick, @NotNull PoseStack stack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        stack.pushPose();
        stack.translate(0.5F, 1.4F, 0.5F);
        stack.mulPose(Axis.ZP.rotationDegrees(180));

        float t = Minecraft.getInstance().player.tickCount + partialTick;
        stack.translate(0, 0.05f * Mth.sin(t / 15), 0);

        this.renderItem(stack, entity, t, buffer, packedLight);

        stack.popPose();

        // Renderizar rayos en un contexto separado
        this.renderRays(stack, entity, partialTick);
    }

    private void renderItem(PoseStack posestack, ImbuementAltarBlockEntity entity, float t, MultiBufferSource source, int light) {
        ItemStack stack = entity.getStack();
        if(stack.isEmpty()) return;
        posestack.pushPose();

        posestack.mulPose(Axis.XP.rotationDegrees(180));
        posestack.mulPose(Axis.YP.rotationDegrees(t));
        posestack.scale(0.85F, 0.85F, 0.85F);

        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, posestack, source, entity.getLevel(),0);
        posestack.popPose();
    }

    private void renderRays(PoseStack poseStack, ImbuementAltarBlockEntity entity, float partialTicks) {
        float progress = entity.getImbuementProgress();

        // Solo renderizar rayos si hay progreso
        if (progress <= 0) return;

        float t = Minecraft.getInstance().player.tickCount + partialTicks;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.0, 0.5); // Centrar en el bloque

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        Random random = new Random(entity.getBlockPos().asLong());

        // TODO IMBUEMENT ALTAR COLORS
        int[] colors = Elements.FIRE.getColors();

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int r1 = colors[1] >> 16 & 255;
        int g1 = colors[1] >> 8 & 255;
        int b1 = colors[1] & 255;

        int r2 = colors[2] >> 16 & 255;
        int g2 = colors[2] >> 8 & 255;
        int b2 = colors[2] & 255;

        for (int j = 0; j < 30; j++) {

            int m = random.nextInt(10);
            int n = random.nextInt(10);

            int sliceAngle = 20 + m;
            float scale = 0.5f;

            poseStack.pushPose();

            // Calcular la escala basada en el progreso (crece de 0 a 1)
            float s = 1 - progress;
            s = 1 - s * s;
            poseStack.scale(s, s, s);

            poseStack.mulPose(Axis.XP.rotationDegrees(31 * m));
            poseStack.mulPose(Axis.ZP.rotationDegrees(31 * n));

            buffer.begin(VertexFormat.Mode.LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

            // Calcular el fade: empieza en 0, crece a 1, luego baja a 0 al final
            float fade = Math.max(0, Math.min(1, (Math.min(1, 1.9f - progress) - 0.9f) * 10));

            buffer.vertex(poseStack.last().pose(), 0, 0, 0).color(r1, g1, b1, (int) (255 * fade)).endVertex();
            buffer.vertex(poseStack.last().pose(), 0, 0, 0).color(r1, g1, b1, (int) (255 * fade)).endVertex();

            double x1 = scale * Mth.sin((t + 40 * j) * ((float) Math.PI / 180));
            double z1 = scale * Mth.cos((t + 40 * j) * ((float) Math.PI / 180));

            double x2 = scale * Mth.sin((t + 40 * j - sliceAngle) * ((float) Math.PI / 180));
            double z2 = scale * Mth.cos((t + 40 * j - sliceAngle) * ((float) Math.PI / 180));

            buffer.vertex(poseStack.last().pose(), (float)x1, 0, (float)z1).color(r2, g2, b2, 0).endVertex();
            buffer.vertex(poseStack.last().pose(), (float)x2, 0, (float)z2).color(r2, g2, b2, 0).endVertex();

            BufferUploader.drawWithShader(buffer.end());

            poseStack.popPose();
        }

        poseStack.popPose();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }
}
