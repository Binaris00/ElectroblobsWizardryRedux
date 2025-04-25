package com.electroblob.wizardry.client.gui.elements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

/**
 * Used internally for the {@link com.electroblob.wizardry.client.gui.screens.ArcaneWorkbenchScreen ArcaneWorkbechScreen},
 * this is just the template that display information about the selected item in the menu,
 * with this you could render: <br>
 * text (Check {@link TooltipElementText}) <br>
 * items (Check {@link TooltipElementUpgrades}) <br>
 * and custom textures and lists (Check {@link TooltipElementSpellEntry} and {@link TooltipElementSpellList})
 * */
public abstract class TooltipElement {
    /** You could need to add different lists like spells or items */
    private final TooltipElement[] children;
    /** Space from this tooltip element and the next one */
    public final int spaceAfter;

    public TooltipElement(int spaceAfter, TooltipElement... children) {
        this.children = children;
        this.spaceAfter = spaceAfter;
    }

    /** Calculate the used distance between this tooltip element and the next one */
    public int getTotalHeight(ItemStack stack) {
        if (!this.isVisible(stack)) return 0;
        int height = this.getHeight(stack);
        for (TooltipElement child : children) height += child.getTotalHeight(stack);
        return height + spaceAfter;
    }

    /**
     * Renders the background layer for this element and its children.
     * This is where you should draw boxes, icons, and backgrounds before text.
     * */
    public int drawBackgroundLayer(GuiGraphics guiGraphics, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
        if (!this.isVisible(stack)) return y;
        this.drawBackground(guiGraphics, x, y, stack, partialTicks, mouseX, mouseY);
        y += this.getHeight(stack);
        for (TooltipElement child : children)
            y = child.drawBackgroundLayer(guiGraphics, x, y, stack, partialTicks, mouseX, mouseY);
        return y + spaceAfter;
    }

    /**
     * Renders the foreground layer for this element and its children.
     * This is where you should draw text or overlays on top of the background.
     * */
    public int drawForegroundLayer(GuiGraphics guiGraphics, int x, int y, ItemStack stack, int mouseX, int mouseY) {
        if (!this.isVisible(stack)) return y;
        this.drawForeground(guiGraphics, x, y, stack, mouseX, mouseY);
        y += this.getHeight(stack);
        for (TooltipElement child : children) y = child.drawForegroundLayer(guiGraphics, x, y, stack, mouseX, mouseY);
        return y + spaceAfter;
    }

    /**
     * Whether this tooltip element should be visible for the given item stack.
     * Used to conditionally render elements based on item tags or state.
     */
    protected abstract boolean isVisible(ItemStack stack);

    /** Returns the vertical height of this element (excluding children or spacing). */
    protected abstract int getHeight(ItemStack stack);

    /** Draws the background layer of this element. */
    protected abstract void drawBackground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY);

    /** Draws the foreground layer of this element. */
    protected abstract void drawForeground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, int mouseX, int mouseY);
}
