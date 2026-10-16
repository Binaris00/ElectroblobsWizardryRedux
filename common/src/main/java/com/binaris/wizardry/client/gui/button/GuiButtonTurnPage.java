package com.binaris.wizardry.client.gui.button;

import com.binaris.wizardry.api.client.util.DrawingUtils;
import com.binaris.wizardry.setup.registries.EBSounds;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GuiButtonTurnPage extends Button {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 12;
    public final Type type;
    private final ResourceLocation texture;
    private final int textureWidth, textureHeight;

    public GuiButtonTurnPage(int x, int y, Type type, ResourceLocation texture, int textureWidth, int textureHeight, OnPress onPress) {
        super(x, y, WIDTH, HEIGHT, Component.empty(), onPress, DEFAULT_NARRATION);
        this.type = type;
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void playDownSound(@NotNull SoundManager handler) {
        handler.play(SimpleSoundInstance.forUI(EBSounds.MISC_PAGE_TURN.get(), 1));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            boolean flag = mouseX >= this.getX() && mouseY >= this.getY() &&
                    mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            DrawingUtils.drawTexturedRect(guiGraphics, texture, this.getX(), this.getY(), flag ? type.u + width : type.u, type.v, width, height, textureWidth, textureHeight);
        }
    }

    public enum Type {
        NEXT_PAGE(0, 196),
        PREVIOUS_PAGE(0, 208),
        NEXT_SECTION(0, 220),
        PREVIOUS_SECTION(0, 232),
        CONTENTS(0, 244);

        private final int u, v;

        Type(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }
}
