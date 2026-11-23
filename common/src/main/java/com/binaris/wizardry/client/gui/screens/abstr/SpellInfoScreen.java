package com.binaris.wizardry.client.gui.screens.abstr;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.client.gui.elements.SpellInfoRenderer;
import com.binaris.wizardry.setup.registries.EBSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class SpellInfoScreen extends Screen {
    protected static final String TRANSLATION_KEY_PREFIX = SpellInfoRenderer.TRANSLATION_KEY_PREFIX;

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
        int top = this.height / 2 - ySize / 2;
        this.renderBackground(guiGraphics);
        this.drawBackgroundLayer(guiGraphics, left, top, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.drawForegroundLayer(guiGraphics, left, top, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void drawBackgroundLayer(@NotNull GuiGraphics guiGraphics, int left, int top, int mouseX, int mouseY) {
        SpellInfoRenderer.drawBackground(guiGraphics, getSpell(), getTexture(), left, top, xSize, ySize,
                textureWidth, textureHeight);
    }

    protected void drawForegroundLayer(@NotNull GuiGraphics guiGraphics, int left, int top, int mouseX, int mouseY) {
        SpellInfoRenderer.drawForeground(guiGraphics, getSpell(), left, top);
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.MISC_BOOK_OPEN.get(), 1));
    }
}
