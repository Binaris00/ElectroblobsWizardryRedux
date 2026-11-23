package com.binaris.wizardry.client.gui.elements;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.client.util.GlyphClientHandler;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.content.data.SpellGlyphData;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.Spells;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public final class SpellInfoRenderer {
    public static final String TRANSLATION_KEY_PREFIX = "gui." + WizardryMainMod.MOD_ID + ".spell_book";

    private SpellInfoRenderer() {
    }

    public static void drawBackground(GuiGraphics guiGraphics, Spell spell, ResourceLocation texture, int left, int top, int xSize, int ySize, int textureWidth, int textureHeight) {
        boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, null);

        RenderSystem.setShaderColor(1, 1, 1, 1);
        guiGraphics.blit(discovered ? spell.getIcon() : Spells.NONE.getIcon(), left + 146, top + 20, 0, 0, 128, 128, 128, 128);
        guiGraphics.blit(texture, left, top, 0, 0, xSize, ySize, textureWidth, textureHeight);
    }

    public static void drawForeground(GuiGraphics guiGraphics, Spell spell, int left, int top) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, null);
        Font font = discovered ? minecraft.font : minecraft.fontFilterFishy;
        SpellGlyphData glyphData = GlyphClientHandler.INSTANCE.getGlyphData();

        Component spellName = discovered
                ? ((MutableComponent) spell.getDescriptionFormatted()).withStyle(ChatFormatting.BLACK)
                : Component.literal(glyphData == null ? "???" : SpellGlyphData.getGlyphName(spell, glyphData))
                .withStyle(Style.EMPTY.withFont(new ResourceLocation("minecraft", "alt")));
        guiGraphics.drawString(font, spellName, left + 17, top + 15, 0, false);
        guiGraphics.drawString(font, Component.translatable(spell.getType().getDisplayName()), left + 17, top + 26, 0x777777, false);

        ChatFormatting tierColor = spell.getTier() == SpellTiers.NOVICE ? ChatFormatting.GRAY : spell.getTier().getColor();
        MutableComponent tier = Component.translatable(TRANSLATION_KEY_PREFIX + ".tier",
                ((MutableComponent) spell.getTier().getDescriptionFormatted()).withStyle(tierColor));
        guiGraphics.drawString(font, tier, left + 17, top + 45, 0, false);

        ChatFormatting elementColor = spell.getElement() == Elements.HEALING ? ChatFormatting.GOLD : spell.getElement().getColor();
        MutableComponent element = Component.translatable(TRANSLATION_KEY_PREFIX + ".element",
                ((MutableComponent) spell.getElement().getDescriptionFormatted()).withStyle(elementColor));
        if (!discovered) element = Component.translatable(TRANSLATION_KEY_PREFIX + ".element_undiscovered");
        guiGraphics.drawString(font, element, left + 17, top + 57, 0, false);

        String manaCost = I18n.get(TRANSLATION_KEY_PREFIX + ".mana_cost", spell.getCost());
        if (!spell.isInstantCast()) manaCost = I18n.get(TRANSLATION_KEY_PREFIX + ".mana_cost_continuous", spell.getCost());
        if (!discovered) manaCost = I18n.get(TRANSLATION_KEY_PREFIX + ".mana_cost_undiscovered");
        guiGraphics.drawString(font, manaCost, left + 17, top + 69, 0, false);

        Component spellDesc = discovered
                ? ((MutableComponent) spell.getDesc()).withStyle(ChatFormatting.BLACK)
                : Component.literal(glyphData == null ? "" : SpellGlyphData.getGlyphDescription(spell, glyphData))
                .withStyle(Style.EMPTY.withFont(new ResourceLocation("minecraft", "alt")));
        guiGraphics.drawWordWrap(font, spellDesc, left + 17, top + 83, 118, 0);
    }
}
