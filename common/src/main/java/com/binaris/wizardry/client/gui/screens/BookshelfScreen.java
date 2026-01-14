package com.binaris.wizardry.client.gui.screens;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.menu.BookshelfMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class BookshelfScreen extends AbstractContainerScreen<BookshelfMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/gui/container/bookshelf.png");
    private final Inventory playerInventory;

    public BookshelfScreen(BookshelfMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
        this.playerInventory = inventory;
        this.imageHeight = 148;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component component = this.playerInventory.getDisplayName();
        guiGraphics.drawString(this.font, component.getString(), 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, component, 8, this.imageHeight - 96 + 2, 4210752, false);
    }

}
