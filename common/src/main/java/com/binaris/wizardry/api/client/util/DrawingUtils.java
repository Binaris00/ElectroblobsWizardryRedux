package com.binaris.wizardry.api.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class DrawingUtils {
    /**
     * The integer colour for black passed into the font renderer methods. This used to be 0 but that's now white for
     * some reason, so I've made a it a constant in case it changes again.
     */
    // I think this is actually ever-so-slightly lighter than pure black, but the difference is unnoticeable.
    public static final int BLACK = 1;

    /**
     * Shorthand for {@link DrawingUtils#drawTexturedRect(GuiGraphics, ResourceLocation, int, int, int, int, int, int, int, int)} which draws the
     * entire texture (u and v are set to 0 and textureWidth and textureHeight are the same as width and height).
     */
    public static void drawTexturedRect(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int width, int height){
        drawTexturedRect(guiGraphics, texture, x, y, 0, 0, width, height, width, height);
    }

    /**
     * Draws a textured rectangle, taking the size of the image and the bit needed into
     * account, unlike {@link GuiGraphics#blit(ResourceLocation, int, int, int, int, int, int)
     * GuiGraphics.blit(ResourceLocation, int, int, int, int, int, int)}, which is harcoded for only 256x256 textures. Also handy
     * for custom potion icons.
     *
     * @param x The x position of the rectangle
     * @param y The y position of the rectangle
     * @param u The x position of the top left corner of the section of the image wanted
     * @param v The y position of the top left corner of the section of the image wanted
     * @param width The width of the section
     * @param height The height of the section
     * @param textureWidth The width of the actual image.
     * @param textureHeight The height of the actual image.
     */
    public static void drawTexturedRect(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight){
        DrawingUtils.drawTexturedFlippedRect(guiGraphics, texture, x, y, u, v, width, height, textureWidth, textureHeight, false, false);
    }

    /**
     * Draws a textured rectangle, taking the size of the image and the bit needed into
     * account, unlike {@link GuiGraphics#blitNineSliced(ResourceLocation, int, int, int, int, int, int, int, int, int)
     * GuiGraphics.blitNineSliced(ResourceLocation, int, int, int, int, int, int)}, which is harcoded for only 256x256 textures. Also handy
     * for custom potion icons. This version allows the texture to additionally be flipped in x and/or y.
     *
     * @param x The x position of the rectangle
     * @param y The y position of the rectangle
     * @param u The x position of the top left corner of the section of the image wanted
     * @param v The y position of the top left corner of the section of the image wanted
     * @param width The width of the section
     * @param height The height of the section
     * @param textureWidth The width of the actual image.
     * @param textureHeight The height of the actual image.
     * @param flipX Whether to flip the texture in the x direction.
     * @param flipY Whether to flip the texture in the y direction.
     */
    public static void drawTexturedFlippedRect(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, boolean flipX, boolean flipY) {
        float f = 1F / (float) textureWidth;
        float f1 = 1F / (float) textureHeight;

        int u1 = flipX ? u + width : u;
        int u2 = flipX ? u : u + width;
        int v1 = flipY ? v + height : v;
        int v2 = flipY ? v : v + height;

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix4f, (float) (x), 		 (float) (y + height), 0).uv(((float)(u1) * f), ((float)(v2) * f1)).endVertex();
        buffer.vertex(matrix4f, (float) (x + width), (float) (y + height), 0).uv(((float)(u2) * f), ((float)(v2) * f1)).endVertex();
        buffer.vertex(matrix4f, (float) (x + width), (float) (y), 		   0).uv(((float)(u2) * f), ((float)(v1) * f1)).endVertex();
        buffer.vertex(matrix4f, (float) (x), 		 (float) (y), 		   0).uv(((float)(u1) * f), ((float)(v1) * f1)).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    /**
     * Draws a textured rectangle, stretching the section of the image to fit the size given.
     *
     * @param x The x position of the rectangle
     * @param y The y position of the rectangle
     * @param u The x position of the top left corner of the section of the image wanted, expressed as a fraction of the
     *        image width
     * @param v The y position of the top left corner of the section of the image wanted, expressed as a fraction of the
     *        image width
     * @param finalWidth The width as rendered
     * @param finalHeight The height as rendered
     * @param width The width of the section, expressed as a fraction of the image width
     * @param height The height of the section, expressed as a fraction of the image width
     */
    public static void drawTexturedStretchedRect(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int finalWidth, int finalHeight, int width, int height){

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix4f, (x),              (y + finalHeight), 0).uv((u),         (v + height)).endVertex();
        buffer.vertex(matrix4f, (x + finalWidth), (y + finalHeight), 0).uv((u + width), (v + height)).endVertex();
        buffer.vertex(matrix4f, (x + finalWidth), (y),               0).uv((u + width), (v))         .endVertex();
        buffer.vertex(matrix4f, (x),              (y),               0).uv((u),         (v))         .endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    /**
     * Draws a 'glitch' rectangle, with some rows of pixels shifted randomly to give a broken effect.
     *
     * @param random A random number generator to use
     * @param x The x position of the rectangle
     * @param y The y position of the rectangle
     * @param u The x position of the top left corner of the section of the image wanted
     * @param v The y position of the top left corner of the section of the image wanted
     * @param width The width of the section
     * @param height The height of the section
     * @param textureWidth The width of the actual image.
     * @param textureHeight The height of the actual image.
     * @param flipX Whether to flip the texture in the x direction.
     * @param flipY Whether to flip the texture in the y direction.
     */
    public static void drawGlitchRect(GuiGraphics guiGraphics, ResourceLocation texture, Random random, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight, boolean flipX, boolean flipY){
        for(int i=0; i<height; i++){
            if(flipY) i = height - i - 1;
            int offset = random.nextInt(4) == 0 ? random.nextInt(6) - 3 : 0;
            drawTexturedFlippedRect(guiGraphics, texture, x + offset, y + i, u, v + i, width, 1, textureWidth, textureHeight, flipX, flipY);
        }
    }

    public static List<String> listFormattedStringToWidth(Font font, String line, int width) {
        List<String> result = new ArrayList<>();
        List<FormattedText> list = font.getSplitter().splitLines(line, width, Style.EMPTY);

        for (FormattedText text : list) {
            result.add(text.getString());
        }

        if (!result.isEmpty()) return result;
        else return List.of();
    }
}
