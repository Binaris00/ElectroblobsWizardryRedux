package com.electroblob.wizardry.client.gui.screens;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.item.IWorkbenchItem;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.client.EBClientConstants;
import com.electroblob.wizardry.client.gui.button.GuiButtonApply;
import com.electroblob.wizardry.client.gui.button.GuiButtonClear;
import com.electroblob.wizardry.client.gui.elements.*;
import com.electroblob.wizardry.content.blockentity.ISpellSortable;
import com.electroblob.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.electroblob.wizardry.content.menu.slot.GuiButtonSpellSort;
import com.electroblob.wizardry.content.menu.slot.SlotBookList;
import com.electroblob.wizardry.core.networking.c2s.ControlInputPacketC2S;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArcaneWorkbenchScreen extends AbstractContainerScreen<ArcaneWorkbenchMenu> {
    private final Inventory playerInventory;
    private final ArcaneWorkbenchMenu menu;

    private AbstractWidget applyBtn;
    private AbstractWidget clearBtn;
    private final AbstractWidget[] sortButtons = new AbstractWidget[3];

    private final List<TooltipElement> tooltipElements = new ArrayList<>();

    // TODO
    // These ones are not really used atm, but I try take them "temp work" be clean
    private EditBox searchField;
    private boolean searchNeedsClearing;
    private int searchBarHoverTime;

    private int animationTimer = 0;
    private float scroll = 0;
    private boolean scrolling = false;

    public ArcaneWorkbenchScreen(ArcaneWorkbenchMenu menu, Inventory playerInventory, Component name) {
        super(menu, playerInventory, name);
        this.menu = menu;
        this.playerInventory = playerInventory;
        imageWidth = EBClientConstants.MAIN_GUI_WIDTH;
        imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.player.containerMenu = this.menu;

        this.leftPos = (this.width - EBClientConstants.MAIN_GUI_WIDTH) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.clearWidgets();

        this.addRenderableWidget(this.applyBtn = new GuiButtonApply(0, this.width / 2 + 64, this.height / 2 + 3, (button) -> {
            if (button.active) {
                ControlInputPacketC2S packet = new ControlInputPacketC2S(ControlInputPacketC2S.ControlType.APPLY_BUTTON);
                Services.NETWORK_HELPER.sendToServer(packet);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND.get(), 1));
                animationTimer = 20;
            }
        }));
        this.addRenderableWidget(this.clearBtn = new GuiButtonClear(0, this.width / 2 + 64, this.height / 2 - 16, (button) -> {
            if (button.active) {
                ControlInputPacketC2S packet = new ControlInputPacketC2S(ControlInputPacketC2S.ControlType.CLEAR_BUTTON);
                Services.NETWORK_HELPER.sendToServer(packet);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND.get(), 0.8f));
                animationTimer = 20;
            }
        }));
        this.addRenderableWidget(sortButtons[0] = new GuiButtonSpellSort(1, this.leftPos - 44, this.topPos + 8, ISpellSortable.SortType.TIER, menu, this, (button) ->
                this.menu.setSortType(((GuiButtonSpellSort) button).sortType)));
        this.addRenderableWidget(sortButtons[1] = new GuiButtonSpellSort(2, this.leftPos - 31, this.topPos + 8, ISpellSortable.SortType.ELEMENT, menu, this, (button) ->
                this.menu.setSortType(((GuiButtonSpellSort) button).sortType)));
        this.addRenderableWidget(sortButtons[2] = new GuiButtonSpellSort(3, this.leftPos - 18, this.topPos + 8, ISpellSortable.SortType.ALPHABETICAL, menu, this, (button) ->
                this.menu.setSortType(((GuiButtonSpellSort) button).sortType)));

        // Not used...
        this.searchField = new EditBox(this.font, this.leftPos - 113, this.topPos + 22, 104, this.font.lineHeight, Component.translatable("ebwizardry.searchbox"));
        this.searchField.setMaxLength(50);
        this.searchField.setBordered(false);
        this.searchField.setVisible(true);
        this.searchField.setTextColor(16777215);
        this.searchField.setCanLoseFocus(false);
        this.searchField.setFocused(true);

        this.tooltipElements.clear();
        this.tooltipElements.add(new TooltipElementText.TooltipElementItemName(Style.EMPTY.withColor(ChatFormatting.WHITE), EBClientConstants.LINE_SPACING_WIDE));
        this.tooltipElements.add(new TooltipElementText.TooltipElementManaReadout(EBClientConstants.LINE_SPACING_WIDE));

        this.tooltipElements.add(new TooltipElementProgressionBar(imageHeight, EBClientConstants.LINE_SPACING_WIDE));
        this.tooltipElements.add(new TooltipElementSpellList(EBClientConstants.LINE_SPACING_WIDE, generateSpellEntries(8)));
        //this.tooltipElements.add(new TooltipElementUpgradeList(this, LINE_SPACING_WIDE));
    }

    @Override
    protected void containerTick() {
        if (animationTimer > 0) animationTimer--;
        if (menu.needsRefresh) {
            menu.refreshBookshelfSlots();
            menu.needsRefresh = false;
        }
        if (searchBarHoverTime > 0 && searchBarHoverTime < EBClientConstants.SEARCH_TOOLTIP_HOVER_TIME) searchBarHoverTime++;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        boolean mouseHeld = this.minecraft.mouseHandler.isLeftPressed();

        if (!scrolling && mouseHeld && getMaxScrollRows() > 0 && isHovering(EBClientConstants.SCROLL_BAR_LEFT, EBClientConstants.SCROLL_BAR_TOP, EBClientConstants.SCROLL_BAR_WIDTH, EBClientConstants.SCROLL_BAR_HEIGHT, mouseX, mouseY)) {
            scrolling = true;
        }

        if (!mouseHeld || getMaxScrollRows() == 0) scrolling = false;

        if (scrolling) {
            scroll = Mth.clamp((float) (mouseY - EBClientConstants.SCROLL_BAR_TOP - EBClientConstants.SCROLL_HANDLE_HEIGHT / 2 - topPos) / (EBClientConstants.SCROLL_BAR_HEIGHT - EBClientConstants.SCROLL_HANDLE_HEIGHT), 0, 1);
            menu.scrollTo((int) (getMaxScrollRows() * scroll + 0.5f));
        }

        Slot centreSlot = this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT);

        imageWidth = EBClientConstants.MAIN_GUI_WIDTH;
        leftPos = (this.width - EBClientConstants.MAIN_GUI_WIDTH) / 2;

        if (centreSlot.hasItem() && centreSlot.getItem().getItem() instanceof IWorkbenchItem && ((IWorkbenchItem) centreSlot.getItem().getItem()).showTooltip(centreSlot.getItem())) {
            imageWidth += EBClientConstants.TOOLTIP_WIDTH;
        }

        if (menu.hasBookshelves()) {
            imageWidth += EBClientConstants.BOOKSHELF_UI_WIDTH;
            leftPos -= EBClientConstants.BOOKSHELF_UI_WIDTH;
        }

        this.applyBtn.active = centreSlot.hasItem();
        this.clearBtn.active = centreSlot.hasItem() && centreSlot.getItem().getItem() instanceof IWorkbenchItem && ((IWorkbenchItem) centreSlot.getItem().getItem()).isClearable();
        for (AbstractWidget button : this.sortButtons) button.visible = menu.hasBookshelves();
        this.searchField.setVisible(menu.hasBookshelves());


        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1,1,1, 1);
        RenderSystem.setShaderTexture(0, EBClientConstants.TEXTURE);
        int left = this.menu.hasBookshelves() ? leftPos + EBClientConstants.BOOKSHELF_UI_WIDTH : leftPos;
        int top = topPos;

        //Gray background
        DrawingUtils.drawTexturedRect(left + EBClientConstants.RUNE_LEFT, top + EBClientConstants.RUNE_TOP, EBClientConstants.MAIN_GUI_WIDTH + EBClientConstants.TOOLTIP_WIDTH, 0,
                EBClientConstants.RUNE_WIDTH, EBClientConstants.RUNE_HEIGHT, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);

        //Yellow 'halo'
        if (animationTimer > 0) {
            guiGraphics.pose().pushPose();

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

            int x = left + EBClientConstants.RUNE_LEFT + EBClientConstants.RUNE_WIDTH / 2;
            int y = top + EBClientConstants.RUNE_TOP + EBClientConstants.RUNE_HEIGHT / 2;

            float scale = (animationTimer + partialTick) / EBClientConstants.ANIMATION_DURATION;
            scale = (float) (1 - Math.pow(1 - scale, 1.4f));
            guiGraphics.pose().scale(scale, scale, 1);
            guiGraphics.pose().translate(x/scale, y/scale, 0);

            DrawingUtils.drawTexturedRectF(guiGraphics.pose(), (float) -EBClientConstants.HALO_DIAMETER /2, (float) -EBClientConstants.HALO_DIAMETER /2,
                    EBClientConstants.MAIN_GUI_WIDTH + EBClientConstants.TOOLTIP_WIDTH, EBClientConstants.RUNE_HEIGHT,
                    EBClientConstants.HALO_DIAMETER, EBClientConstants.HALO_DIAMETER, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);

            RenderSystem.disableBlend();
            guiGraphics.pose().popPose();
        }

        //Main Inventory
        DrawingUtils.drawTexturedRect(left, top, 0, 0, EBClientConstants.MAIN_GUI_WIDTH, this.imageHeight, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT); //166 was old ySize

        float opacity = (animationTimer + partialTick)/ EBClientConstants.ANIMATION_DURATION;

        // Spell book slots (always use guiLeft and guiTop here regardless of bookshelf UI visibility
        for (int i = 0; i < ArcaneWorkbenchMenu.CRYSTAL_SLOT; i++) {
            Slot slot = this.menu.getSlot(i);
            if (slot.x >= 0 && slot.y >= 0) {
                DrawingUtils.drawTexturedRect(leftPos + slot.x - 10, topPos + slot.y - 10, 0, 220, 36, 36, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);

                if (animationTimer > 0 && slot.hasItem()) {
                    guiGraphics.pose().pushPose();
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                    RenderSystem.setShaderColor(1, 1, 1, opacity);

                    DrawingUtils.drawTexturedRect(leftPos + slot.x - 10, topPos + slot.y - 10, 36, 220, 36, 36, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);

                    RenderSystem.setShaderColor(1, 1, 1, 1);
                    RenderSystem.disableBlend();
                    guiGraphics.pose().popPose();
                }
            }
        }

        // Crystal + upgrade slot animations
        if (animationTimer > 0) {
            Slot crystals = this.menu.getSlot(ArcaneWorkbenchMenu.CRYSTAL_SLOT);
            Slot upgrades = this.menu.getSlot(ArcaneWorkbenchMenu.UPGRADE_SLOT);

            if (crystals.hasItem()) {
                guiGraphics.pose().pushPose();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1, 1, 1, opacity);

                DrawingUtils.drawTexturedRect(leftPos + crystals.x - 8, topPos + crystals.y - 8,
                        EBClientConstants.MAIN_GUI_WIDTH + EBClientConstants.TOOLTIP_WIDTH + EBClientConstants.RUNE_WIDTH, 0,
                        32, 32, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);

                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
                guiGraphics.pose().popPose();
            }

            if (upgrades.hasItem()) {
                guiGraphics.pose().pushPose();
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderColor(1, 1, 1, opacity);

                DrawingUtils.drawTexturedRect(leftPos + upgrades.x - 8, topPos + upgrades.y - 8,
                        EBClientConstants.MAIN_GUI_WIDTH + EBClientConstants.TOOLTIP_WIDTH + EBClientConstants.RUNE_WIDTH, 0, 32, 32,
                        EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);

                RenderSystem.setShaderColor(1, 1, 1, 1);
                RenderSystem.disableBlend();
                guiGraphics.pose().popPose();
            }
        }


        if (menu.hasBookshelves()) {
            DrawingUtils.drawTexturedRect(left - EBClientConstants.BOOKSHELF_UI_WIDTH, top, 0, 256, EBClientConstants.BOOKSHELF_UI_WIDTH, imageHeight, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);
            DrawingUtils.drawTexturedRect(left - EBClientConstants.BOOKSHELF_UI_WIDTH + EBClientConstants.SCROLL_BAR_LEFT, top + EBClientConstants.SCROLL_BAR_TOP + (int) (scroll * (EBClientConstants.SCROLL_BAR_HEIGHT - EBClientConstants.SCROLL_HANDLE_HEIGHT) + 0.5f), getMaxScrollRows() > 0 ? 0 : EBClientConstants.SCROLL_BAR_WIDTH, 476, EBClientConstants.SCROLL_BAR_WIDTH, EBClientConstants.SCROLL_HANDLE_HEIGHT, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);
        }

        //Render rune
        if (this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).hasItem()) {
            ItemStack stack = this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).getItem();

            if (!(stack.getItem() instanceof IWorkbenchItem)) {
                EBLogger.warn("Invalid item in central slot of arcane workbench, how did that get there?!");
                return;
            }

            if (((IWorkbenchItem) stack.getItem()).showTooltip(stack)) {
                int tooltipHeight = tooltipElements.stream().mapToInt(e -> e.getTotalHeight(stack)).sum() - tooltipElements.get(tooltipElements.size() - 1).spaceAfter;

                DrawingUtils.drawTexturedRect(left + EBClientConstants.MAIN_GUI_WIDTH, top, EBClientConstants.MAIN_GUI_WIDTH, 0, EBClientConstants.TOOLTIP_WIDTH, EBClientConstants.TOOLTIP_BORDER + tooltipHeight, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);
                DrawingUtils.drawTexturedRect(left + EBClientConstants.MAIN_GUI_WIDTH, top + EBClientConstants.TOOLTIP_BORDER + tooltipHeight, EBClientConstants.MAIN_GUI_WIDTH, imageHeight - EBClientConstants.TOOLTIP_BORDER, EBClientConstants.TOOLTIP_WIDTH, EBClientConstants.TOOLTIP_BORDER, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);

                int x = left + EBClientConstants.MAIN_GUI_WIDTH + EBClientConstants.TOOLTIP_BORDER;
                int y = top + EBClientConstants.TOOLTIP_BORDER;

                for (TooltipElement element : this.tooltipElements) {
                    y = element.drawBackgroundLayer(guiGraphics, x, y, stack, partialTick, mouseX, mouseY);
                }
            }
        }


        this.searchField.render(guiGraphics, mouseX, mouseY, partialTick);

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, EBClientConstants.TEXTURE);
        RenderSystem.disableBlend();
    }


    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);

        int left = menu.hasBookshelves() ? EBClientConstants.BOOKSHELF_UI_WIDTH : 0;

        guiGraphics.drawString(this.font, getTitle(), left + 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventory.getName().getString(), left + 8, imageHeight - 96 + 2, 4210752, false);

        if (menu.hasBookshelves()) {
            guiGraphics.drawString(this.font, "container." + WizardryMainMod.MOD_ID + ".arcane_workbench.bookshelves",
                    left + 8, 6, 4210752, false);
        }

        if (this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).hasItem()) {
            ItemStack stack = this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).getItem();

            if (!(stack.getItem() instanceof IWorkbenchItem)) {
                EBLogger.warn("Invalid item in central slot of arcane workbench, how did that get there?!");
                return;
            }

            if (((IWorkbenchItem) stack.getItem()).showTooltip(stack)) {
                int x = left + EBClientConstants.MAIN_GUI_WIDTH + EBClientConstants.TOOLTIP_BORDER;
                int y = EBClientConstants.TOOLTIP_BORDER;

                for (TooltipElement element : this.tooltipElements) {
                    y = element.drawForegroundLayer(guiGraphics, x, y, stack, mouseX, mouseY);
                }
            }
        }

        if (isHovering(searchField.getX(), searchField.getY(), searchField.getWidth(), searchField.getHeight(), mouseX + leftPos, mouseY + topPos)) {
            if (searchBarHoverTime == 0) {
                searchBarHoverTime++;
            } else if (searchBarHoverTime == EBClientConstants.SEARCH_TOOLTIP_HOVER_TIME) {
                renderTooltip(guiGraphics, mouseX - leftPos, mouseY - topPos);
            }
        } else {
            searchBarHoverTime = 0;
        }

    }

    private int getMaxScrollRows() {
        return Math.max(0, Mth.ceil((float) menu.getActiveBookshelfSlots().size() / ArcaneWorkbenchMenu.BOOKSHELF_SLOTS_X) - ArcaneWorkbenchMenu.BOOKSHELF_SLOTS_Y);
    }

    @Override
    protected void slotClicked(@NotNull Slot slot, int slotId, int mouseButton, @NotNull ClickType type) {
        searchNeedsClearing = true;

        if (slot instanceof SlotBookList slotBookList && slotBookList.hasDelegate() && playerInventory.getSelected().isEmpty()) {
            Slot virtualSlot = slotBookList.getDelegate();
            super.slotClicked(virtualSlot, virtualSlot.index, mouseButton, type);
        } else {
            super.slotClicked(slot, slotId, mouseButton, type);
        }

        menu.updateActiveBookshelfSlots();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int scrollDist = -modifiers;

        if (scrollDist != 0 && getMaxScrollRows() > 0) {
            if (scrollDist > 0) this.scroll += 1f / getMaxScrollRows();
            if (scrollDist < 0) this.scroll -= 1f / getMaxScrollRows();

            scroll = Mth.clamp(scroll, 0, 1);

            menu.scrollTo((int) (scroll * getMaxScrollRows() + 0.5f));
        }

        if (this.searchNeedsClearing) {
            this.searchNeedsClearing = false;
            this.searchField.insertText("");
        }

        if (this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
            menu.setSearchText(searchField.getMessage().getString().toLowerCase(Locale.ROOT));
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        return true;
    }

    private TooltipElement[] generateSpellEntries(int count) {
        TooltipElement[] entries = new TooltipElement[count];
        for (int i = 0; i < count; i++) entries[i] = new TooltipElementSpellEntry(this, i);
        return entries;
    }

    public @NotNull ArcaneWorkbenchMenu getMenu() {
        return menu;
    }
}
