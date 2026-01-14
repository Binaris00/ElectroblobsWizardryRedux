package com.binaris.wizardry.client.gui.elements;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.util.WandHelper;
import com.binaris.wizardry.client.EBClientConstants;
import com.binaris.wizardry.client.gui.screens.ArcaneWorkbenchScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public class TooltipElementUpgradeList extends TooltipElementText {

    public TooltipElementUpgradeList(ArcaneWorkbenchScreen menu, int spaceAfter) {
        super(I18n.get("container." + WizardryMainMod.MOD_ID + ".arcane_workbench.upgrades"), Style.EMPTY.withColor(ChatFormatting.WHITE), spaceAfter, new TooltipElementUpgrades(menu, 0));
    }

    @Override
    protected int getHeight(ItemStack stack) {
        return super.getHeight(stack) + EBClientConstants.LINE_SPACING_NARROW;
    }

    @Override
    protected boolean isVisible(ItemStack stack) {
        return WandHelper.getTotalUpgrades(stack) > 0;
    }
}
