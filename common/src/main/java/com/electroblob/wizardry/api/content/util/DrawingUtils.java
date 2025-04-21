package com.electroblob.wizardry.api.content.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.Random;

public class DrawingUtils {

    public static float smoothScaleFactor(int lifetime, int ticksExisted, float partialTicks, int startLength, int endLength) {
        float age = ticksExisted + partialTicks;
        float s = Mth.clamp(age < startLength || lifetime < 0 ? age / startLength : (lifetime - age) / endLength, 0, 1);
        s = (float) Math.pow(s, 0.4);
        return s;
    }

    public static int mix(int colour1, int colour2, float proportion) {
        proportion = Mth.clamp(proportion, 0, 1);

        int r1 = colour1 >> 16 & 255;
        int g1 = colour1 >> 8 & 255;
        int b1 = colour1 & 255;
        int r2 = colour2 >> 16 & 255;
        int g2 = colour2 >> 8 & 255;
        int b2 = colour2 & 255;

        int r = (int) (r1 + (r2 - r1) * proportion);
        int g = (int) (g1 + (g2 - g1) * proportion);
        int b = (int) (b1 + (b2 - b1) * proportion);

        return (r << 16) + (g << 8) + b;
    }

    public static void drawGlitchRect(Random random, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, boolean flipX, boolean flipY) {
        for (int i = 0; i < height; i++) {
            if (flipY) i = height - i - 1;
            int offset = random.nextInt(4) == 0 ? random.nextInt(6) - 3 : 0;
            drawTexturedFlippedRect(x + offset, y + i, u, v + i, width, 1, textureWidth, textureHeight, flipX, flipY);
        }
    }

    public static void drawTexturedFlippedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, boolean flipX, boolean flipY) {
        PoseStack stack = new PoseStack();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        float f = 1F / (float) textureWidth;
        float f1 = 1F / (float) textureHeight;

        int u1 = flipX ? u + width : u;
        int u2 = flipX ? u : u + width;
        int v1 = flipY ? v + height : v;
        int v2 = flipY ? v : v + height;

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.vertex(stack.last().pose(), (float) (x), (float) (y + height), 0).uv(((float) (u1) * f), ((float) (v2) * f1)).endVertex();
        buffer.vertex(stack.last().pose(), (float) (x + width), (float) (y + height), 0).uv(((float) (u2) * f), ((float) (v2) * f1)).endVertex();
        buffer.vertex(stack.last().pose(), (float) (x + width), (float) (y), 0).uv(((float) (u2) * f), ((float) (v1) * f1)).endVertex();
        buffer.vertex(stack.last().pose(), (float) (x), (float) (y), 0).uv(((float) (u1) * f), ((float) (v1) * f1)).endVertex();

        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawTexturedRect(int x, int y, int width, int height) {
        drawTexturedRect(x, y, 0, 0, width, height, width, height);
    }

    public static void drawTexturedRect(int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
        DrawingUtils.drawTexturedFlippedRect(x, y, u, v, width, height, textureWidth, textureHeight, false, false);
    }

    public static int makeTranslucent(int colour, float opacity) {
        return colour + ((int) (opacity * 0xff) << 24);
    }

    public static void drawScaledStringToWidth(GuiGraphics guiGraphics, Font font, Component text, float x, float y, float scale, int colour, float width, boolean centre, boolean alignR) {
        float textWidth = font.width(text) * scale;
        float textHeight = font.lineHeight * scale;

        // If the text is wider than the desired width, adjust the scale
        if (textWidth > width) {
            scale *= width / textWidth;
            font.width(text);
        } else if (alignR) {
            x += width - textWidth;
        }

        if (centre) {
            y += (font.lineHeight - textHeight) / 2;
        }

        DrawingUtils.drawScaledTranslucentString(guiGraphics, font, text, x, y, scale, colour);
    }

    public static void drawScaledTranslucentString(GuiGraphics guiGraphics, Font font, Component text, float x, float y, float scale, int colour) {
        PoseStack stack = guiGraphics.pose();
        stack.pushPose();
        RenderSystem.enableBlend();
        stack.scale(scale, scale, scale);

        float adjustedX = x / scale;
        float adjustedY = y / scale;

        guiGraphics.drawString(font, text, (int) adjustedX, (int) adjustedY, colour);

        RenderSystem.disableBlend();
        stack.popPose();
    }
}
