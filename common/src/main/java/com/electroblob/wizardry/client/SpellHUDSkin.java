package com.electroblob.wizardry.client;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class SpellHUDSkin {
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

    private static final Minecraft mc = Minecraft.getInstance();
    private static final Gson gson = new Gson();
    private static final Random random = new Random();

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
        // TODO
//            if (width > 128)
//                Wizardry.logger.warn("The width of the spell HUD skin " + name + " exceeds 128, this may cause it to render strangely.");
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

    public void drawBackground(PoseStack stack, int x, int y, boolean flipX, boolean flipY, ResourceLocation icon, float cooldownBarProgress, boolean creativeMode, boolean jammed) {
        if (flipX && !mirrorX) x -= width;
        if (flipY && !mirrorY) y += height;

        stack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, icon);

        int x1 = flipX && mirrorX ? x - spellIconInsetX - SpellGUIDisplay.SPELL_ICON_SIZE : x + spellIconInsetX;
        int y1 = flipY && mirrorY ? y + spellIconInsetY : y - spellIconInsetY - SpellGUIDisplay.SPELL_ICON_SIZE;

        if (jammed) {
            random.setSeed(mc.level.getGameTime() / 2);
            DrawingUtils.drawGlitchRect(random, x1, y1, 0, 0, SpellGUIDisplay.SPELL_ICON_SIZE, SpellGUIDisplay.SPELL_ICON_SIZE, SpellGUIDisplay.SPELL_ICON_SIZE, SpellGUIDisplay.SPELL_ICON_SIZE, false, false);
        } else {
            DrawingUtils.drawTexturedRect(x1, y1, 0, 0, SpellGUIDisplay.SPELL_ICON_SIZE, SpellGUIDisplay.SPELL_ICON_SIZE, SpellGUIDisplay.SPELL_ICON_SIZE, SpellGUIDisplay.SPELL_ICON_SIZE);
        }

        RenderSystem.setShaderTexture(0, texture);

        x1 = flipX && mirrorX ? x - width : x;
        y1 = flipY && mirrorY ? y : y - height;

        if (jammed) {
            DrawingUtils.drawGlitchRect(random, x1, y1, creativeMode ? 128 : 0, 0, width, height, 256, 256, flipX && mirrorX, flipY && mirrorY);
        } else {
            DrawingUtils.drawTexturedFlippedRect(x1, y1, creativeMode ? 128 : 0, 0, width, height, 256, 256, flipX && mirrorX, flipY && mirrorY);
        }

        if (!creativeMode && cooldownBarProgress > 0 && (showCooldownWhenFull || cooldownBarProgress < 1)) {
            int l = (int) (cooldownBarProgress * cooldownBarLength);
            x1 = flipX && mirrorX ? x - cooldownBarX - (cooldownBarMirrorX ? l : cooldownBarLength) : x + cooldownBarX;
            y1 = flipY && mirrorY ? y + cooldownBarY : y - cooldownBarY - cooldownBarHeight;

            int u = cooldownBarX;
            int v = height;

            if (jammed) {
                DrawingUtils.drawGlitchRect(random, x1, y1, u, v, l, cooldownBarHeight, 256, 256, flipX && cooldownBarMirrorX, flipY && cooldownBarMirrorY);
            } else {
                DrawingUtils.drawTexturedFlippedRect(x1, y1, u, v, l, cooldownBarHeight, 256, 256, flipX && cooldownBarMirrorX, flipY && cooldownBarMirrorY);
            }
        }

        stack.popPose();
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

            int sideColour = DrawingUtils.makeTranslucent(0xffffff, SpellGUIDisplay.SPELL_NAME_OPACITY);


            DrawingUtils.drawScaledStringToWidth(guiGraphics, font, prevSpellName, xPrev, yPrev, SpellGUIDisplay.SPELL_NAME_SCALE, sideColour, maxWidthPrev, true, flipX && mirrorX);
            DrawingUtils.drawScaledStringToWidth(guiGraphics, font, spellName, x1, y1, 1, 0xffffffff, maxWidth, true, flipX && mirrorX);
            DrawingUtils.drawScaledStringToWidth(guiGraphics, font, nextSpellName, xNext, yNext, SpellGUIDisplay.SPELL_NAME_SCALE, sideColour, maxWidthNext, true, flipX && mirrorX);
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
            int clrPrev = DrawingUtils.makeTranslucent(0xffffff, (int)(SpellGUIDisplay.SPELL_NAME_OPACITY + (1 - SpellGUIDisplay.SPELL_NAME_OPACITY) * (1 - animationProgress)));
            int clrNext = DrawingUtils.makeTranslucent(0xffffff, (int)(SpellGUIDisplay.SPELL_NAME_OPACITY + (1 - SpellGUIDisplay.SPELL_NAME_OPACITY) * animationProgress));

            if (reverse) {
                DrawingUtils.drawScaledStringToWidth(guiGraphics, font, spellName, xPrev, yPrev, scalePrev, clrPrev, maxWidthPrev, true, flipX && mirrorX);
                DrawingUtils.drawScaledStringToWidth(guiGraphics, font, nextSpellName, xNext, yNext, scaleNext, clrNext, maxWidthNext, true, flipX && mirrorX);
            } else {
                DrawingUtils.drawScaledStringToWidth(guiGraphics, font, prevSpellName, xPrev, yPrev, scalePrev, clrPrev, maxWidthPrev, true, flipX && mirrorX);
                DrawingUtils.drawScaledStringToWidth(guiGraphics, font, spellName, xNext, yNext, scaleNext, clrNext, maxWidthNext, true, flipX && mirrorX);
            }
        }
    }
}
