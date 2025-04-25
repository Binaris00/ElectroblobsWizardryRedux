package com.electroblob.wizardry.client.gui.elements;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.client.EBClientConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

class TooltipElementUpgradeList extends TooltipElementText {

    public TooltipElementUpgradeList(int spaceAfter) {
        super(I18n.get("container." + WizardryMainMod.MOD_ID + ".arcane_workbench.upgrades"), Style.EMPTY.withColor(ChatFormatting.WHITE), spaceAfter, new TooltipElementUpgrades(0));
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
