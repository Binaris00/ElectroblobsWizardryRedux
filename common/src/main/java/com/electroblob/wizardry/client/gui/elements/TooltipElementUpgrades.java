package com.electroblob.wizardry.client.gui.elements;

import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.util.WandHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.electroblob.wizardry.client.EBClientConstants.TOOLTIP_BORDER;
import static com.electroblob.wizardry.client.EBClientConstants.TOOLTIP_WIDTH;

class TooltipElementUpgrades extends TooltipElement {
    private static final int ITEM_SIZE = 16;
    private static final int ITEM_SPACING = 2;

    public TooltipElementUpgrades(int spaceAfter) {
        super(spaceAfter);
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

        for (DeferredObject<Item> item : WandHelper.getSpecialUpgrades()) {
            int level = WandHelper.getUpgradeLevel(stack, item);

            if (level > 0) {
                ItemStack upgrade = new ItemStack(item.get(), level);
                guiGraphics.renderFakeItem(upgrade, x + x1, y);

                x1 += ITEM_SIZE + ITEM_SPACING;

                if (x1 + ITEM_SIZE > TOOLTIP_WIDTH - TOOLTIP_BORDER * 2) {
                    x1 = 0;
                    y += ITEM_SIZE + ITEM_SPACING;
                }
            }
        }
    }

    // TODO WAND UPGRADES
    @Override
    protected void drawForeground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, int mouseX, int mouseY) {
//        int x1 = 0;
//
//        for (DeferredObject<Item> item : WandHelper.getSpecialUpgrades()) {
//            int level = WandHelper.getUpgradeLevel(stack, item);
//
//            if (level > 0) {
//                if (guiArcaneWorkbench.isHovering(x + x1, y, ITEM_SIZE, ITEM_SIZE, mouseX, mouseY)) {
//                    ItemStack upgrade = new ItemStack(item.get(), level);
//                    guiArcaneWorkbench.renderTooltip(guiGraphics, upgrade, mouseX - guiArcaneWorkbench.leftPos, mouseY - guiArcaneWorkbench.topPos);
//                }
//
//                x1 += ITEM_SIZE + ITEM_SPACING;
//
//                if (TOOLTIP_BORDER * 2 + x1 + ITEM_SIZE > TOOLTIP_WIDTH) {
//                    x1 = 0;
//                    y += ITEM_SIZE + ITEM_SPACING;
//                }
//            }
//        }
    }
}
