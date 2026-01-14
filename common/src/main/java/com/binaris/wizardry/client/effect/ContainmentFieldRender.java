package com.binaris.wizardry.client.effect;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.data.ContainmentData;
import com.binaris.wizardry.api.content.util.GeometryUtil;
import com.binaris.wizardry.content.effect.ContainmentEffect;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR;

public final class ContainmentFieldRender {
    private static final ResourceLocation[] TEXTURES = new ResourceLocation[8];
    private static final float ANIMATION_SPEED = 0.004f;
    private static final float FADE_DISTANCE_SQUARED = 15;

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/environment/containment_field_" + i + ".png");
        }
    }

    private ContainmentFieldRender() {
    }


    public static void render(Camera camera, PoseStack poseStack, float partialTicks) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isAlive()) return;

        MobEffectInstance effect = player.getEffect(EBMobEffects.CONTAINMENT.get());
        if (effect == null) return;

        ContainmentData data = Services.OBJECT_DATA.getContainmentData(player);
        if (data.getContainmentPos() == null) return;

        Vec3 centre = GeometryUtil.getCentre(data.getContainmentPos());
        float r = ContainmentEffect.getContainmentDistance(effect.getAmplifier());

        Vec3 cameraPos = camera.getPosition();
        poseStack.pushPose();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        int textureIndex = (player.tickCount % (TEXTURES.length * 2)) / 2;
        RenderSystem.setShaderTexture(0, TEXTURES[textureIndex]);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        // Shader
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float distance = (player.tickCount + partialTicks) * ANIMATION_SPEED;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        Vec3 playerEyes = player.getEyePosition(partialTicks);
        Vec3 relative = centre.subtract(playerEyes);

        relative = relative.subtract(cameraPos.subtract(playerEyes));

        double x1 = relative.x - r;
        double y1 = relative.y - r;
        double z1 = relative.z - r;
        double x2 = relative.x + r;
        double y2 = relative.y + r;
        double z2 = relative.z + r;

        float alpha = Math.min(1, effect.getDuration() / 40f);

        buffer.begin(VertexFormat.Mode.QUADS, POSITION_TEX_COLOR);

        Matrix4f matrix = poseStack.last().pose();

        // Bottom face (Y = y1)
        drawVertex(buffer, matrix, x1, y1, z1, 0, 0, distance, alpha);
        drawVertex(buffer, matrix, x1, y1, 0, 0, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, 0, y1, 0, -(float) x1, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, 0, y1, z1, -(float) x1, 0, distance, alpha);

        drawVertex(buffer, matrix, 0, y1, 0, -(float) x1, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, x2, y1, 0, 2 * r, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, x2, y1, z1, 2 * r, 0, distance, alpha);
        drawVertex(buffer, matrix, 0, y1, z1, -(float) x1, 0, distance, alpha);

        drawVertex(buffer, matrix, x1, y1, z2, 0, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, 0, y1, z2, -(float) x1, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, 0, y1, 0, -(float) x1, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, x1, y1, 0, 0, -(float) z1, distance, alpha);

        drawVertex(buffer, matrix, 0, y1, 0, -(float) x1, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, 0, y1, z2, -(float) x1, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x2, y1, z2, 2 * r, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x2, y1, 0, 2 * r, -(float) z1, distance, alpha);

        // Top face (Y = y2)
        drawVertex(buffer, matrix, x1, y2, z1, 0, 0, distance, alpha);
        drawVertex(buffer, matrix, 0, y2, z1, -(float) x1, 0, distance, alpha);
        drawVertex(buffer, matrix, 0, y2, 0, -(float) x1, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, x1, y2, 0, 0, -(float) z1, distance, alpha);

        drawVertex(buffer, matrix, x2, y2, z1, 2 * r, 0, distance, alpha);
        drawVertex(buffer, matrix, x2, y2, 0, 2 * r, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, 0, y2, 0, -(float) x1, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, 0, y2, z1, -(float) x1, 0, distance, alpha);

        drawVertex(buffer, matrix, 0, y2, 0, -(float) x1, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, 0, y2, z2, -(float) x1, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x1, y2, z2, 0, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x1, y2, 0, 0, -(float) z1, distance, alpha);

        drawVertex(buffer, matrix, 0, y2, 0, -(float) x1, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, x2, y2, 0, 2 * r, -(float) z1, distance, alpha);
        drawVertex(buffer, matrix, x2, y2, z2, 2 * r, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, 0, y2, z2, -(float) x1, 2 * r, distance, alpha);

        // North face (Z = z1)
        drawVertex(buffer, matrix, x1, y1, z1, 0, 0, distance, alpha);
        drawVertex(buffer, matrix, 0, y1, z1, -(float) x1, 0, distance, alpha);
        drawVertex(buffer, matrix, 0, 0, z1, -(float) x1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x1, 0, z1, 0, -(float) y1, distance, alpha);

        drawVertex(buffer, matrix, x2, y1, z1, 2 * r, 0, distance, alpha);
        drawVertex(buffer, matrix, x2, 0, z1, 2 * r, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, 0, 0, z1, -(float) x1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, 0, y1, z1, -(float) x1, 0, distance, alpha);

        drawVertex(buffer, matrix, 0, 0, z1, -(float) x1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, 0, y2, z1, -(float) x1, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x1, y2, z1, 0, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x1, 0, z1, 0, -(float) y1, distance, alpha);

        drawVertex(buffer, matrix, 0, 0, z1, -(float) x1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x2, 0, z1, 2 * r, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x2, y2, z1, 2 * r, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, 0, y2, z1, -(float) x1, 2 * r, distance, alpha);

        // South face (Z = z2)
        drawVertex(buffer, matrix, x1, y1, z2, 0, 0, distance, alpha);
        drawVertex(buffer, matrix, x1, 0, z2, 0, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, 0, 0, z2, -(float) x1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, 0, y1, z2, -(float) x1, 0, distance, alpha);

        drawVertex(buffer, matrix, 0, 0, z2, -(float) x1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x2, 0, z2, 2 * r, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x2, y1, z2, 2 * r, 0, distance, alpha);
        drawVertex(buffer, matrix, 0, y1, z2, -(float) x1, 0, distance, alpha);

        drawVertex(buffer, matrix, x1, y2, z2, 0, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, 0, y2, z2, -(float) x1, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, 0, 0, z2, -(float) x1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x1, 0, z2, 0, -(float) y1, distance, alpha);

        drawVertex(buffer, matrix, 0, 0, z2, -(float) x1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, 0, y2, z2, -(float) x1, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x2, y2, z2, 2 * r, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x2, 0, z2, 2 * r, -(float) y1, distance, alpha);

        // West face (X = x1)
        drawVertex(buffer, matrix, x1, y1, z1, 0, 0, distance, alpha);
        drawVertex(buffer, matrix, x1, 0, z1, 0, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x1, 0, 0, -(float) z1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x1, y1, 0, -(float) z1, 0, distance, alpha);

        drawVertex(buffer, matrix, x1, 0, 0, -(float) z1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x1, 0, z2, 2 * r, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x1, y1, z2, 2 * r, 0, distance, alpha);
        drawVertex(buffer, matrix, x1, y1, 0, -(float) z1, 0, distance, alpha);

        drawVertex(buffer, matrix, x1, y2, z1, 0, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x1, y2, 0, -(float) z1, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x1, 0, 0, -(float) z1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x1, 0, z1, 0, -(float) y1, distance, alpha);

        drawVertex(buffer, matrix, x1, 0, 0, -(float) z1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x1, y2, 0, -(float) z1, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x1, y2, z2, 2 * r, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x1, 0, z2, 2 * r, -(float) y1, distance, alpha);

        // East face (X = x2)
        drawVertex(buffer, matrix, x2, y1, z1, 0, 0, distance, alpha);
        drawVertex(buffer, matrix, x2, y1, 0, -(float) z1, 0, distance, alpha);
        drawVertex(buffer, matrix, x2, 0, 0, -(float) z1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x2, 0, z1, 0, -(float) y1, distance, alpha);

        drawVertex(buffer, matrix, x2, y1, z2, 2 * r, 0, distance, alpha);
        drawVertex(buffer, matrix, x2, 0, z2, 2 * r, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x2, 0, 0, -(float) z1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x2, y1, 0, -(float) z1, 0, distance, alpha);

        drawVertex(buffer, matrix, x2, 0, 0, -(float) z1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x2, y2, 0, -(float) z1, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x2, y2, z1, 0, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x2, 0, z1, 0, -(float) y1, distance, alpha);

        drawVertex(buffer, matrix, x2, 0, 0, -(float) z1, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x2, 0, z2, 2 * r, -(float) y1, distance, alpha);
        drawVertex(buffer, matrix, x2, y2, z2, 2 * r, 2 * r, distance, alpha);
        drawVertex(buffer, matrix, x2, y2, 0, -(float) z1, 2 * r, distance, alpha);

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        poseStack.popPose();
    }

    private static void drawVertex(BufferBuilder buffer, Matrix4f matrix, double x, double y, double z, float u, float v, float offset, float alpha) {
        float distSq = (float) (x * x + y * y + z * z);
        float fade = Mth.clamp(1 - distSq / FADE_DISTANCE_SQUARED, 0, 1);

        buffer.vertex(matrix, (float) x, (float) y, (float) z)
                .uv(u + offset, v + offset)
                .color(1f, 1f, 1f, fade * alpha)
                .endVertex();
    }
}
