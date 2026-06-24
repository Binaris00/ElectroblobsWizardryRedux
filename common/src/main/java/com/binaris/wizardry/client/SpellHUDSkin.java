package com.binaris.wizardry.client;

import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.core.EBLogger;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class SpellHUDSkin {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Gson gson = new Gson();
    private static final Random random = new Random();
    private final ResourceLocation texture;
    private String name;
    private String description;
    private int width;
    private int height;
    private boolean mirrorX;
    private boolean mirrorY;
    private int spellIconInsetX;
    private int spellIconInsetY;
    private int textInsetX;
    private int textInsetY;
    private int cascadeOffsetX;
    private int cascadeOffsetY;
    private int cooldownBarX;
    private int cooldownBarY;
    private int cooldownBarLength;
    private int cooldownBarHeight;
    private boolean cooldownBarMirrorX;
    private boolean cooldownBarMirrorY;
    private boolean showCooldownWhenFull;

    public SpellHUDSkin(ResourceLocation texture, ResourceLocation metadata) {
        this.texture = texture;

        try {
            Resource metadataFile = mc.getResourceManager().getResourceOrThrow(metadata);
            BufferedReader reader = new BufferedReader(new InputStreamReader(metadataFile.open(), StandardCharsets.UTF_8));

            JsonElement je = gson.fromJson(reader, JsonElement.class);

            parseJson(je.getAsJsonObject());

        } catch (IOException e) {
            EBLogger.error("Error reading spell HUD skin metadata file: ", e);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }

    private void parseJson(JsonObject json) {
        name = GsonHelper.getAsString(json, "name");
        description = GsonHelper.getAsString(json, "description");

        width = GsonHelper.getAsInt(json, "width");
        height = GsonHelper.getAsInt(json, "height");

        JsonObject mirror = GsonHelper.getAsJsonObject(json, "mirror");
        mirrorX = GsonHelper.getAsBoolean(mirror, "x");
        mirrorY = GsonHelper.getAsBoolean(mirror, "y");

        JsonObject spellIconInset = GsonHelper.getAsJsonObject(json, "spell_icon_inset");
        spellIconInsetX = GsonHelper.getAsInt(spellIconInset, "x");
        spellIconInsetY = GsonHelper.getAsInt(spellIconInset, "y");

        JsonObject textInset = GsonHelper.getAsJsonObject(json, "text_inset");
        textInsetX = GsonHelper.getAsInt(textInset, "x");
        textInsetY = GsonHelper.getAsInt(textInset, "y");

        JsonObject cascadeOffset = GsonHelper.getAsJsonObject(json, "spell_cascade_offset");
        cascadeOffsetX = GsonHelper.getAsInt(cascadeOffset, "x");
        cascadeOffsetY = GsonHelper.getAsInt(cascadeOffset, "y");

        JsonObject cooldownBar = GsonHelper.getAsJsonObject(json, "cooldown_bar");
        cooldownBarX = GsonHelper.getAsInt(cooldownBar, "x");
        cooldownBarY = GsonHelper.getAsInt(cooldownBar, "y");
        cooldownBarLength = GsonHelper.getAsInt(cooldownBar, "length");
        cooldownBarHeight = GsonHelper.getAsInt(cooldownBar, "height");

        JsonObject cooldownBarMirror = GsonHelper.getAsJsonObject(cooldownBar, "mirror");
        cooldownBarMirrorX = GsonHelper.getAsBoolean(cooldownBarMirror, "x");
        cooldownBarMirrorY = GsonHelper.getAsBoolean(cooldownBarMirror, "y");

        showCooldownWhenFull = GsonHelper.getAsBoolean(cooldownBar, "show_when_full");
    }

    public void drawBackground(GuiGraphics guiGraphics, int x, int y, boolean flipX, boolean flipY,
                               ResourceLocation icon, float cooldownBarProgress, boolean creativeMode, boolean jammed) {
        if (flipX && !mirrorX) x -= width;
        if (flipY && !mirrorY) y += height;

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Icon
        int x1 = flipX && mirrorX ? x - spellIconInsetX - SpellGUIDisplay.SPELL_ICON_SIZE : x + spellIconInsetX;
        int y1 = flipY && mirrorY ? y + spellIconInsetY : y - spellIconInsetY - SpellGUIDisplay.SPELL_ICON_SIZE;
        int iconSize = SpellGUIDisplay.SPELL_ICON_SIZE;
        if (jammed) {
            random.setSeed(mc.level.getGameTime() / 2);
            drawGlitchRect(guiGraphics, icon, random, x1, y1, 0, 0, iconSize, iconSize, iconSize, iconSize, false, false);
        } else {
            guiGraphics.blit(icon, x1, y1, 0, 0, iconSize, iconSize, iconSize, iconSize);
        }

        // Background
        x1 = flipX && mirrorX ? x - width : x;
        y1 = flipY && mirrorY ? y : y - height;
        if (jammed) {
            drawGlitchRect(guiGraphics, texture, random, x1, y1, creativeMode ? 128 : 0, 0, width, height, 256, 256,
                    flipX && mirrorX, flipY && mirrorY);
        } else {
            blitFlipped(guiGraphics, texture, x1, y1, creativeMode ? 128 : 0, 0, width, height, 256, 256,
                    flipX && mirrorX, flipY && mirrorY);
        }

        // Cooldown bar
        if (!creativeMode && cooldownBarProgress > 0 && (showCooldownWhenFull || cooldownBarProgress < 1)) {
            int l = (int) (cooldownBarProgress * cooldownBarLength);
            x1 = flipX && mirrorX ? x - cooldownBarX - (cooldownBarMirrorX ? l : cooldownBarLength) : x + cooldownBarX;
            y1 = flipY && mirrorY ? y + cooldownBarY : y - cooldownBarY - cooldownBarHeight;
            if (jammed) {
                drawGlitchRect(guiGraphics, texture, random, x1, y1, cooldownBarX, height, l, cooldownBarHeight, 256, 256,
                        flipX && cooldownBarMirrorX, flipY && cooldownBarMirrorY);
            } else {
                blitFlipped(guiGraphics, texture, x1, y1, cooldownBarX, height, l, cooldownBarHeight, 256, 256,
                        flipX && cooldownBarMirrorX, flipY && cooldownBarMirrorY);
            }
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private static void blitFlipped(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int w, int h, int texW, int texH, boolean flipX, boolean flipY) {
        if (!flipX && !flipY) {
            guiGraphics.blit(texture, x, y, u, v, w, h, texW, texH);
            return;
        }

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        float f  = 1f / texW;
        float f1 = 1f / texH;

        float u1 = flipX ? (u + w) * f : u * f;
        float u2 = flipX ? u * f       : (u + w) * f;
        float v1 = flipY ? (v + h) * f1 : v * f1;
        float v2 = flipY ? v * f1        : (v + h) * f1;

        Matrix4f mat = guiGraphics.pose().last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(mat, x,     y + h, 0).uv(u1, v2).endVertex();
        buffer.vertex(mat, x + w, y + h, 0).uv(u2, v2).endVertex();
        buffer.vertex(mat, x + w, y,     0).uv(u2, v1).endVertex();
        buffer.vertex(mat, x,     y,     0).uv(u1, v1).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    private static void drawGlitchRect(GuiGraphics guiGraphics, ResourceLocation texture, Random random, int x, int y, int u, int v, int width, int height, int texW, int texH, boolean flipX, boolean flipY) {
        for (int i = 0; i < height; i++) {
            int row = flipY ? height - i - 1 : i;
            int offset = random.nextInt(4) == 0 ? random.nextInt(6) - 3 : 0;
            blitFlipped(guiGraphics, texture, x + offset, y + row, u, v + row, width, 1, texW, texH, flipX, flipY);
        }
    }

    public void drawText(GuiGraphics guiGraphics, int x, int y, boolean flipX, boolean flipY, Component prevSpellName, Component spellName, Component nextSpellName, float animationProgress) {
        if (flipX && !mirrorX) {
            x -= width;
        }
        if (flipY && !mirrorY) {
            y += height;
        }

        Font font = mc.font;
        int x1 = (flipX && mirrorX) ? (x - width) : (x + textInsetX);
        int y1 = (flipY && mirrorY) ? (y + textInsetY - font.lineHeight / 2 + 2) : (y - textInsetY - font.lineHeight / 2 - 1);

        int maxWidth = width - textInsetX;

        if (animationProgress == 0) {
            float xPrev = (flipX && mirrorX) ? (x - width) : (x + textInsetX - (flipY ? -1 : 1) * cascadeOffsetX);
            float xNext = (flipX && mirrorX) ? (x - width) : (x + textInsetX + (flipY ? -1 : 1) * cascadeOffsetX);
            float yPrev = y1 - (cascadeOffsetY + 1);
            float yNext = y1 + cascadeOffsetY;

            float maxWidthPrev = maxWidth + (flipY ? -1 : 1) * cascadeOffsetX;
            float maxWidthNext = maxWidth - (flipY ? -1 : 1) * cascadeOffsetX;

            // Make the spell name transparent
            int sideColour = ClientUtils.makeTranslucentColor(0xffffff, SpellGUIDisplay.SPELL_NAME_OPACITY);

            drawScaledStringToWidth(guiGraphics, font, prevSpellName, xPrev, yPrev, SpellGUIDisplay.SPELL_NAME_SCALE, sideColour, maxWidthPrev, true, flipX && mirrorX);
            drawScaledStringToWidth(guiGraphics, font, spellName, x1, y1, 1, 0xffffffff, maxWidth, true, flipX && mirrorX);
            drawScaledStringToWidth(guiGraphics, font, nextSpellName, xNext, yNext, SpellGUIDisplay.SPELL_NAME_SCALE, sideColour, maxWidthNext, true, flipX && mirrorX);
        } else {
            boolean reverse = animationProgress < 0;
            if (reverse) {
                animationProgress = 1 - Math.abs(animationProgress);
            }

            float xPrev = (flipX && mirrorX) ? (x - width) : (x + textInsetX - (flipY ? -1 : 1) * cascadeOffsetX * animationProgress);
            float xNext = (flipX && mirrorX) ? (x - width) : (x + textInsetX + (flipY ? -1 : 1) * cascadeOffsetX * (1 - animationProgress));
            float yPrev = y1 - (cascadeOffsetY + 1) * animationProgress;
            float yNext = y1 + cascadeOffsetY * (1 - animationProgress);

            float maxWidthPrev = maxWidth + (flipY ? -1 : 1) * cascadeOffsetX * animationProgress;
            float maxWidthNext = maxWidth - (flipY ? -1 : 1) * cascadeOffsetX * (1 - animationProgress);
            float scalePrev = SpellGUIDisplay.SPELL_NAME_SCALE + (1 - SpellGUIDisplay.SPELL_NAME_SCALE) * (1 - animationProgress);
            float scaleNext = SpellGUIDisplay.SPELL_NAME_SCALE + (1 - SpellGUIDisplay.SPELL_NAME_SCALE) * (animationProgress);
            int clrPrev = ClientUtils.makeTranslucentColor(0xffffff, (int) (SpellGUIDisplay.SPELL_NAME_OPACITY + (1 - SpellGUIDisplay.SPELL_NAME_OPACITY) * (1 - animationProgress)));
            int clrNext = ClientUtils.makeTranslucentColor(0xffffff, (int) (SpellGUIDisplay.SPELL_NAME_OPACITY + (1 - SpellGUIDisplay.SPELL_NAME_OPACITY) * animationProgress));

            if (reverse) {
                drawScaledStringToWidth(guiGraphics, font, spellName, xPrev, yPrev, scalePrev, clrPrev, maxWidthPrev, true, flipX && mirrorX);
                drawScaledStringToWidth(guiGraphics, font, nextSpellName, xNext, yNext, scaleNext, clrNext, maxWidthNext, true, flipX && mirrorX);
            } else {
                drawScaledStringToWidth(guiGraphics, font, prevSpellName, xPrev, yPrev, scalePrev, clrPrev, maxWidthPrev, true, flipX && mirrorX);
                drawScaledStringToWidth(guiGraphics, font, spellName, xNext, yNext, scaleNext, clrNext, maxWidthNext, true, flipX && mirrorX);
            }
        }
    }

    private static void drawScaledStringToWidth(GuiGraphics guiGraphics, Font font, Component text, float x, float y, float scale, int colour, float width, boolean centre, boolean alignR) {
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
