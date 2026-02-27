package com.binaris.wizardry.client.gui.screens;

import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.api.content.util.DrawingUtils;
import com.binaris.wizardry.client.EBClientConstants;
import com.binaris.wizardry.client.gui.button.GuiButtonApply;
import com.binaris.wizardry.client.gui.button.GuiButtonClear;
import com.binaris.wizardry.client.gui.elements.*;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.core.networking.c2s.ControlInputPacketC2S;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.binaris.wizardry.client.EBClientConstants.LINE_SPACING_WIDE;

/**
 * Client-side GUI screen for the Arcane Workbench, where all rendering, button logic, tooltips, and animations are
 * handled. Delegates most logic to the associated {@link ArcaneWorkbenchMenu}.
 */
public class ArcaneWorkbenchScreen extends AbstractContainerScreen<ArcaneWorkbenchMenu> {
    private final Inventory playerInventory;
    private final ArcaneWorkbenchMenu menu;
    private final List<TooltipElement> tooltipElements = new ArrayList<>();
    private AbstractWidget applyBtn;
    private AbstractWidget clearBtn;
    private int animationTimer = 0;

    public ArcaneWorkbenchScreen(ArcaneWorkbenchMenu menu, Inventory playerInventory, Component name) {
        super(menu, playerInventory, name);
        this.menu = menu;
        this.playerInventory = playerInventory;
        imageWidth = EBClientConstants.MAIN_GUI_WIDTH;
        imageHeight = 220;
    }

    /**
     * Initializes the screen, positions widgets, and sets up tooltips.
     */
    @Override
    protected void init() {
        super.init();
        // Just in case
        if (this.minecraft == null) return;
        if (this.minecraft.player == null) return;
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

        this.tooltipElements.clear();
        this.tooltipElements.add(new TooltipElementText.TooltipElementItemName(Style.EMPTY.withColor(ChatFormatting.WHITE), LINE_SPACING_WIDE));
        this.tooltipElements.add(new TooltipElementText.TooltipElementManaReadout(LINE_SPACING_WIDE));

        this.tooltipElements.add(new TooltipElementProgressionBar(imageHeight, LINE_SPACING_WIDE));
        this.tooltipElements.add(new TooltipElementSpellList(LINE_SPACING_WIDE, generateSpellEntries()));
        this.tooltipElements.add(new TooltipElementUpgradeList(this, LINE_SPACING_WIDE));
    }

    /**
     * Renders the screen, including background, slots, tooltips, and animations.
     *
     * @param guiGraphics The graphics context.
     * @param mouseX      Mouse X position.
     * @param mouseY      Mouse Y position.
     * @param partialTick Partial tick time.
     */
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        Slot centreSlot = this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT);

        imageWidth = EBClientConstants.MAIN_GUI_WIDTH;
        leftPos = (this.width - EBClientConstants.MAIN_GUI_WIDTH) / 2;

        if (centreSlot.hasItem() && centreSlot.getItem().getItem() instanceof IWorkbenchItem && ((IWorkbenchItem) centreSlot.getItem().getItem()).showTooltip(centreSlot.getItem())) {
            imageWidth += EBClientConstants.TOOLTIP_WIDTH;
        }

        this.applyBtn.active = centreSlot.hasItem();
        this.clearBtn.active = centreSlot.hasItem() && centreSlot.getItem().getItem() instanceof IWorkbenchItem && ((IWorkbenchItem) centreSlot.getItem().getItem()).isClearable();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    /**
     * Renders the background layer, including slot highlights and animations.
     *
     * @param guiGraphics The graphics context.
     * @param partialTick Partial tick time.
     * @param mouseX      Mouse X position.
     * @param mouseY      Mouse Y position.
     */
    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, EBClientConstants.ARCANE_WORKBENCH_CONTAINER_TEXTURE);
        int left = leftPos;
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
            guiGraphics.pose().translate(x / scale, y / scale, 0);

            DrawingUtils.drawTexturedRectF(guiGraphics.pose(), (float) -EBClientConstants.HALO_DIAMETER / 2, (float) -EBClientConstants.HALO_DIAMETER / 2,
                    EBClientConstants.MAIN_GUI_WIDTH + EBClientConstants.TOOLTIP_WIDTH, EBClientConstants.RUNE_HEIGHT,
                    EBClientConstants.HALO_DIAMETER, EBClientConstants.HALO_DIAMETER, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT);

            RenderSystem.disableBlend();
            guiGraphics.pose().popPose();
        }

        //Main Inventory
        DrawingUtils.drawTexturedRect(left, top, 0, 0, EBClientConstants.MAIN_GUI_WIDTH, this.imageHeight, EBClientConstants.TEXTURE_WIDTH, EBClientConstants.TEXTURE_HEIGHT); //166 was old ySize

        float opacity = (animationTimer + partialTick) / EBClientConstants.ANIMATION_DURATION;

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

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, EBClientConstants.ARCANE_WORKBENCH_CONTAINER_TEXTURE);
        RenderSystem.disableBlend();
    }

    /**
     * Renders the foreground labels, including the title and inventory name.
     * Also draws tooltip foreground if needed.
     *
     * @param guiGraphics The graphics context.
     * @param mouseX      Mouse X position.
     * @param mouseY      Mouse Y position.
     */
    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        int left = 0;

        guiGraphics.drawString(this.font, getTitle(), left + 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventory.getName().getString(), left + 8, imageHeight - 96 + 2, 4210752, false);

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
    }

    /**
     * Called every tick to update animation and refresh state.
     */
    @Override
    protected void containerTick() {
        if (animationTimer > 0) animationTimer--;
        if (menu.needsRefresh) menu.needsRefresh = false;
    }

    /**
     * Generates the array of spell entry tooltip elements. This is done in a separate method to keep the constructor
     * cleaner and allow for easy overriding.
     *
     * @return An array of eight {@link TooltipElementSpellEntry}s.
     */
    private TooltipElement[] generateSpellEntries() {
        TooltipElement[] entries = new TooltipElement[8];
        for (int i = 0; i < 8; i++) entries[i] = new TooltipElementSpellEntry(this, i);
        return entries;
    }

    public @NotNull ArcaneWorkbenchMenu getMenu() {
        return menu;
    }
}
