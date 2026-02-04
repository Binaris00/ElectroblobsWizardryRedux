package com.binaris.wizardry.client.gui.button;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.client.EBClientConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class GuiButtonClear extends Button {
    public GuiButtonClear(int id, int x, int y, OnPress onPress) {
        super(x, y, 16, 16, Component.translatable("container." + WizardryMainMod.MOD_ID + ".arcane_workbench.clear"), onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX()
                + this.width && mouseY < this.getY() + this.height;

        int k = 72;
        int l = 236;

        if (this.active) {
            if (this.isHovered) {
                k += this.width * 2;
            }
        } else {
            k += this.width;
        }

        RenderSystem.setShaderTexture(0, EBClientConstants.ARCANE_WORKBENCH_CONTAINER_TEXTURE);
        DrawingUtils.drawTexturedRect(this.getX(), this.getY(), k, l, this.width, this.height, 512, 512);
    }
}
