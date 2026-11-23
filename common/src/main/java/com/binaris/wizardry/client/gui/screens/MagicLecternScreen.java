package com.binaris.wizardry.client.gui.screens;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.client.util.GlyphClientHandler;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.client.gui.elements.SpellInfoRenderer;
import com.binaris.wizardry.content.block.LecternBookshelfUtils;
import com.binaris.wizardry.content.blockentity.MagicLecternBlockEntity;
import com.binaris.wizardry.content.data.SpellGlyphData;
import com.binaris.wizardry.core.networking.c2s.LecternSpellPacketC2S;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/// The arcane encyclopedia: an index of every spell stored in the bookshelves around a lectern.
public class MagicLecternScreen extends Screen {
    private static final ResourceLocation TEXTURE = WizardryMainMod.location("textures/gui/container/lectern.png");
    private static final ResourceLocation SORT_TEXTURE = WizardryMainMod.location("textures/gui/container/spell_sort_buttons.png");

    private static final int X_SIZE = 288;
    private static final int Y_SIZE = 180;
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 512;

    private static final int SPELL_ROWS = 3;
    private static final int SPELL_COLUMNS = 3;
    private static final int SPELL_BUTTON_COUNT = SPELL_ROWS * SPELL_COLUMNS * 2;

    private static final int PAGE_BUTTON_INSET_X = 22;
    private static final int PAGE_BUTTON_INSET_Y = 13;
    private static final int PAGE_BUTTON_SPACING = 20;
    private static final int TITLE_INSET_X = 20;
    private static final int TITLE_INSET_Y = 24;
    private static final int SORT_BUTTON_INSET_X = 96;
    private static final int SORT_BUTTON_INSET_Y = 20;
    private static final int SORT_BUTTON_SPACING = 13;
    private static final int SPELL_BUTTON_INSET_X = 23;
    private static final int SPELL_BUTTON_INSET_Y = 44;
    private static final int SPELL_BUTTON_SPACING = 38;

    private final BlockPos lecternPos;

    private final List<Spell> availableSpells = new ArrayList<>();
    private List<Spell> matchingSpells = new ArrayList<>();
    private Spell currentSpell = Spells.NONE;

    private SortType sortType = SortType.TIER;
    private boolean sortDescending;
    private int currentPage;
    private int ticks;
    private int searchHoverTime;
    private boolean initialised;
    private boolean spellSent;
    private String lastSearch = "";

    private EditBox searchField;
    private TurnPageButton nextPageButton;
    private TurnPageButton prevPageButton;
    private TurnPageButton lastPageButton;
    private TurnPageButton firstPageButton;
    private TurnPageButton indexButton;
    private LocateButton locateButton;
    private final SortButton[] sortButtons = new SortButton[SortType.values().length];
    private final SpellButton[] spellButtons = new SpellButton[SPELL_BUTTON_COUNT];

    public MagicLecternScreen(BlockPos lecternPos) {
        super(Component.translatable("gui.ebwizardry.lectern.title"));
        this.lecternPos = lecternPos;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int left = (this.width - X_SIZE) / 2;
        int top = (this.height - Y_SIZE) / 2;

        int pageY = top + Y_SIZE - PAGE_BUTTON_INSET_Y - 12;
        this.nextPageButton = this.addRenderableWidget(new TurnPageButton(left + X_SIZE - PAGE_BUTTON_INSET_X - 20, pageY,
                TurnPageType.NEXT_PAGE, () -> changePage(1)));
        this.prevPageButton = this.addRenderableWidget(new TurnPageButton(left + PAGE_BUTTON_INSET_X, pageY,
                TurnPageType.PREVIOUS_PAGE, () -> changePage(-1)));
        this.lastPageButton = this.addRenderableWidget(new TurnPageButton(
                left + X_SIZE - PAGE_BUTTON_INSET_X - 20 - PAGE_BUTTON_SPACING, pageY,
                TurnPageType.NEXT_SECTION, this::lastPage));
        this.firstPageButton = this.addRenderableWidget(new TurnPageButton(left + PAGE_BUTTON_INSET_X + PAGE_BUTTON_SPACING,
                pageY, TurnPageType.PREVIOUS_SECTION, this::firstPage));
        this.indexButton = this.addRenderableWidget(new TurnPageButton(left + X_SIZE / 2 - 23, pageY,
                TurnPageType.CONTENTS, () -> {
            this.currentSpell = Spells.NONE;
            this.updateButtonVisibility();
        }));
        this.locateButton = this.addRenderableWidget(new LocateButton(left + X_SIZE / 2 - 34, pageY));

        for (SortType type : SortType.values()) {
            this.sortButtons[type.ordinal()] = this.addRenderableWidget(new SortButton(
                    left + SORT_BUTTON_INSET_X + SORT_BUTTON_SPACING * type.ordinal(),
                    top + SORT_BUTTON_INSET_Y, type));
        }

        for (int i = 0; i < SPELL_BUTTON_COUNT; i++) {
            int[] pos = spellButtonPos(i);
            this.spellButtons[i] = this.addRenderableWidget(new SpellButton(left + pos[0], top + pos[1], i));
        }

        this.searchField = new EditBox(this.font, left + 157, top + 21, 106, this.font.lineHeight, Component.empty());
        this.searchField.setBordered(false);
        this.searchField.setMaxLength(50);
        this.searchField.setTextColor(0xFFFFFF);
        this.searchField.setCanLoseFocus(false);
        this.searchField.setResponder(text -> {
            this.lastSearch = text;
            this.currentPage = 0;
            this.updateMatchingSpells();
            this.updateButtonVisibility();
        });
        this.addRenderableWidget(this.searchField);
        this.searchField.setValue(this.lastSearch);
        this.searchField.setFocused(true);

        if (!this.initialised) {
            MagicLecternBlockEntity lectern = this.getLectern();
            if (lectern != null) {
                this.currentSpell = lectern.getSpell();
                this.initialised = true;
            }
        }

        this.refreshAvailableSpells();
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.initialised) {
            MagicLecternBlockEntity lectern = this.getLectern();
            if (lectern != null) {
                this.currentSpell = lectern.getSpell();
                this.initialised = true;
                this.refreshAvailableSpells();
            }
        }

        if (++this.ticks % 40 == 0) this.refreshAvailableSpells();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int left = (this.width - X_SIZE) / 2;
        int top = (this.height - Y_SIZE) / 2;

        if (this.currentSpell == Spells.NONE) {
            this.drawIndexBackground(guiGraphics, left, top);
            this.drawHoveredSpellBackdrop(guiGraphics, mouseX, mouseY);
        } else {
            SpellInfoRenderer.drawBackground(guiGraphics, this.currentSpell, TEXTURE, left, top,
                    X_SIZE, Y_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.currentSpell == Spells.NONE) {
            this.drawIndexForeground(guiGraphics, left, top);
        } else {
            SpellInfoRenderer.drawForeground(guiGraphics, this.currentSpell, left, top);
        }

        this.drawTooltips(guiGraphics, mouseX, mouseY, left, top);
    }

    private void drawIndexBackground(GuiGraphics guiGraphics, int left, int top) {
        for (int i = 0; i < SPELL_BUTTON_COUNT; i++) {
            int index = this.currentPage * SPELL_BUTTON_COUNT + i;
            Spell spell = index < this.matchingSpells.size() ? this.matchingSpells.get(index) : Spells.NONE;
            boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, null);
            int[] pos = spellButtonPos(i);

            guiGraphics.blit(discovered ? spell.getIcon() : Spells.NONE.getIcon(),
                    left + pos[0] + 1, top + pos[1] + 1, 32, 32, 0, 0, 128, 128, 128, 128);
        }

        guiGraphics.blit(TEXTURE, left, top, 0, 256, X_SIZE, Y_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private void drawHoveredSpellBackdrop(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (SpellButton button : this.spellButtons) {
            if (!button.visible || !button.isMouseOver(mouseX, mouseY)) continue;

            int x = button.getX();
            int y = button.getY();
            int colour = 0xFFFF8900;
            guiGraphics.fill(x, y, x + 34, y + 1, colour);
            guiGraphics.fill(x, y + 33, x + 34, y + 34, colour);
            guiGraphics.fill(x, y + 1, x + 1, y + 33, colour);
            guiGraphics.fill(x + 33, y + 1, x + 34, y + 33, colour);
        }
    }

    private void drawIndexForeground(GuiGraphics guiGraphics, int left, int top) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(left + TITLE_INSET_X, top + TITLE_INSET_Y, 0);
        guiGraphics.pose().scale(0.7F, 0.7F, 1.0F);
        guiGraphics.drawString(this.font, this.title, 0, 0, 0x3F2A1D, false);
        guiGraphics.pose().popPose();
    }

    private void drawTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY, int left, int top) {
        if (this.currentSpell == Spells.NONE) {
            for (SpellButton button : this.spellButtons) {
                if (!button.visible || !button.isMouseOver(mouseX, mouseY)) continue;

                Spell spell = this.getSpellForButton(button.index);
                if (spell == null) continue;

                boolean discovered = ClientUtils.shouldDisplayDiscovered(spell, null);
                SpellGlyphData glyphData = GlyphClientHandler.INSTANCE.getGlyphData();
                Component name = discovered
                        ? spell.getDescriptionFormatted()
                        : glyphData == null ? Component.literal("???") : SpellGlyphData.getGlyphNameFormatted(spell, glyphData);
                guiGraphics.renderTooltip(discovered ? this.font : this.minecraft.fontFilterFishy, name, mouseX, mouseY);
            }

            for (SortButton button : this.sortButtons) {
                if (button != null && button.visible && button.isMouseOver(mouseX, mouseY)) {
                    guiGraphics.renderTooltip(this.font,
                            Component.translatable("container.ebwizardry.arcane_workbench.sort_" + button.sortType.name),
                            mouseX, mouseY);
                }
            }

            int searchX = left + 157;
            int searchY = top + 21;
            if (mouseX >= searchX && mouseX < searchX + 106 && mouseY >= searchY && mouseY < searchY + this.font.lineHeight) {
                if (++this.searchHoverTime > 20) {
                    guiGraphics.renderTooltip(this.font,
                            Component.translatable("gui.ebwizardry.lectern.search_tooltip"), mouseX, mouseY);
                }
            } else {
                this.searchHoverTime = 0;
            }

            this.renderPageButtonTooltip(guiGraphics, this.nextPageButton, "next_page", mouseX, mouseY);
            this.renderPageButtonTooltip(guiGraphics, this.prevPageButton, "previous_page", mouseX, mouseY);
            this.renderPageButtonTooltip(guiGraphics, this.lastPageButton, "last_page", mouseX, mouseY);
            this.renderPageButtonTooltip(guiGraphics, this.firstPageButton, "first_page", mouseX, mouseY);
        } else {
            if (this.indexButton.visible && this.indexButton.isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(this.font,
                        Component.translatable("gui.ebwizardry.lectern.contents"), mouseX, mouseY);
            }
            if (this.locateButton.visible && this.locateButton.isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(this.font,
                        Component.translatable("gui.ebwizardry.lectern.locate"), mouseX, mouseY);
            }
        }
    }

    private void renderPageButtonTooltip(GuiGraphics guiGraphics, TurnPageButton button, String key, int mouseX, int mouseY) {
        if (button.visible && button.isMouseOver(mouseX, mouseY)) {
            guiGraphics.renderTooltip(this.font,
                    Component.translatable("gui.ebwizardry.lectern." + key), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.searchField != null && this.searchField.mouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchField != null && this.searchField.isVisible() && this.searchField.isFocused()
                && this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (this.minecraft != null && (this.searchField == null || !this.searchField.isFocused())
                && this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.searchField != null && this.searchField.isVisible() && this.searchField.isFocused()
                && this.searchField.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        this.sendSpellToServer();
        super.onClose();
    }

    @Override
    public void removed() {
        this.sendSpellToServer();
        super.removed();
    }

    private void sendSpellToServer() {
        if (this.spellSent) return;
        this.spellSent = true;

        if (this.minecraft == null || this.minecraft.getConnection() == null) return;

        Services.NETWORK_HELPER.sendToServer(new LecternSpellPacketC2S(this.lecternPos,
                this.currentSpell == Spells.NONE ? null : this.currentSpell.getLocation()));
    }

    @Nullable
    private MagicLecternBlockEntity getLectern() {
        if (this.minecraft == null || this.minecraft.level == null) return null;
        return this.minecraft.level.getBlockEntity(this.lecternPos) instanceof MagicLecternBlockEntity lectern
                ? lectern : null;
    }

    private void refreshAvailableSpells() {
        this.availableSpells.clear();

        if (this.minecraft != null && this.minecraft.level != null) {
            this.availableSpells.addAll(LecternBookshelfUtils.findAvailableSpells(this.minecraft.level, this.lecternPos));
        }

        if (this.currentSpell != Spells.NONE && !this.availableSpells.contains(this.currentSpell)) {
            this.currentSpell = Spells.NONE;
        }

        this.updateMatchingSpells();
        this.updateButtonVisibility();
    }

    private void updateMatchingSpells() {
        String query = this.searchField == null ? "" : this.searchField.getValue().trim().toLowerCase(Locale.ROOT);

        this.matchingSpells = this.availableSpells.stream()
                .filter(spell -> query.isEmpty() || this.matchesSearch(spell, query))
                .sorted(this.sortDescending ? this.sortType.comparator.reversed() : this.sortType.comparator)
                .collect(Collectors.toCollection(ArrayList::new));

        this.currentPage = Mth.clamp(this.currentPage, 0, Math.max(0, this.pageCount() - 1));
    }

    private boolean matchesSearch(Spell spell, String query) {
        return spell.getDescriptionFormatted().getString().toLowerCase(Locale.ROOT).contains(query)
                || spell.getLocation().getPath().toLowerCase(Locale.ROOT).contains(query);
    }

    private void updateButtonVisibility() {
        boolean index = this.currentSpell == Spells.NONE;

        if (this.searchField != null) {
            this.searchField.setVisible(index);
            this.searchField.setFocused(index);
        }
        if (this.nextPageButton == null) return;

        int lastPage = this.pageCount() - 1;

        this.prevPageButton.visible = index && this.currentPage > 0;
        this.firstPageButton.visible = index && this.currentPage > 0;
        this.nextPageButton.visible = index && this.currentPage < lastPage;
        this.lastPageButton.visible = index && this.currentPage < lastPage;
        this.indexButton.visible = !index;
        this.locateButton.visible = !index;

        for (SortButton button : this.sortButtons) {
            if (button != null) button.visible = index;
        }

        for (SpellButton button : this.spellButtons) {
            if (button != null) {
                button.visible = index && this.currentPage * SPELL_BUTTON_COUNT + button.index < this.matchingSpells.size();
            }
        }
    }

    private int pageCount() {
        return Math.max(1, Mth.ceil(this.matchingSpells.size() / (float) SPELL_BUTTON_COUNT));
    }

    private void changePage(int delta) {
        this.currentPage = Mth.clamp(this.currentPage + delta, 0, this.pageCount() - 1);
        this.updateButtonVisibility();
    }

    private void firstPage() {
        this.currentPage = 0;
        this.updateButtonVisibility();
    }

    private void lastPage() {
        this.currentPage = this.pageCount() - 1;
        this.updateButtonVisibility();
    }

    @Nullable
    private Spell getSpellForButton(int buttonIndex) {
        int index = this.currentPage * SPELL_BUTTON_COUNT + buttonIndex;
        return index < this.matchingSpells.size() ? this.matchingSpells.get(index) : null;
    }

    private void selectSpell(int buttonIndex) {
        Spell spell = this.getSpellForButton(buttonIndex);
        if (spell == null) return;

        this.currentSpell = spell;
        this.updateButtonVisibility();
    }

    private void toggleSort(SortType type) {
        if (this.sortType == type) {
            this.sortDescending = !this.sortDescending;
        } else {
            this.sortType = type;
            this.sortDescending = false;
        }

        this.updateMatchingSpells();
        this.updateButtonVisibility();
    }

    private void locateCurrentSpell() {
        if (this.currentSpell == Spells.NONE || this.minecraft == null || this.minecraft.level == null) return;

        List<BlockPos> shelves = LecternBookshelfUtils.findBookshelvesContaining(
                this.minecraft.level, this.lecternPos, this.currentSpell);

        this.onClose();

        for (BlockPos shelf : shelves) {
            for (Direction side : Direction.values()) {
                Vec3 faceCentre = Vec3.atCenterOf(shelf)
                        .add(side.getStepX() * 0.52, side.getStepY() * 0.52, side.getStepZ() * 0.52);

                for (int i = 0; i < 4; i++) {
                    double offsetX = (this.minecraft.level.random.nextDouble() - 0.5) * 0.35;
                    double offsetY = (this.minecraft.level.random.nextDouble() - 0.5) * 0.35;
                    double offsetZ = (this.minecraft.level.random.nextDouble() - 0.5) * 0.35;

                    ParticleBuilder.create(EBParticles.DUST)
                            .pos(faceCentre.x + offsetX, faceCentre.y + offsetY, faceCentre.z + offsetZ)
                            .color(0.9F, 0.5F, 0.8F)
                            .fade(0.7F, 0, 1)
                            .face(side)
                            .gravity(false)
                            .spawn(this.minecraft.level);
                }
            }

            this.minecraft.level.playLocalSound(shelf.getX() + 0.5, shelf.getY() + 0.5, shelf.getZ() + 0.5,
                    EBSounds.BLOCK_LECTERN_LOCATE_SPELL.get(), SoundSource.BLOCKS, 1.0F, 0.7F, false);
        }
    }

    private static int[] spellButtonPos(int index) {
        int column = index % SPELL_COLUMNS;
        int row = (index / SPELL_COLUMNS) % SPELL_ROWS;

        int x = index < SPELL_BUTTON_COUNT / 2
                ? SPELL_BUTTON_INSET_X + column * SPELL_BUTTON_SPACING
                : X_SIZE - SPELL_BUTTON_INSET_X - 34 - (2 - column) * SPELL_BUTTON_SPACING;

        return new int[]{x, SPELL_BUTTON_INSET_Y + row * SPELL_BUTTON_SPACING};
    }

    private enum SortType {
        TIER("tier", Comparator.comparingInt((Spell spell) -> spell.getTier().getLevel())),
        ELEMENT("element", Comparator.comparing((Spell spell) -> spell.getElement().getLocation().toString())
                .thenComparingInt(spell -> spell.getTier().getLevel())),
        ALPHABETICAL("alphabetical", Comparator.comparing(spell -> spell.getLocation().getPath()));

        private final String name;
        private final Comparator<Spell> comparator;

        SortType(String name, Comparator<Spell> comparator) {
            this.name = name;
            this.comparator = comparator;
        }
    }

    private enum TurnPageType {
        NEXT_PAGE(0, 196),
        PREVIOUS_PAGE(0, 208),
        NEXT_SECTION(0, 220),
        PREVIOUS_SECTION(0, 232),
        CONTENTS(0, 244);

        private final int u;
        private final int v;

        TurnPageType(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }

    private class TurnPageButton extends Button {
        private final TurnPageType type;

        private TurnPageButton(int x, int y, TurnPageType type, Runnable action) {
            super(x, y, 20, 12, Component.empty(), button -> action.run(), Button.DEFAULT_NARRATION);
            this.type = type;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int colour = this.isMouseOver(mouseX, mouseY) ? 0xFFFF8900 : 0xFF5B3A24;
            int x = this.getX();
            int y = this.getY();

            switch (this.type) {
                case NEXT_PAGE -> drawArrow(guiGraphics, x + 3, y + 2, true, colour);
                case PREVIOUS_PAGE -> drawArrow(guiGraphics, x + 8, y + 2, false, colour);
                case NEXT_SECTION -> {
                    drawChevron(guiGraphics, x + 4, y + 2, true, colour);
                    drawChevron(guiGraphics, x + 10, y + 2, true, colour);
                }
                case PREVIOUS_SECTION -> {
                    drawChevron(guiGraphics, x + 5, y + 2, false, colour);
                    drawChevron(guiGraphics, x + 11, y + 2, false, colour);
                }
                case CONTENTS -> {
                    for (int row = 0; row < 3; row++) {
                        int lineY = y + 2 + row * 3;
                        guiGraphics.fill(x + 3, lineY, x + 5, lineY + 2, colour);
                        guiGraphics.fill(x + 7, lineY, x + 17, lineY + 2, colour);
                    }
                }
            }
        }

        @Override
        public void playDownSound(@NotNull SoundManager soundManager) {
            soundManager.play(SimpleSoundInstance.forUI(EBSounds.MISC_PAGE_TURN.get(), 1.0F));
        }
    }

    private class SpellButton extends Button {
        private final int index;

        private SpellButton(int x, int y, int index) {
            super(x, y, 34, 34, Component.empty(), button -> selectSpell(index), Button.DEFAULT_NARRATION);
            this.index = index;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            // Hover is rendered with the slot contents so it remains behind the tooltip.
        }

        @Override
        public void playDownSound(@NotNull SoundManager soundManager) {
            soundManager.play(SimpleSoundInstance.forUI(EBSounds.MISC_PAGE_TURN.get(), 1.0F));
        }
    }

    private class SortButton extends Button {
        private final SortType sortType;

        private SortButton(int x, int y, SortType sortType) {
            super(x, y, 10, 10, Component.empty(), button -> toggleSort(sortType), Button.DEFAULT_NARRATION);
            this.sortType = sortType;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int u = 0;
            if (MagicLecternScreen.this.sortType == this.sortType) {
                u = MagicLecternScreen.this.sortDescending ? 20 : 10;
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            guiGraphics.blit(SORT_TEXTURE, this.getX(), this.getY(), u, this.sortType.ordinal() * 10,
                    10, 10, 32, 32);
        }
    }

    private class LocateButton extends Button {
        private LocateButton(int x, int y) {
            super(x, y, 12, 12, Component.empty(), button -> locateCurrentSpell(), Button.DEFAULT_NARRATION);
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int colour = this.isMouseOver(mouseX, mouseY) ? 0xFFFF8900 : 0xFF5B3A24;
            int x = this.getX();
            int y = this.getY();

            // Magnifying glass: a small square ring and a two-pixel diagonal handle.
            guiGraphics.fill(x + 2, y + 1, x + 7, y + 2, colour);
            guiGraphics.fill(x + 1, y + 2, x + 2, y + 7, colour);
            guiGraphics.fill(x + 7, y + 2, x + 8, y + 7, colour);
            guiGraphics.fill(x + 2, y + 7, x + 7, y + 8, colour);
            guiGraphics.fill(x + 7, y + 7, x + 9, y + 9, colour);
            guiGraphics.fill(x + 9, y + 9, x + 11, y + 11, colour);
        }
    }

    private static void drawArrow(GuiGraphics guiGraphics, int x, int y, boolean right, int colour) {
        guiGraphics.fill(x, y + 3, x + 12, y + 5, colour);
        int tipX = right ? x + 9 : x;
        drawChevron(guiGraphics, tipX, y, right, colour);
    }

    private static void drawChevron(GuiGraphics guiGraphics, int x, int y, boolean right, int colour) {
        for (int i = 0; i < 4; i++) {
            int px = right ? x + i : x + 3 - i;
            guiGraphics.fill(px, y + i, px + 2, y + i + 2, colour);
            guiGraphics.fill(px, y + 7 - i, px + 2, y + 9 - i, colour);
        }
    }
}
