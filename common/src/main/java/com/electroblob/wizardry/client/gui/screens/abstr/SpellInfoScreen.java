package com.electroblob.wizardry.client.gui.screens.abstr;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.util.ClientUtils;
import com.electroblob.wizardry.api.client.util.GlyphClientHandler;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.content.data.SpellGlyphData;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Spells;
import com.electroblob.wizardry.setup.registries.Tiers;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class SpellInfoScreen extends Screen {
    protected static final String TRANSLATION_KEY_PREFIX = "gui." + WizardryMainMod.MOD_ID + ".spell_book";

    protected final int xSize, ySize;
    protected int textureWidth = 512;
    protected int textureHeight = 256;

    protected SpellInfoScreen(int xSize, int ySize, Component component) {
        super(component);
        this.xSize = xSize;
        this.ySize = ySize;
    }

    protected void setTextureSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
    }

    public abstract Spell getSpell();

    public abstract ResourceLocation getTexture();

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int left = this.width / 2 - xSize / 2;
        int top = this.height / 2 - this.ySize / 2;
        this.renderBackground(guiGraphics);
        this.drawBackgroundLayer(left, top, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.drawForegroundLayer(guiGraphics, left, top, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void drawBackgroundLayer(int left, int top, int mouseX, int mouseY) {
        boolean discovered = ClientUtils.shouldDisplayDiscovered(getSpell(), null);

        RenderSystem.setShaderColor(1, 1, 1, 1);

        RenderSystem.setShaderTexture(0, discovered ? getSpell().getIcon() : Spells.NONE.getIcon());
        DrawingUtils.drawTexturedRect(left + 146, top + 20, 0, 0, 128, 128, 128, 128);

        RenderSystem.setShaderTexture(0, getTexture());
        DrawingUtils.drawTexturedRect(left, top, 0, 0, xSize, ySize, textureWidth, textureHeight);
    }

    protected void drawForegroundLayer(GuiGraphics guiGraphics, int left, int top, int mouseX, int mouseY) {
        boolean discovered = ClientUtils.shouldDisplayDiscovered(getSpell(), null);
        Font font = discovered ? minecraft.font : minecraft.fontFilterFishy;

        Component spellName = discovered ? ((MutableComponent) getSpell().getDescriptionFormatted()).withStyle(ChatFormatting.BLACK) : Component.literal(SpellGlyphData.getGlyphName(getSpell(), GlyphClientHandler.INSTANCE.getGlyphData())).withStyle(Style.EMPTY.withFont(new ResourceLocation("minecraft", "alt")));
        guiGraphics.drawString(font, spellName, left + 17, top + 15, 0, false);
        guiGraphics.drawString(font, getSpell().getType().getDisplayName(), left + 17, top + 26, 0x777777, false);

        ChatFormatting tierColor = getSpell().getTier() == Tiers.NOVICE ? ChatFormatting.GRAY : getSpell().getTier().getColor();
        MutableComponent tier = Component.translatable(TRANSLATION_KEY_PREFIX + ".tier", ((MutableComponent)getSpell().getTier().getDescriptionFormatted()).withStyle(tierColor));
        guiGraphics.drawString(this.font, tier, left + 17, top + 45, 0, false);

        ChatFormatting elementColor = getSpell().getElement() == Elements.HEALING ? ChatFormatting.GOLD : getSpell().getElement().getColor();
        MutableComponent element = Component.translatable(TRANSLATION_KEY_PREFIX + ".element", ((MutableComponent) getSpell().getElement().getDescriptionFormatted()).withStyle(elementColor));
        if (!discovered) element = Component.translatable(TRANSLATION_KEY_PREFIX + ".element_undiscovered");
        guiGraphics.drawString(this.font, element, left + 17, top + 57, 0, false);

        String manaCost = I18n.get(TRANSLATION_KEY_PREFIX + ".mana_cost", getSpell().getCost());
        if (!getSpell().isInstantCast()) manaCost = I18n.get(TRANSLATION_KEY_PREFIX + ".mana_cost_continuous", getSpell().getCost());
        if (!discovered) manaCost = I18n.get(TRANSLATION_KEY_PREFIX + ".mana_cost_undiscovered");
        guiGraphics.drawString(this.font, manaCost, left + 17, top + 69, 0, false);

        Component spellDesc = discovered ? ((MutableComponent) getSpell().getDesc()).withStyle(ChatFormatting.BLACK) :
                Component.literal(SpellGlyphData.getGlyphDescription(getSpell(), GlyphClientHandler.INSTANCE.getGlyphData())).withStyle(Style.EMPTY.withFont(new ResourceLocation("minecraft", "alt")));
        guiGraphics.drawWordWrap(font, spellDesc, left + 17, top + 83, 118, 0);

    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.MISC_BOOK_OPEN.get(), 1));
    }

    @Override
    public boolean isPauseScreen() {
        return EBConfig.booksPauseGame;
    }
}
