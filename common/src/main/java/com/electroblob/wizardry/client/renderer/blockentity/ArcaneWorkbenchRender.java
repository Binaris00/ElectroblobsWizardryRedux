package com.electroblob.wizardry.client.renderer.blockentity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.content.blockentity.ArcaneWorkbenchBlockEntity;
import com.electroblob.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ArcaneWorkbenchRender implements BlockEntityRenderer<ArcaneWorkbenchBlockEntity> {
    private static final ResourceLocation RUNE_TEXTURE = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/arcane_workbench_rune.png");

    public ArcaneWorkbenchRender(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public void render(@NotNull ArcaneWorkbenchBlockEntity blockEntity, float partialTick, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.pushPose();
        double angle;
        angle = Math.toDegrees(Math.atan(1.0));
//        if(x < -0.5)
//        {
//            angle = Math.toDegrees(Math.atan((0.5) / (0.5))) + 180;
//        }
//        else
//        {
//            angle = Math.toDegrees(Math.atan((0.5) / (0.5)));
//        }
        this.renderEffect(poseStack, blockEntity, partialTick);
        this.renderWand(poseStack, blockEntity, angle, partialTick, packedLight, buffer);
        poseStack.popPose();
        poseStack.popPose();
    }

    private void renderEffect(PoseStack stack, ArcaneWorkbenchBlockEntity tileentity, float partialTicks) {
        ItemStack itemstack = tileentity.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);

        //EBLogger.warn(itemstack.toString());

        if (!itemstack.isEmpty()) {
            stack.pushPose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            stack.mulPose(Axis.YP.rotationDegrees(tileentity.timer + partialTicks));
            stack.translate(0.0f, 0.65f, 0.0f);
            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();
            RenderSystem.setShaderTexture(0, RUNE_TEXTURE);

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.vertex(stack.last().pose(), -0.5f, 0, -0.5f).uv(0, 0).endVertex();
            buffer.vertex(stack.last().pose(), 0.5f, 0, -0.5f).uv(1, 0).endVertex();
            buffer.vertex(stack.last().pose(), 0.5f, 0, 0.5f).uv(1, 1).endVertex();
            buffer.vertex(stack.last().pose(), -0.5f, 0, 0.5f).uv(0, 1).endVertex();
            BufferUploader.drawWithShader(buffer.end());

            RenderSystem.disableBlend();
            stack.popPose();
        }
    }

    private void renderWand(PoseStack posestack, ArcaneWorkbenchBlockEntity tileentity, double viewAngle, float partialTicks, int light, MultiBufferSource multibuffer) {
        ItemStack stack = tileentity.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);

        //EBLogger.warn(stack.toString());

        if (!stack.isEmpty()) {
            posestack.pushPose();
            posestack.mulPose(Axis.XP.rotationDegrees(90.0F));

            posestack.mulPose(Axis.YP.rotationDegrees(180));
            posestack.mulPose(Axis.ZP.rotationDegrees((float) (viewAngle - 90f)));

            posestack.translate(0.0F, 0.0F, 0.56f + 0.05f * Mth.sin((tileentity.timer + partialTicks) / 15));
            posestack.scale(0.75F, 0.75F, 0.75F);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, posestack, multibuffer, null,0);
            posestack.popPose();
        }
    }
}
