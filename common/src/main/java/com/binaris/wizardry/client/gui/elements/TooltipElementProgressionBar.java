package com.binaris.wizardry.client.gui.elements;

import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.api.content.util.WandHelper;
import com.binaris.wizardry.client.EBClientConstants;
import com.binaris.wizardry.content.item.WandItem;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import static com.binaris.wizardry.client.EBClientConstants.TOOLTIP_BORDER;
import static com.binaris.wizardry.client.EBClientConstants.TOOLTIP_WIDTH;

public class TooltipElementProgressionBar extends TooltipElement {
    private final int height;

    public TooltipElementProgressionBar(int height, int spaceAfter) {
        super(spaceAfter);
        this.height = height;
    }

    @Override
    protected boolean isVisible(ItemStack stack) {
        return stack.getItem() instanceof WandItem;
    }

    @Override
    protected int getHeight(ItemStack stack) {
        return Minecraft.getInstance().font.lineHeight + EBClientConstants.LINE_SPACING_NARROW + EBClientConstants.PROGRESSION_BAR_HEIGHT;
    }

    @Override
    protected void drawBackground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
        y += Minecraft.getInstance().font.lineHeight + EBClientConstants.LINE_SPACING_NARROW;
        float progressFraction = 1;

        SpellTier nextTier = getNextTier(stack);
        if (nextTier != null) {
            progressFraction = (float) WandHelper.getProgression(stack) / nextTier.getProgression();
        }

        DrawingUtils.drawTexturedRect(x, y, EBClientConstants.MAIN_GUI_WIDTH, height + EBClientConstants.PROGRESSION_BAR_HEIGHT, EBClientConstants.PROGRESSION_BAR_WIDTH, EBClientConstants.PROGRESSION_BAR_HEIGHT, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);
        int width = (int) (EBClientConstants.PROGRESSION_BAR_WIDTH * progressFraction);
        DrawingUtils.drawTexturedRect(x, y, EBClientConstants.MAIN_GUI_WIDTH, height, width, EBClientConstants.PROGRESSION_BAR_HEIGHT, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);
    }

    @Override
    protected void drawForeground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, int mouseX, int mouseY) {
        SpellTier tier = ((WandItem) stack.getItem()).getTier(stack);
        guiGraphics.drawString(Minecraft.getInstance().font, tier.getDescriptionFormatted().getString(), x, y,
                tier.getDescriptionFormatted().getStyle().getColor().getValue(), true);

        SpellTier nextTier = getNextTier(stack);

        if (nextTier != null) {
            Component s = nextTier.getDescriptionFormatted().copy().withStyle(ChatFormatting.DARK_GRAY);

            if (WandHelper.getProgression(stack) >= nextTier.getProgression())
                s = nextTier.getDescriptionFormatted();
            guiGraphics.drawString(Minecraft.getInstance().font, s.getString(),
                    x + TOOLTIP_WIDTH - TOOLTIP_BORDER * 2 - Minecraft.getInstance().font.width(s.getString()), y,
                    s.getStyle().getColor().getValue(), true);
        }
    }

    private @Nullable SpellTier getNextTier(ItemStack stack) {
        SpellTier tier = ((WandItem) stack.getItem()).getTier(stack);

        if (tier != SpellTiers.MASTER) {
            return SpellTiers.getNextByLevel(tier.getLevel() + 1);
        }
        return null;
    }
}
