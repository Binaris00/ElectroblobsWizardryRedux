package com.electroblob.wizardry.content.menu.slot;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.content.blockentity.ISpellSortable;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class GuiButtonSpellSort extends Button {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WizardryMainMod.MOD_ID, "textures/gui/container/spell_sort_buttons.png");
    private static final int TEXTURE_WIDTH = 32;
    private static final int TEXTURE_HEIGHT = 32;

    public final ISpellSortable.SortType sortType;

    private final ISpellSortable sortable;
    private final Screen parent;
    protected static final Button.CreateNarration DEFAULT_NARRATION = Supplier::get;

    public GuiButtonSpellSort(int id, int x, int y, ISpellSortable.SortType sortType, ISpellSortable sortable, Screen parent, OnPress onPress) {
        super(x, y, 10, 10, Component.translatable(I18n.get("container." + WizardryMainMod.MOD_ID + ":arcane_workbench.sort_" + sortType.name)), onPress, DEFAULT_NARRATION);
        this.sortType = sortType;
        this.sortable = sortable;
        this.parent = parent;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // TODO?
        //super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.visible) {
            this.isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + this.width && mouseY < getY() + this.height;

            int k = 0;
            int l = this.sortType.ordinal() * this.height;

            if (sortType == sortable.getSortType()) {
                k += this.width;
                if (sortable.isSortDescending()) k += this.width;
            }

            RenderSystem.setShaderTexture(0, TEXTURE);
            DrawingUtils.drawTexturedRect(getX(), getY(), k, l, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    // TODO
//    @Override
//    public void renderToolTip(PoseStack p_93653_, int mouseX, int mouseY) {
//        if (isHovered) parent.renderTooltip(p_93653_, this.getMessage(), mouseX, mouseY);
//    }
    // TODO

//    @Override
//    public void updateNarration(NarrationElementOutput p_169152_) {
//
//    }
}
