package com.binaris.wizardry.client.gui.elements;

import com.binaris.wizardry.api.content.DeferredObject;
import com.binaris.wizardry.api.content.util.WandHelper;
import com.binaris.wizardry.client.gui.screens.ArcaneWorkbenchScreen;
import com.binaris.wizardry.setup.registries.WandUpgrades;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.binaris.wizardry.client.EBClientConstants.TOOLTIP_BORDER;
import static com.binaris.wizardry.client.EBClientConstants.TOOLTIP_WIDTH;

class TooltipElementUpgrades extends TooltipElement {
    private static final int ITEM_SIZE = 16;
    private static final int ITEM_SPACING = 2;
    public final ArcaneWorkbenchScreen screen;

    public TooltipElementUpgrades(ArcaneWorkbenchScreen screen, int spaceAfter) {
        super(spaceAfter);
        this.screen = screen;
    }

    @Override
    protected boolean isVisible(ItemStack stack) {
        return true;
    }

    @Override
    protected int getHeight(ItemStack stack) {
        int rows = 1 + (WandHelper.getTotalUpgrades(stack) * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING) / (TOOLTIP_WIDTH - TOOLTIP_BORDER * 2);
        return rows * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
    }

    @Override
    protected void drawBackground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
        int x1 = 0;

        for (Item item : WandUpgrades.getSpecialUpgrades()) {
            int upgradeLevel = WandHelper.getUpgradeLevel(stack, item);

            if (upgradeLevel > 0) {
                ItemStack upgrade = new ItemStack(item, upgradeLevel);
                guiGraphics.renderFakeItem(upgrade, x + x1, y);
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, upgrade, x + x1, y);

                x1 += ITEM_SIZE + ITEM_SPACING;

                if (x1 + ITEM_SIZE > TOOLTIP_WIDTH - TOOLTIP_BORDER * 2) {
                    x1 = 0;
                    y += ITEM_SIZE + ITEM_SPACING;
                }
            }
        }
    }

    @Override
    protected void drawForeground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, int mouseX, int mouseY) {
        int x1 = 0;

        for (Item item : WandUpgrades.getSpecialUpgrades()) {
            int level = WandHelper.getUpgradeLevel(stack, item);
            if (level < 0) continue;

            // FIXME upgrades tooltip item on arcane workbench
//            if (screen.isHovering(x + x1, y, ITEM_SIZE, ITEM_SIZE, mouseX, mouseY)) {
//                ItemStack upgrade = new ItemStack(item.get(), level);
//
//                if(upgrade.isEmpty()) continue;
//                // upgrade
//                guiGraphics.renderTooltip(Minecraft.getInstance().font, Screen.getTooltipFromItem(Minecraft.getInstance(), upgrade),
//                        upgrade.getTooltipImage(), mouseX - screen.getLeftPos(), mouseY - screen.getTopPos());
//            }
//            x1 += ITEM_SIZE + ITEM_SPACING;
//
//            if (TOOLTIP_BORDER * 2 + x1 + ITEM_SIZE > TOOLTIP_WIDTH) {
//                x1 = 0;
//                y += ITEM_SIZE + ITEM_SPACING;
//            }

        }
    }
}
