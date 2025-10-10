package com.electroblob.wizardry.client.renderer.blockentity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.content.blockentity.ArcaneWorkbenchBlockEntity;
import com.electroblob.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.logging.Level;

// This render checks the player view angle to rotate the wand inside of it accordingly (of course it needs to have one
// in it first) - this is done in a similar way to the enchanting table book, specially the way that the block rotates
// and the way the angle is interpolated is everything from there.

public class ArcaneWorkbenchRender implements BlockEntityRenderer<ArcaneWorkbenchBlockEntity> {
    private static final ResourceLocation RUNE_TEXTURE = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/arcane_workbench_rune.png");
    public int time;

    public ArcaneWorkbenchRender(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public void render(ArcaneWorkbenchBlockEntity entity, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.pushPose();

        // Calculate interpolated angle
        float f1 = entity.rot - entity.oRot;
        while (f1 >= (float) Math.PI) {
            f1 -= ((float) Math.PI * 2F);
        }
        while (f1 < -(float) Math.PI) {
            f1 += ((float) Math.PI * 2F);
        }
        double angle = Math.toDegrees(entity.oRot + f1 * partialTicks);

        this.renderEffect(poseStack, entity, partialTicks);
        this.renderWand(poseStack, entity, angle, partialTicks, packedLight, bufferSource);
        poseStack.popPose();
        poseStack.popPose();
    }


    private void renderEffect(PoseStack poseStack, ArcaneWorkbenchBlockEntity entity, float partialTicks) {
        ItemStack stack = entity.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        if(stack.isEmpty()) return;

        poseStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.timer + partialTicks));
        poseStack.translate(0.0f, 0.65f, 0.0f);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        RenderSystem.setShaderTexture(0, RUNE_TEXTURE);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(poseStack.last().pose(), -0.5f, 0, -0.5f).uv(0, 0).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.5f, 0, -0.5f).uv(1, 0).endVertex();
        buffer.vertex(poseStack.last().pose(), 0.5f, 0, 0.5f).uv(1, 1).endVertex();
        buffer.vertex(poseStack.last().pose(), -0.5f, 0, 0.5f).uv(0, 1).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void renderWand(PoseStack posestack, ArcaneWorkbenchBlockEntity entity, double v, float partialTicks, int light, MultiBufferSource bufferSource) {
        ItemStack stack = entity.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);
        if(stack.isEmpty()) return;

        posestack.pushPose();
        posestack.mulPose(Axis.XP.rotationDegrees(90.0F));

        posestack.mulPose(Axis.YP.rotationDegrees(180));
        posestack.mulPose(Axis.ZP.rotationDegrees((float) (v + 45)));

        posestack.translate(0.0F, 0.0F, 0.56f + 0.05f * Mth.sin((entity.timer + partialTicks) / 15));
        posestack.scale(0.75F, 0.75F, 0.75F);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, posestack, bufferSource, null, 0);
        posestack.popPose();

    }
}
