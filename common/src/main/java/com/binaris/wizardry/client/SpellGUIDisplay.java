package com.binaris.wizardry.client;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.client.util.GlyphClientHandler;
import com.binaris.wizardry.api.content.event.EBLivingTick;
import com.binaris.wizardry.api.content.item.ICastItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.CastItemUtils;
import com.binaris.wizardry.content.data.SpellGlyphData;
import com.binaris.wizardry.core.config.EBClientConfig;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.Random;

public final class SpellGUIDisplay {
    private static final int CHARGE_METER_WIDTH = 25;
    private static final int CHARGE_METER_HEIGHT = 9;
    private static final int SPELL_ICON_SIZE = 32;
    private static final float SPELL_NAME_SCALE = 0.5f;
    private static final float SPELL_NAME_OPACITY = 0.3f;
    private static final ResourceLocation CHARGE_METER = WizardryMainMod.location("textures/gui/spell_charge_meter.png");
    private static final Random random = new Random();
    private static final ResourceLocation TEXTURE = WizardryMainMod.location("gui/spell_hud/default.png");

    private static final int SPELL_UI_WIDTH = 128;
    private static final int SPELL_UI_HEIGHT = 50;
    private static final int SPELL_UI_SPELL_ICON_INSET_X = 2;
    private static final int SPELL_UI_SPELL_ICON_INSET_Y = 2;
    private static final int SPELL_UI_TEXT_INSET_X = 42;
    private static final int SPELL_UI_TEXT_INSET_Y = 18;
    private static final int SPELL_CASCADE_OFFSET_X = 2;
    private static final int SPELL_CASCADE_OFFSET_Y = 8;
    private static final int COOLDOWN_BAR_X = 42;
    private static final int COOLDOWN_BAR_Y = 2;
    private static final int COOLDOWN_BAR_LENGTH = 79;
    private static final int COOLDOWN_BAR_HEIGHT = 3;

    private static final int SPELL_SWITCH_TIME = 4;
    private static final Minecraft mc = Minecraft.getInstance();
    private static int switchTimer = 0;

    public static void draw(GuiGraphics guiGraphics, PoseStack stack, float partialTicks) {
        if (!EBClientConfig.SHOW_SPELL_HUD.get() && !EBClientConfig.SHOW_CHARGE_METER.get()) return;

        Player player = mc.player;
        if (player.isSpectator()) return;

        ItemStack wand = player.getMainHandItem();
        boolean mainHand = true;

        if (!(wand.getItem() instanceof ICastItem castingItem && castingItem.showSpellHUD(player, wand))) {
            wand = player.getOffhandItem();
            mainHand = false;
            if (!(wand.getItem() instanceof ICastItem castingItem && castingItem.showSpellHUD(player, wand)))
                return;
        }

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        renderChargeMeter(guiGraphics, player, wand, width, height, partialTicks);
        renderSpellHUD(guiGraphics, stack, player, wand, mainHand, width, height, partialTicks, true);
        renderSpellHUD(guiGraphics, stack, player, wand, mainHand, width, height, partialTicks, false);
    }

    public static void playSpellSwitchAnimation(boolean next) {
        switchTimer = next ? SPELL_SWITCH_TIME : -SPELL_SWITCH_TIME;
    }

    public static void renderSpellHUD(GuiGraphics guiGraphics, PoseStack stack, Player player, ItemStack wand, boolean mainHand, int width, int height, float partialTicks, boolean textLayer) {
        if (!EBClientConfig.SHOW_SPELL_HUD.get()) return;

        if (!(wand.getItem() instanceof ICastItem))
            throw new IllegalArgumentException("The given stack must contain an ISpellCastingItem!");

        boolean flipX = EBClientConfig.SPELL_HUD_FLIP_X.get();
        boolean flipY = EBClientConfig.SPELL_HUD_FLIP_Y.get();

        if (EBClientConfig.SPELL_HUD_DYNAMIC_POSITIONING.get()) {
            flipX = flipX == ((mainHand ? player.getMainArm() : player.getMainArm().getOpposite()) == HumanoidArm.LEFT);
        }

        stack.pushPose();

        int x = flipX ? width : 0;
        int y = flipY ? 0 : height;

        Spell spell = ((ICastItem) wand.getItem()).getCurrentSpell(wand);
        int cooldown = ((ICastItem) wand.getItem()).getCurrentCooldown(wand, player.level());
        int maxCooldown = ((ICastItem) wand.getItem()).getCurrentMaxCooldown(wand);

        if (textLayer) {
            float animationProgress = Math.signum(switchTimer) * ((SPELL_SWITCH_TIME - Math.abs(switchTimer) + partialTicks) / SPELL_SWITCH_TIME);

            Component prevSpellName = getFormattedSpellName(((ICastItem) wand.getItem()).getPreviousSpell(wand), player, 0);
            Component spellName = getFormattedSpellName(((ICastItem) wand.getItem()).getCurrentSpell(wand), player, cooldown);
            Component nextSpellName = getFormattedSpellName(((ICastItem) wand.getItem()).getNextSpell(wand), player, 0);
            drawText(guiGraphics, x, y, flipX, flipY, prevSpellName, spellName, nextSpellName, animationProgress);
        } else {
            boolean discovered = true;

            if (!player.isCreative()) {
                discovered = Services.OBJECT_DATA.getSpellManagerData(player).hasSpellBeenDiscovered(spell);
            }

            ResourceLocation location = spell.getLocation();
            ResourceLocation icon = discovered ?
                    WizardryMainMod.location(location.getNamespace(), "textures/spells/%s.png".formatted(location.getPath()))
                    : WizardryMainMod.location("textures/spells/none.png");

            float progress = 1;
            if (!player.isCreative()) {
                progress = maxCooldown == 0 ? 1 : (maxCooldown - (float) cooldown + partialTicks) / maxCooldown;
            }

            drawBackground(guiGraphics, x, y, flipX, flipY, icon, progress, player.isCreative(), player.hasEffect(EBMobEffects.ARCANE_JAMMER.get()));
        }

        stack.popPose();
    }

    private static void drawBackground(GuiGraphics guiGraphics, int x, int y, boolean flipX, boolean flipY, ResourceLocation icon, float cooldownBarProgress, boolean creativeMode, boolean jammed) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        // Icon
        int x1 = flipX ? x - SPELL_UI_SPELL_ICON_INSET_X - SPELL_ICON_SIZE : x + SPELL_UI_SPELL_ICON_INSET_X;
        int y1 = flipY ? y + SPELL_UI_SPELL_ICON_INSET_Y : y - SPELL_UI_SPELL_ICON_INSET_Y - SPELL_ICON_SIZE;
        int iconSize = SPELL_ICON_SIZE;
        if (jammed) {
            random.setSeed(mc.level.getGameTime() / 2);
            drawGlitchRect(guiGraphics, icon, x1, y1, 0, 0, iconSize, iconSize, iconSize, iconSize, false, false);
        } else {
            guiGraphics.blit(icon, x1, y1, 0, 0, iconSize, iconSize, iconSize, iconSize);
        }

        // Background
        x1 = flipX ? x - SPELL_UI_WIDTH : x;
        y1 = flipY ? y : y - SPELL_UI_HEIGHT;
        if (jammed) {
            drawGlitchRect(guiGraphics, TEXTURE, x1, y1, creativeMode ? 128 : 0, 0, SPELL_UI_WIDTH, SPELL_UI_HEIGHT, 256, 256,
                    flipX, flipY);
        } else {
            blitFlipped(guiGraphics, TEXTURE, x1, y1, creativeMode ? 128 : 0, 0, SPELL_UI_WIDTH, SPELL_UI_HEIGHT, 256, 256,
                    flipX, flipY);
        }

        // Cooldown bar
        if (!creativeMode && cooldownBarProgress > 0) {
            int l = (int) (cooldownBarProgress * COOLDOWN_BAR_LENGTH);
            x1 = flipX ? x - COOLDOWN_BAR_X - l : x + COOLDOWN_BAR_X;
            y1 = flipY ? y + COOLDOWN_BAR_Y : y - COOLDOWN_BAR_Y - COOLDOWN_BAR_HEIGHT;
            if (jammed) {
                drawGlitchRect(guiGraphics, TEXTURE, x1, y1, COOLDOWN_BAR_X, SPELL_UI_HEIGHT, l, COOLDOWN_BAR_HEIGHT, 256, 256,
                        flipX, flipY);
            } else {
                blitFlipped(guiGraphics, TEXTURE, x1, y1, COOLDOWN_BAR_X, SPELL_UI_HEIGHT, l, COOLDOWN_BAR_HEIGHT, 256, 256,
                        flipX, flipY);
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

    private static void drawGlitchRect(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int texW, int texH, boolean flipX, boolean flipY) {
        for (int i = 0; i < height; i++) {// Why would you do this
            int row = flipY ? height - i - 1 : i;
            int offset = SpellGUIDisplay.random.nextInt(4) == 0 ? SpellGUIDisplay.random.nextInt(6) - 3 : 0;
            blitFlipped(guiGraphics, texture, x + offset, y + row, u, v + row, width, 1, texW, texH, flipX, flipY);
        }
    }

    private static void drawText(GuiGraphics guiGraphics, int x, int y, boolean flipX, boolean flipY, Component prevSpellName, Component spellName, Component nextSpellName, float animationProgress) {
        Font font = mc.font;
        int x1 = (flipX) ? (x - SPELL_UI_WIDTH) : (x + SPELL_UI_TEXT_INSET_X);
        int y1 = (flipY) ? (y + SPELL_UI_TEXT_INSET_Y - font.lineHeight / 2 + 2) : (y - SPELL_UI_TEXT_INSET_Y - font.lineHeight / 2 - 1);

        int maxWidth = SPELL_UI_WIDTH - SPELL_UI_TEXT_INSET_X;

        if (animationProgress == 0) {
            float xPrev = (flipX) ? (x - SPELL_UI_WIDTH) : (x + SPELL_UI_TEXT_INSET_X - (flipY ? -1 : 1) * SPELL_CASCADE_OFFSET_X);
            float xNext = (flipX) ? (x - SPELL_UI_WIDTH) : (x + SPELL_UI_TEXT_INSET_X + (flipY ? -1 : 1) * SPELL_CASCADE_OFFSET_X);
            float yPrev = y1 - (SPELL_CASCADE_OFFSET_Y + 1);
            float yNext = y1 + SPELL_CASCADE_OFFSET_Y;

            float maxWidthPrev = maxWidth + (flipY ? -1 : 1) * SPELL_CASCADE_OFFSET_X;
            float maxWidthNext = maxWidth - (flipY ? -1 : 1) * SPELL_CASCADE_OFFSET_X;

            // Make the spell name transparent
            int sideColour = ClientUtils.makeTranslucentColor(0xffffff, SpellGUIDisplay.SPELL_NAME_OPACITY);

            drawScaledStringToWidth(guiGraphics, font, prevSpellName, xPrev, yPrev, SpellGUIDisplay.SPELL_NAME_SCALE, sideColour, maxWidthPrev, flipX);
            drawScaledStringToWidth(guiGraphics, font, spellName, x1, y1, 1, 0xffffffff, maxWidth, flipX);
            drawScaledStringToWidth(guiGraphics, font, nextSpellName, xNext, yNext, SpellGUIDisplay.SPELL_NAME_SCALE, sideColour, maxWidthNext, flipX);
        } else {
            boolean reverse = animationProgress < 0;
            if (reverse) {
                animationProgress = 1 - Math.abs(animationProgress);
            }

            float xPrev = (flipX) ? (x - SPELL_UI_WIDTH) : (x + SPELL_UI_TEXT_INSET_X - (flipY ? -1 : 1) * SPELL_CASCADE_OFFSET_X * animationProgress);
            float xNext = (flipX) ? (x - SPELL_UI_WIDTH) : (x + SPELL_UI_TEXT_INSET_X + (flipY ? -1 : 1) * SPELL_CASCADE_OFFSET_X * (1 - animationProgress));
            float yPrev = y1 - (SPELL_CASCADE_OFFSET_Y + 1) * animationProgress;
            float yNext = y1 + SPELL_CASCADE_OFFSET_Y * (1 - animationProgress);

            float maxWidthPrev = maxWidth + (flipY ? -1 : 1) * SPELL_CASCADE_OFFSET_X * animationProgress;
            float maxWidthNext = maxWidth - (flipY ? -1 : 1) * SPELL_CASCADE_OFFSET_X * (1 - animationProgress);
            float scalePrev = SpellGUIDisplay.SPELL_NAME_SCALE + (1 - SpellGUIDisplay.SPELL_NAME_SCALE) * (1 - animationProgress);
            float scaleNext = SpellGUIDisplay.SPELL_NAME_SCALE + (1 - SpellGUIDisplay.SPELL_NAME_SCALE) * (animationProgress);
            int clrPrev = ClientUtils.makeTranslucentColor(0xffffff, (int) (SpellGUIDisplay.SPELL_NAME_OPACITY + (1 - SpellGUIDisplay.SPELL_NAME_OPACITY) * (1 - animationProgress)));
            int clrNext = ClientUtils.makeTranslucentColor(0xffffff, (int) (SpellGUIDisplay.SPELL_NAME_OPACITY + (1 - SpellGUIDisplay.SPELL_NAME_OPACITY) * animationProgress));

            if (reverse) {
                drawScaledStringToWidth(guiGraphics, font, spellName, xPrev, yPrev, scalePrev, clrPrev, maxWidthPrev, flipX);
                drawScaledStringToWidth(guiGraphics, font, nextSpellName, xNext, yNext, scaleNext, clrNext, maxWidthNext, flipX);
            } else {
                drawScaledStringToWidth(guiGraphics, font, prevSpellName, xPrev, yPrev, scalePrev, clrPrev, maxWidthPrev, flipX);
                drawScaledStringToWidth(guiGraphics, font, spellName, xNext, yNext, scaleNext, clrNext, maxWidthNext, flipX);
            }
        }
    }


    private static void drawScaledStringToWidth(GuiGraphics guiGraphics, Font font, Component text, float x, float y, float scale, int colour, float width, boolean alignR) {
        float textWidth = font.width(text) * scale;
        float textHeight = font.lineHeight * scale;

        // If the text is wider than the desired width, adjust the scale
        if (textWidth > width) {
            scale *= width / textWidth;
            font.width(text);
        } else if (alignR) {
            x += width - textWidth;
        }

        y += (font.lineHeight - textHeight) / 2;

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

    public static void renderChargeMeter(GuiGraphics guiGraphics, Player player, ItemStack wand, int width, int height, float partialTicks) {
        if (player.isSpectator()) return;
        if (!EBClientConfig.SHOW_CHARGE_METER.get()) return;
        if (mc.options.renderDebug) return;
        if (mc.options.getCameraType() != CameraType.FIRST_PERSON) return;
        if (wand != player.getUseItem()) return;
        if (!(wand.getItem() instanceof ICastItem castItem))
            throw new IllegalArgumentException("The given stack must contain an ISpellCastingItem!");

        Spell spell = castItem.getCurrentSpell(wand);
        int chargeup = CastItemUtils.calcCharge(spell, Services.OBJECT_DATA.getWizardData(player).getSpellModifiers());
        if (chargeup <= 0) return;
        if (player.getTicksUsingItem() == 0) return;

        float charge = (player.getTicksUsingItem() + partialTicks) / chargeup;
        if (charge > 1) return;

        int x1 = width / 2 - CHARGE_METER_WIDTH / 2 - 1;
        float y = height / 2F - CHARGE_METER_HEIGHT / 2F - 0.5F;
        int w = (int) ((float) CHARGE_METER_WIDTH / 2 * charge);
        int u = CHARGE_METER_WIDTH - w;

        guiGraphics.blit(CHARGE_METER, x1,     (int) y, 0, 0, w, CHARGE_METER_HEIGHT, 32, 32);
        guiGraphics.blit(CHARGE_METER, x1 + u, (int) y, u, 0, w, CHARGE_METER_HEIGHT, 32, 32);
    }

    private static Component getFormattedSpellName(Spell spell, Player player, int cooldown) {
        boolean discovered = true;

        if (!player.isCreative()) {
            discovered = Services.OBJECT_DATA.getSpellManagerData(player).hasSpellBeenDiscovered(spell);
        }

        Style format = cooldown > 0 ? Style.EMPTY.withColor(ChatFormatting.GRAY) : Style.EMPTY.withColor(spell.getElement().getColor());
        if (!discovered)
            format = Style.EMPTY.withColor(ChatFormatting.BLUE).withFont(new ResourceLocation("minecraft", "alt"));

        if (player.hasEffect(EBMobEffects.ARCANE_JAMMER.get())) format = Style.EMPTY.withObfuscated(true);

        MutableComponent name = discovered ? Component.translatable(spell.getDescriptionId()) :
                Component.literal(SpellGlyphData.getGlyphName(spell, GlyphClientHandler.INSTANCE.getGlyphData()));
        name.withStyle(format);
        return name;
    }


    public static void onLivingTickEvent(EBLivingTick event) {
        if (event.getLevel().isClientSide && event.getEntity() == mc.player) {
            if (switchTimer > 0) switchTimer--;
            else if (switchTimer < 0) switchTimer++;
        }
    }

    private SpellGUIDisplay() {
    }
}
