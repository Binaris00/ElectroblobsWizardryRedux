package com.electroblob.wizardry.client.gui.elements;

import com.electroblob.wizardry.api.content.item.ISpellCastingItem;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.api.content.util.SpellUtil;
import com.electroblob.wizardry.client.gui.screens.ArcaneWorkbenchScreen;
import com.electroblob.wizardry.content.item.ScrollItem;
import com.electroblob.wizardry.content.item.SpellBookItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import static com.electroblob.wizardry.client.EBClientConstants.LINE_SPACING_NARROW;

public class TooltipElementSpellEntry extends TooltipElementText {
    private final int index;
    private final ArcaneWorkbenchScreen screen;

    public TooltipElementSpellEntry(ArcaneWorkbenchScreen screen, int index) {
        super(null, Style.EMPTY.withColor(ChatFormatting.BLUE), LINE_SPACING_NARROW);
        this.index = index;
        this.screen = screen;
    }

    private boolean shouldFlash(ItemStack stack) {
        ItemStack spellBook = screen.getMenu().getSlot(index).getItem();
        return !spellBook.isEmpty() && (spellBook.getItem() instanceof SpellBookItem || spellBook.getItem() instanceof ScrollItem)
                && SpellUtil.getSpell(stack) != ((ISpellCastingItem) stack.getItem()).getSpells(stack)[index];
    }

    private float getAlpha(float partialTicks) {
        return (Mth.sin(0.2f * (Minecraft.getInstance().player.tickCount + partialTicks)) + 1) / 4 + 0.5f;
    }

    @Override
    protected boolean isVisible(ItemStack stack) {
        return stack.getItem() instanceof ISpellCastingItem && index < ((ISpellCastingItem) stack.getItem()).getSpells(stack).length;
    }

    @Override
    protected Font getFontRenderer(ItemStack stack) {
        // TODO Wizardry.proxy.shouldDisplayDiscovered(getSpell(stack), null)
        return super.getFontRenderer(stack);
    }

    @Override
    protected int getColour(ItemStack stack) {
        Spell spell = getSpell(stack);
        int color = spell.getElement().getColor().getColor();

        return shouldFlash(stack) ? DrawingUtils.makeTranslucent(color, getAlpha(Minecraft.getInstance().getFrameTime()))
                : color;
    }

    @Override
    protected Component getText(ItemStack stack) {
        Spell spell = getSpell(stack);

        // TODO Wizardry.proxy.shouldDisplayDiscovered(spell, null)
        return Component.translatable(spell.getLocation().toString()).withStyle(spell.getElement().getColor());
    }

    @Override
    protected void drawBackground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY) {
        Spell spell = getSpell(stack);
        // TODO Wizardry.proxy.shouldDisplayDiscovered(spell, null)
        RenderSystem._setShaderTexture(0, spell.getElement().getIcon());

        if (shouldFlash(stack)) RenderSystem.setShaderColor(1, 1, 1, getAlpha(partialTicks));

        DrawingUtils.drawTexturedRect(x, y, 8, 8);
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    private Spell getSpell(ItemStack stack) {
        ItemStack spellBook = screen.getMenu().getSlot(index).getItem();

        if (!spellBook.isEmpty() && (spellBook.getItem() instanceof SpellBookItem || spellBook.getItem() instanceof ScrollItem)) {
            return SpellUtil.getSpell(spellBook);
        } else {
            return ((ISpellCastingItem) stack.getItem()).getSpells(stack)[index];
        }
    }

    @Override
    protected void drawForeground(GuiGraphics guiGraphics, int x, int y, ItemStack stack, int mouseX, int mouseY) {
        super.drawForeground(guiGraphics, x + 11, y, stack, mouseX, mouseY);
    }
}
