package com.binaris.wizardry.client.gui.screens;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.RegistryUtils;
import com.binaris.wizardry.content.block.LecternBookshelfUtils;
import com.binaris.wizardry.content.blockentity.ArcaneWorkbenchBlockEntity;
import com.binaris.wizardry.content.item.SpellBookItem;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.client.gui.button.GuiButtonApply;
import com.binaris.wizardry.client.gui.button.GuiButtonClear;
import com.binaris.wizardry.client.gui.elements.*;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.core.networking.c2s.ControlInputPacketC2S;
import com.binaris.wizardry.core.networking.c2s.WorkbenchStoreBookPacketC2S;
import com.binaris.wizardry.core.networking.c2s.WorkbenchTakeBookPacketC2S;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/// Client-side GUI screen for the Arcane Workbench, where all rendering, button logic, tooltips, and animations are
/// handled. Delegates most logic to the associated [ArcaneWorkbenchMenu].
public class ArcaneWorkbenchScreen extends AbstractContainerScreen<ArcaneWorkbenchMenu> {
    public static final int MAIN_GUI_WIDTH = 176;
    public static final int RUNE_LEFT = 38;
    public static final int RUNE_TOP = 22;
    public static final int RUNE_WIDTH = 100;
    public static final int RUNE_HEIGHT = 100;
    public static final int HALO_DIAMETER = 156;
    public static final int TEXTURE_WIDTH = 512;
    public static final int TEXTURE_HEIGHT = 512;

    public static final int TOOLTIP_WIDTH = 144;
    public static final int TOOLTIP_BORDER = 6;
    public static final int PROGRESSION_BAR_WIDTH = 131;
    public static final int PROGRESSION_BAR_HEIGHT = 3;

    public static final ResourceLocation ARCANE_WORKBENCH_CONTAINER_TEXTURE = WizardryMainMod.location("textures/gui/container/arcane_workbench.png");
    private static final ResourceLocation SORT_TEXTURE = WizardryMainMod.location("textures/gui/container/spell_sort_buttons.png");

    public static final int LINE_SPACING_WIDE = 5;
    public static final int LINE_SPACING_NARROW = 1;
    public static final int ANIMATION_DURATION = 20;

    public static final int BOOKSHELF_UI_WIDTH = 122;
    public static final int BOOKSHELF_SLOTS_X = 5;
    public static final int BOOKSHELF_SLOTS_Y = 10;
    private static final int SCROLL_BAR_LEFT = 102;
    private static final int SCROLL_BAR_TOP = 34;
    private static final int SCROLL_BAR_WIDTH = 12;
    private static final int SCROLL_BAR_HEIGHT = 178;
    private static final int SCROLL_HANDLE_HEIGHT = 15;
    private static final int SEARCH_TOOLTIP_HOVER_TIME = 20;

    private final Inventory playerInventory;
    private final ArcaneWorkbenchMenu menu;
    private final List<TooltipElement> tooltipElements = new ArrayList<>();
    private AbstractWidget applyBtn;
    private AbstractWidget clearBtn;
    private int animationTimer = 0;

    private final List<BookEntry> bookshelfBooks = new ArrayList<>();
    private List<BookEntry> filteredBooks = new ArrayList<>();
    private EditBox bookshelfSearchField;
    private final BookSortButton[] bookSortButtons = new BookSortButton[BookSortType.values().length];
    private BookSortType bookSortType = BookSortType.TIER;
    private boolean bookSortDescending;
    private int bookScroll;
    private int bookshelfTicks;
    private int searchHoverTime;
    private boolean scrollingBooks;
    private boolean hasBookshelves;
    private int pendingBookshelfRefreshTicks;
    private @Nullable BlockPos workbenchPos;
    private String lastBookSearch = "";

    public ArcaneWorkbenchScreen(ArcaneWorkbenchMenu menu, Inventory playerInventory, Component name) {
        super(menu, playerInventory, name);
        this.menu = menu;
        this.playerInventory = playerInventory;
        imageWidth = MAIN_GUI_WIDTH;
        imageHeight = 220;
    }

    /// Initializes the screen, positions widgets, and sets up tooltips.
    @Override
    protected void init() {
        super.init();
        // Just in case
        if (this.minecraft == null) return;
        if (this.minecraft.player == null) return;
        this.minecraft.player.containerMenu = this.menu;

        this.leftPos = (this.width - MAIN_GUI_WIDTH) / 2;
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

        int panelLeft = this.leftPos - BOOKSHELF_UI_WIDTH;

        this.bookshelfSearchField = new EditBox(this.font, panelLeft + 9, this.topPos + 22, 104,
                this.font.lineHeight, Component.empty());
        this.bookshelfSearchField.setBordered(false);
        this.bookshelfSearchField.setMaxLength(50);
        this.bookshelfSearchField.setTextColor(0xFFFFFF);
        this.bookshelfSearchField.setCanLoseFocus(false);
        this.bookshelfSearchField.setResponder(text -> {
            this.lastBookSearch = text;
            this.bookScroll = 0;
            this.updateFilteredBooks();
        });
        this.addRenderableWidget(this.bookshelfSearchField);
        this.bookshelfSearchField.setValue(this.lastBookSearch);
        this.bookshelfSearchField.setFocused(true);

        for (BookSortType type : BookSortType.values()) {
            this.bookSortButtons[type.ordinal()] = this.addRenderableWidget(new BookSortButton(
                    panelLeft + 78 + 13 * type.ordinal(), this.topPos + 8, type));
        }

        this.refreshBookshelves();
    }

    /// Renders the screen, including background, slots, tooltips, and animations.
    ///
    /// @param guiGraphics The graphics context.
    /// @param mouseX      Mouse X position.
    /// @param mouseY      Mouse Y position.
    /// @param partialTick Partial tick time.
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        Slot centreSlot = this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT);

        imageWidth = MAIN_GUI_WIDTH;
        leftPos = (this.width - MAIN_GUI_WIDTH) / 2;

        if (centreSlot.hasItem() && centreSlot.getItem().getItem() instanceof IWorkbenchItem && ((IWorkbenchItem) centreSlot.getItem().getItem()).showTooltip(centreSlot.getItem())) {
            imageWidth += TOOLTIP_WIDTH;
        }

        this.applyBtn.active = centreSlot.hasItem();
        this.clearBtn.active = centreSlot.hasItem() && centreSlot.getItem().getItem() instanceof IWorkbenchItem && ((IWorkbenchItem) centreSlot.getItem().getItem()).isClearable();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        if (!this.hasBookshelves) return;
        
        BookEntry hovered = this.getHoveredBook(mouseX, mouseY);

        if (hovered != null) {
            List<Component> lines = new ArrayList<>(hovered.stack().getTooltipLines(this.minecraft.player,
                    this.minecraft.options.advancedItemTooltips
                            ? net.minecraft.world.item.TooltipFlag.Default.ADVANCED
                            : net.minecraft.world.item.TooltipFlag.Default.NORMAL));
            lines.add(Component.translatable("container.ebwizardry.arcane_workbench.pickup_tooltip")
                    .withStyle(ChatFormatting.DARK_GRAY));

            List<net.minecraft.util.FormattedCharSequence> visualLines = new ArrayList<>(lines.size());
            for (Component line : lines) visualLines.add(line.getVisualOrderText());
            guiGraphics.renderTooltip(this.font, visualLines, mouseX, mouseY);
        } else if (this.bookshelfSearchField != null && this.bookshelfSearchField.isVisible()
                && mouseX >= this.bookshelfSearchField.getX()
                && mouseX < this.bookshelfSearchField.getX() + this.bookshelfSearchField.getWidth()
                && mouseY >= this.bookshelfSearchField.getY()
                && mouseY < this.bookshelfSearchField.getY() + this.bookshelfSearchField.getHeight()) {
            if (++this.searchHoverTime > SEARCH_TOOLTIP_HOVER_TIME) {
                guiGraphics.renderTooltip(this.font,
                        Component.translatable("container.ebwizardry.arcane_workbench.search_tooltip"),
                        mouseX, mouseY);
            }
        } else {
            this.searchHoverTime = 0;
        }

        for (BookSortButton button : this.bookSortButtons) {
            if (button != null && button.visible && button.isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(this.font,
                        Component.translatable("container.ebwizardry.arcane_workbench.sort_" + button.sortType.name),
                        mouseX, mouseY);
            }
        }
    }

    /// Renders the background layer, including slot highlights and animations.
    ///
    /// @param guiGraphics The graphics context.
    /// @param partialTick Partial tick time.
    /// @param mouseX      Mouse X position.
    /// @param mouseY      Mouse Y position.
    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        ResourceLocation texture = ARCANE_WORKBENCH_CONTAINER_TEXTURE;
        int left = leftPos;
        int top = topPos;

        // Gray background
        guiGraphics.blit(texture, left + RUNE_LEFT, top + RUNE_TOP, MAIN_GUI_WIDTH + TOOLTIP_WIDTH, 0,
                RUNE_WIDTH, RUNE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        // Yellow 'halo'
        if (animationTimer > 0) {
            float scale = (animationTimer + partialTick) / ANIMATION_DURATION;
            scale = (float) (1 - Math.pow(1 - scale, 1.4f));

            int x = left + RUNE_LEFT + RUNE_WIDTH / 2;
            int y = top + RUNE_TOP + RUNE_HEIGHT / 2;
            float halfDiameter = HALO_DIAMETER / 2f;

            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            pose.scale(scale, scale, 1);
            pose.translate(x / scale, y / scale, 0);
            guiGraphics.blit(texture, (int) -halfDiameter, (int) -halfDiameter, MAIN_GUI_WIDTH + TOOLTIP_WIDTH,
                    RUNE_HEIGHT, HALO_DIAMETER, HALO_DIAMETER, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            RenderSystem.disableBlend();
            pose.popPose();
        }

        // Main inventory
        guiGraphics.blit(texture, left, top, 0, 0, MAIN_GUI_WIDTH, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        float opacity = (animationTimer + partialTick) / ANIMATION_DURATION;

        // Spell book slots
        for (int i = 0; i < ArcaneWorkbenchMenu.CRYSTAL_SLOT; i++) {
            Slot slot = this.menu.getSlot(i);
            if (slot.x < 0 || slot.y < 0) continue;

            guiGraphics.blit(texture, leftPos + slot.x - 10, topPos + slot.y - 10, 0, 220, 36, 36, TEXTURE_WIDTH, TEXTURE_HEIGHT);

            if (animationTimer > 0 && slot.hasItem()) {
                blitWithOpacity(guiGraphics, texture, leftPos + slot.x - 10, topPos + slot.y - 10, 36, 220, 36, 36, opacity);
            }
        }

        // Crystal + upgrade slot animations
        if (animationTimer > 0) {
            int glowU = MAIN_GUI_WIDTH + TOOLTIP_WIDTH + RUNE_WIDTH;

            Slot crystals = this.menu.getSlot(ArcaneWorkbenchMenu.CRYSTAL_SLOT);
            if (crystals.hasItem()) {
                blitWithOpacity(guiGraphics, texture,
                        leftPos + crystals.x - 8, topPos + crystals.y - 8,
                        glowU, 0, 32, 32, opacity);
            }

            Slot upgrades = this.menu.getSlot(ArcaneWorkbenchMenu.UPGRADE_SLOT);
            if (upgrades.hasItem()) {
                blitWithOpacity(guiGraphics, texture,
                        leftPos + upgrades.x - 8, topPos + upgrades.y - 8,
                        glowU, 0, 32, 32, opacity);
            }
        }

        if (this.hasBookshelves) this.drawBookshelfPanel(guiGraphics, left - BOOKSHELF_UI_WIDTH, top);

        // Render rune tooltip panel
        if (this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).hasItem()) {
            ItemStack stack = this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).getItem();

            if (!(stack.getItem() instanceof IWorkbenchItem workbenchItem)) {
                EBLogger.warn("Invalid item in central slot of arcane workbench, how did that get there?!");
                return;
            }

            if (workbenchItem.showTooltip(stack)) {
                int tooltipHeight = tooltipElements.stream().mapToInt(e -> e.getTotalHeight(stack)).sum()
                        - tooltipElements.get(tooltipElements.size() - 1).spaceAfter;

                guiGraphics.blit(texture, left + MAIN_GUI_WIDTH, top, MAIN_GUI_WIDTH, 0, TOOLTIP_WIDTH,
                        TOOLTIP_BORDER + tooltipHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);

                guiGraphics.blit(texture, left + MAIN_GUI_WIDTH, top + TOOLTIP_BORDER + tooltipHeight, MAIN_GUI_WIDTH, imageHeight - TOOLTIP_BORDER,
                        TOOLTIP_WIDTH, TOOLTIP_BORDER, TEXTURE_WIDTH, TEXTURE_HEIGHT);

                int x = left + MAIN_GUI_WIDTH + TOOLTIP_BORDER;
                int y = top + TOOLTIP_BORDER;
                for (TooltipElement element : this.tooltipElements) {
                    y = element.drawBackgroundLayer(guiGraphics, x, y, stack, partialTick, mouseX, mouseY);
                }
            }
        }

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    private void drawBookshelfPanel(GuiGraphics guiGraphics, int panelLeft, int top) {
        ResourceLocation texture = ARCANE_WORKBENCH_CONTAINER_TEXTURE;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(texture, panelLeft, top, 0, 256, BOOKSHELF_UI_WIDTH, this.imageHeight,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);

        int start = this.bookScroll * BOOKSHELF_SLOTS_X;
        int end = Math.min(start + BOOKSHELF_SLOTS_X * BOOKSHELF_SLOTS_Y, this.filteredBooks.size());

        for (int i = start; i < end; i++) {
            int visible = i - start;
            int x = panelLeft + 8 + (visible % BOOKSHELF_SLOTS_X) * 18;
            int y = top + 34 + (visible / BOOKSHELF_SLOTS_X) * 18;
            ItemStack stack = this.filteredBooks.get(i).stack();
            guiGraphics.renderItem(stack, x, y);
            guiGraphics.renderItemDecorations(this.font, stack, x, y);
        }

        int maxScroll = this.getMaxBookScrollRows();
        int handleY = top + SCROLL_BAR_TOP + (maxScroll > 0
                ? Math.round((this.bookScroll / (float) maxScroll) * (SCROLL_BAR_HEIGHT - SCROLL_HANDLE_HEIGHT))
                : 0);
        int handleU = maxScroll > 0 ? 0 : SCROLL_BAR_WIDTH;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(texture, panelLeft + SCROLL_BAR_LEFT, handleY, handleU, 476,
                SCROLL_BAR_WIDTH, SCROLL_HANDLE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private void blitWithOpacity(GuiGraphics guiGraphics, ResourceLocation texture, int x, int y, int u, int v, int w, int h, float opacity) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1, 1, 1, opacity);
        guiGraphics.blit(texture, x, y, u, v, w, h, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    /// Renders the foreground labels, including the title and inventory name.
    /// Also draws tooltip foreground if needed.
    ///
    /// @param guiGraphics The graphics context.
    /// @param mouseX      Mouse X position.
    /// @param mouseY      Mouse Y position.
    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        int left = 0;

        guiGraphics.drawString(this.font, getTitle(), left + 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventory.getName().getString(), left + 8, imageHeight - 96 + 2, 4210752, false);

        if (this.hasBookshelves) {
            guiGraphics.drawString(this.font,
                    Component.translatable("container.ebwizardry.arcane_workbench.bookshelves"),
                    -BOOKSHELF_UI_WIDTH + 8, 6, 4210752, false);
        }

        if (!this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).hasItem()) return;
        ItemStack stack = this.menu.getSlot(ArcaneWorkbenchMenu.CENTRE_SLOT).getItem();

        if (!(stack.getItem() instanceof IWorkbenchItem)) {
            EBLogger.warn("Invalid item in central slot of arcane workbench, how did that get there?!");
            return;
        }

        if (!((IWorkbenchItem) stack.getItem()).showTooltip(stack)) return;
        int x = left + MAIN_GUI_WIDTH + TOOLTIP_BORDER;
        int y = TOOLTIP_BORDER;

        for (TooltipElement element : this.tooltipElements) {
            y = element.drawForegroundLayer(guiGraphics, x, y, stack, mouseX, mouseY);
        }
    }

    /// Called every tick to update animation and refresh state.
    @Override
    protected void containerTick() {
        if (animationTimer > 0) animationTimer--;
        if (menu.needsRefresh) menu.needsRefresh = false;

        if (this.pendingBookshelfRefreshTicks > 0) {
            this.pendingBookshelfRefreshTicks--;
            this.refreshBookshelves();
        } else if (++this.bookshelfTicks % 20 == 0) {
            this.refreshBookshelves();
        }
    }

    private void refreshBookshelves() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return;

        this.workbenchPos = this.findWorkbenchPos();
        this.menu.setClientWorkbenchPos(this.workbenchPos);
        this.bookshelfBooks.clear();
        this.hasBookshelves = false;

        if (this.workbenchPos != null) {
            List<BlockPos> shelves = LecternBookshelfUtils.findBookshelfPositions(this.minecraft.level, this.workbenchPos);
            this.hasBookshelves = !shelves.isEmpty();

            for (BlockPos pos : shelves) {
                Container bookshelf = LecternBookshelfUtils.getBookshelf(this.minecraft.level, pos);
                if (bookshelf == null) continue;

                for (int slot = 0; slot < bookshelf.getContainerSize(); slot++) {
                    ItemStack stack = bookshelf.getItem(slot);
                    if (!(stack.getItem() instanceof SpellBookItem)) continue;

                    Spell spell = RegistryUtils.getSpell(stack);
                    if (spell == Spells.NONE) continue;

                    this.bookshelfBooks.add(new BookEntry(pos.immutable(), slot, stack.copy(), spell));
                }
            }
        }

        this.updateFilteredBooks();
        this.updateBookshelfWidgets();
    }

    @Nullable
    private BlockPos findWorkbenchPos() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) return null;

        BlockPos centre = this.minecraft.player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(centre.offset(-6, -4, -6), centre.offset(6, 4, 6))) {
            if (this.minecraft.level.getBlockEntity(pos) instanceof ArcaneWorkbenchBlockEntity workbench
                    && workbench.stillValid(this.minecraft.player)) {
                return workbench.getBlockPos();
            }
        }

        return null;
    }

    private void updateFilteredBooks() {
        String query = this.bookshelfSearchField == null ? ""
                : this.bookshelfSearchField.getValue().trim().toLowerCase(Locale.ROOT);
        Comparator<Spell> comparator = this.bookSortDescending
                ? this.bookSortType.comparator.reversed() : this.bookSortType.comparator;

        this.filteredBooks = this.bookshelfBooks.stream()
                .filter(entry -> query.isEmpty()
                        || entry.spell().getDescriptionFormatted().getString().toLowerCase(Locale.ROOT).contains(query)
                        || entry.spell().getLocation().getPath().toLowerCase(Locale.ROOT).contains(query))
                .sorted((a, b) -> comparator.compare(a.spell(), b.spell()))
                .collect(Collectors.toCollection(ArrayList::new));

        this.bookScroll = Mth.clamp(this.bookScroll, 0, this.getMaxBookScrollRows());
    }

    private int getMaxBookScrollRows() {
        return Math.max(0, Mth.ceil(this.filteredBooks.size() / (float) BOOKSHELF_SLOTS_X) - BOOKSHELF_SLOTS_Y);
    }

    private void updateBookshelfWidgets() {
        if (this.bookshelfSearchField != null) {
            this.bookshelfSearchField.setVisible(this.hasBookshelves);
            this.bookshelfSearchField.setFocused(this.hasBookshelves);
        }

        for (BookSortButton button : this.bookSortButtons) {
            if (button != null) button.visible = this.hasBookshelves;
        }
    }

    private void toggleBookSort(BookSortType type) {
        if (this.bookSortType == type) {
            this.bookSortDescending = !this.bookSortDescending;
        } else {
            this.bookSortType = type;
            this.bookSortDescending = false;
        }

        this.updateFilteredBooks();
    }

    @Nullable
    private BookEntry getHoveredBook(int mouseX, int mouseY) {
        if (!this.hasBookshelves) return null;

        int panelLeft = this.leftPos - BOOKSHELF_UI_WIDTH;
        int panelTop = this.topPos;
        int start = this.bookScroll * BOOKSHELF_SLOTS_X;
        int end = Math.min(start + BOOKSHELF_SLOTS_X * BOOKSHELF_SLOTS_Y, this.filteredBooks.size());

        for (int i = start; i < end; i++) {
            int visible = i - start;
            int x = panelLeft + 8 + (visible % BOOKSHELF_SLOTS_X) * 18;
            int y = panelTop + 34 + (visible / BOOKSHELF_SLOTS_X) * 18;

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                return this.filteredBooks.get(i);
            }
        }

        return null;
    }

    private boolean isHoveringBookshelfPanel(double mouseX, double mouseY) {
        int panelLeft = this.leftPos - BOOKSHELF_UI_WIDTH;
        return mouseX >= panelLeft && mouseX < panelLeft + BOOKSHELF_UI_WIDTH
                && mouseY >= this.topPos && mouseY < this.topPos + this.imageHeight;
    }

    private boolean isHoveringBookshelfSlots(double mouseX, double mouseY) {
        int panelLeft = this.leftPos - BOOKSHELF_UI_WIDTH;
        return mouseX >= panelLeft + 8 && mouseX < panelLeft + 8 + BOOKSHELF_SLOTS_X * 18
                && mouseY >= this.topPos + 34 && mouseY < this.topPos + 34 + BOOKSHELF_SLOTS_Y * 18;
    }

    private void updateBookScrollFromMouse(int mouseY) {
        int maxScroll = this.getMaxBookScrollRows();
        if (maxScroll <= 0) {
            this.bookScroll = 0;
            return;
        }

        float fraction = Mth.clamp((mouseY - (this.topPos + SCROLL_BAR_TOP + SCROLL_HANDLE_HEIGHT / 2f))
                / (float) (SCROLL_BAR_HEIGHT - SCROLL_HANDLE_HEIGHT), 0, 1);
        this.bookScroll = Math.round(fraction * maxScroll);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.bookshelfSearchField != null && this.bookshelfSearchField.isVisible()
                && this.bookshelfSearchField.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (this.hasBookshelves && button == 0) {
            int panelLeft = this.leftPos - BOOKSHELF_UI_WIDTH;
            int panelTop = this.topPos;

            if (mouseX >= panelLeft + SCROLL_BAR_LEFT && mouseX < panelLeft + SCROLL_BAR_LEFT + SCROLL_BAR_WIDTH
                    && mouseY >= panelTop + SCROLL_BAR_TOP && mouseY < panelTop + SCROLL_BAR_TOP + SCROLL_BAR_HEIGHT) {
                this.scrollingBooks = true;
                this.updateBookScrollFromMouse((int) mouseY);
                return true;
            }

            BookEntry hovered = this.getHoveredBook((int) mouseX, (int) mouseY);
            if (this.isHoveringBookshelfSlots(mouseX, mouseY) && !this.menu.getCarried().isEmpty()) {
                if (this.menu.getCarried().getItem() instanceof SpellBookItem) {
                    WorkbenchStoreBookPacketC2S packet = hovered == null
                            ? new WorkbenchStoreBookPacketC2S()
                            : new WorkbenchStoreBookPacketC2S(hovered.shelfPos(), hovered.slot());
                    Services.NETWORK_HELPER.sendToServer(packet);
                    this.pendingBookshelfRefreshTicks = 10;
                }
                // The side panel is outside the vanilla menu bounds. Always consume this click so an unsupported item
                // on the cursor is not interpreted as a click outside the inventory and thrown onto the ground.
                return true;
            }

            if (hovered != null) {
                Services.NETWORK_HELPER.sendToServer(new WorkbenchTakeBookPacketC2S(
                        hovered.shelfPos(), hovered.slot(), Screen.hasShiftDown()));
                // Remove it immediately; the next shelf sync will restore it if the server rejected the click.
                this.bookshelfBooks.remove(hovered);
                this.updateFilteredBooks();
                this.pendingBookshelfRefreshTicks = 10;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.bookshelfSearchField != null && this.bookshelfSearchField.isVisible()
                && this.bookshelfSearchField.isFocused()
                && this.bookshelfSearchField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (this.minecraft != null
                && (this.bookshelfSearchField == null || !this.bookshelfSearchField.isFocused())
                && this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.bookshelfSearchField != null && this.bookshelfSearchField.isVisible()
                && this.bookshelfSearchField.isFocused()
                && this.bookshelfSearchField.charTyped(codePoint, modifiers)) {
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        return !this.isHoveringBookshelfPanel(mouseX, mouseY)
                && super.hasClickedOutside(mouseX, mouseY, left, top, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) this.scrollingBooks = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrollingBooks) {
            this.updateBookScrollFromMouse((int) mouseY);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (this.hasBookshelves && this.isHoveringBookshelfPanel(mouseX, mouseY)) {
            int maxScroll = this.getMaxBookScrollRows();
            if (maxScroll > 0) {
                this.bookScroll = Mth.clamp(this.bookScroll - (int) Math.signum(delta), 0, maxScroll);
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    /// Generates the array of spell entry tooltip elements. This is done in a separate method to keep the constructor
    /// cleaner and allow for easy overriding.
    ///
    /// @return An array of eight [TooltipElementSpellEntry]s.
    private TooltipElement[] generateSpellEntries() {
        TooltipElement[] entries = new TooltipElement[8];
        for (int i = 0; i < 8; i++) entries[i] = new TooltipElementSpellEntry(this, i);
        return entries;
    }

    public @NotNull ArcaneWorkbenchMenu getMenu() {
        return menu;
    }

    private record BookEntry(BlockPos shelfPos, int slot, ItemStack stack, Spell spell) {
    }

    private enum BookSortType {
        TIER("tier", Comparator.comparingInt((Spell spell) -> spell.getTier().getLevel())),
        ELEMENT("element", Comparator.comparing((Spell spell) -> spell.getElement().getLocation().toString())
                .thenComparingInt(spell -> spell.getTier().getLevel())),
        ALPHABETICAL("alphabetical", Comparator.comparing(spell -> spell.getLocation().getPath()));

        private final String name;
        private final Comparator<Spell> comparator;

        BookSortType(String name, Comparator<Spell> comparator) {
            this.name = name;
            this.comparator = comparator;
        }
    }

    private class BookSortButton extends Button {
        private final BookSortType sortType;

        private BookSortButton(int x, int y, BookSortType sortType) {
            super(x, y, 10, 10, Component.empty(), button -> toggleBookSort(sortType), Button.DEFAULT_NARRATION);
            this.sortType = sortType;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int u = 0;
            if (bookSortType == this.sortType) {
                u = bookSortDescending ? 20 : 10;
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            guiGraphics.blit(SORT_TEXTURE, this.getX(), this.getY(), u, this.sortType.ordinal() * 10,
                    10, 10, 32, 32);
        }
    }
}
