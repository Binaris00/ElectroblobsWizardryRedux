package com.binaris.wizardry.client.gui.elements;

import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class TooltipElementSpellList extends TooltipElement {

    public TooltipElementSpellList(int spaceAfter, TooltipElement... children) {
        super(spaceAfter, children);
    }

    @Override
    protected boolean isVisible(ItemStack stack) {
        return stack.getItem() instanceof ISpellCastingItem castingItem && castingItem.showSpellsInWorkbench(Minecraft.getInstance().player, stack);
    }

    @Override
    public int drawBackgroundLayer(GuiGraphics guiGraphics, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        y = super.drawBackgroundLayer(guiGraphics, x, y, stack, partialTicks, mouseX, mouseY);
        RenderSystem.disableBlend();
        return y;
    }

    @Override
    protected int getHeight(ItemStack stack) {
        return 0;
    }

    @Override
    protected void drawBackground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {

    }

    @Override
    protected void drawForeground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, int mouseX, int mouseY) {

    }
}
