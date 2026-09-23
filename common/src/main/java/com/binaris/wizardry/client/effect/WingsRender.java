package com.binaris.wizardry.client.effect;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.setup.registries.Spells;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX;

public final class WingsRender {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/entity/wing.png");

    // No first person in here because you can never see the wings on your back!

    private WingsRender() {
    }

    public static void render(Camera camera, PoseStack poseStack, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;

        boolean firstPerson = minecraft.options.getCameraType().isFirstPerson();
        Vec3 cameraPos = camera.getPosition();

        for (Player player : minecraft.level.players()) {
            if (!EntityUtil.isCasting(player, Spells.FLIGHT)) continue;
            if (player.isInvisible()) continue;
            if (firstPerson && player == minecraft.player) continue;

            poseStack.pushPose();
            poseStack.translate(
                    Mth.lerp(partialTicks, player.xOld, player.getX()) - cameraPos.x,
                    Mth.lerp(partialTicks, player.yOld, player.getY()) - cameraPos.y,
                    Mth.lerp(partialTicks, player.zOld, player.getZ()) - cameraPos.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot)));

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            float flap = 20 * Mth.sin((player.tickCount + partialTicks) * 0.3f);

            drawWing(poseStack, 0.1, 20 + flap);
            drawWing(poseStack, -0.1, -200 - flap);

            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            poseStack.popPose();
        }
    }

    private static void drawWing(PoseStack poseStack, double x, float flapDegrees) {
        poseStack.pushPose();
        poseStack.translate(x, 0.4, -0.15);
        poseStack.mulPose(Axis.YP.rotationDegrees(flapDegrees));

        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, POSITION_TEX);

        buffer.vertex(matrix, 0, 2, 0).uv(0, 0).endVertex();
        buffer.vertex(matrix, 2, 2, 0).uv(1, 0).endVertex();
        buffer.vertex(matrix, 2, 0, 0).uv(1, 1).endVertex();
        buffer.vertex(matrix, 0, 0, 0).uv(0, 1).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        buffer.begin(VertexFormat.Mode.QUADS, POSITION_TEX);

        buffer.vertex(matrix, 0, 2, 0).uv(0, 0).endVertex();
        buffer.vertex(matrix, 0, 0, 0).uv(0, 1).endVertex();
        buffer.vertex(matrix, 2, 0, 0).uv(1, 1).endVertex();
        buffer.vertex(matrix, 2, 2, 0).uv(1, 0).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        poseStack.popPose();
    }
}
