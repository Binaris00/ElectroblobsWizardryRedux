package com.binaris.wizardry.client.gui.screens.handbook;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.client.util.DrawingUtils;
import com.binaris.wizardry.client.gui.button.GuiButtonHyperlink;
import com.binaris.wizardry.client.gui.button.GuiButtonInvisible;
import com.binaris.wizardry.client.gui.button.GuiButtonTurnPage;
import com.binaris.wizardry.client.gui.button.GuiButtonTurnPage.Type;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.core.config.EBServerConfig;
import com.binaris.wizardry.core.networking.c2s.RequestAdvancementSyncPacketC2S;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import com.binaris.wizardry.setup.registries.client.EBKeyBinding;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

/// GUI class for the wizard's handbook. Like any GUI class, this is instantiated each time the book is opened. As of
/// Wizardry 0.8.6, the handbook text is defined as a JSON file rather than a plain text file, and is loaded only on
/// resource pack reload, rather than every time the book is opened. This means all the data structures (sections, images,
/// etc.) are built before the GUI instance exists at all. However, since some things depend on positioning, these have to
/// be initialised on GUI creation. (Previously, everything was done on GUI load)
///
/// @author Electroblob
/// @author Crystal
/// @version Wizardry 0.8.6-alpha
/// @see Section
/// @see Contents
/// @see Image
/// @see CraftingRecipe
///
public class HandBookScreen extends Screen {
    public static final ResourceLocation BOOK_LOCATION = WizardryMainMod.location("textures/gui/handbook/handbook.png");
    // Formatting markup
    public static final char FORMAT_MARKER = '#';
    public static final char HYPERLINK_MARKER = '@';
    public static final String IMAGE_TAG = "image";
    public static final String RECIPE_TAG = "recipe";
    public static final String RULER_TAG = "ruler";
    public static final Map<String, String> FORMAT_TAGS = new HashMap<>();
    /// The dimensions of the rendered GUI area.
    public static final int GUI_WIDTH = 288, GUI_HEIGHT = 180;
    /// The dimensions of the GUI texture itself.
    public static final int TEXTURE_WIDTH = 512, TEXTURE_HEIGHT = 256;
    /// The dimensions of the area of a single page in which text can be drawn.
    public static final int PAGE_WIDTH = 120, PAGE_HEIGHT = 140;

    // Dimension constants
    // Private constants are not relevant to book elements, package-protected ones are
    
	/// The distance of the text from the top outside corner of each page.
    public static final int TEXT_INSET_X = 17, TEXT_INSET_Y = 16;
    /// A map which stores all loaded section objects, including subsections. This gets wiped on resource pack reload and
    /// repopulated with mappings as specified by the handbook JSON file for the current language. The keys in the map
    /// correspond to the keys in the sections object in that file, and are sorted in that order.
    public static final Map<String, Section> sections = new LinkedHashMap<>();
    /// A map which stores all loaded contents objects. This gets wiped on resource pack reload and repopulated with
    /// mappings as specified by the handbook JSON file for the current language. The keys in the map correspond to the
    /// id strings for the contents objects in that file. This map is not sorted.
    public static final Map<String, Contents> contentsList = new HashMap<>();
    /// A map which stores all loaded hex colour values. This gets wiped on resource pack reload and repopulated with
    /// mappings as specified by the handbook JSON file for the current language. The keys in the map correspond to the
    /// keys in the colours object in that file. This map is not sorted.
    public static final Map<String, Integer> colours = new HashMap<>();
    /// A map which stores all loaded image objects. This gets wiped on resource pack reload and repopulated with
    /// mappings as specified by the handbook JSON file for the current language. The keys in the map correspond to the
    /// keys in the images object in that file. This map is not sorted.
    public static final Map<String, Image> images = new HashMap<>();
    /// A map which stores all loaded crafting recipe objects. This gets wiped on resource pack reload and repopulated
    /// with mappings as specified by the handbook JSON file for the current language. The keys in the map correspond to
    /// the keys in the recipes object in that file. This map is not sorted.
    public static final Map<String, CraftingRecipe> recipes = new HashMap<>();
    private static final ResourceLocation DEFAULT = new ResourceLocation(WizardryMainMod.MOD_ID, "texts/handbook_en_us.json");

    // Global variables
    private static final List<String> ADDONS = new ArrayList<>();
    /// Global Gson instance for the handbook.
    private static final Gson gson = new Gson();
    /// The distance of the buttons from the bottom outside corners of the GUI.
    private static final int BUTTON_INSET_X = 22, BUTTON_INSET_Y = 13;
    /// The distance between adjacent buttons.
    private static final int BUTTON_SPACING = 20;
    /// The distance of the page numbers from the bottom of the GUI.
    private static final int PAGE_NUMBER_INSET = 22;

	// ===================================================
	// Handbook content
	// ===================================================

    // As a general rule, I prefer to make static final fields lowercase if they're collections that change, because even
    // though the collection itself is constant, the stuff in it is not, so being lowercase highlights this difference.
    
	/// The <b>double-page</b> number where the bookmark is currently set, <b>relative to the section stored in
    /// {@link HandBookScreen#bookmarkSection}</b>. Static because it persists when the book is closed.
    private static int bookmarkPage = 0;
    /// The key corresponding to the section in which the bookmark is currently set. Static because it persists when the
    /// book is closed. Storing a section means the bookmark doesn't change location when new sections are unlocked.
    private static String bookmarkSection;
    /// A list which stores all loaded section objects, including subsections. This is an unmodifiable list view of the
    /// values in {@link HandBookScreen#sections}, sorted in the same (page number) order. This exists only to allow
    /// sections to be accessed by ordinal index for the various navigation buttons, hence why it is private.
    private static List<Section> sectionList;
    /// The <b>double-page</b> currently being viewed. Each double-page spread counts as a single page, with the inside
    /// of the front cover being page 0.
    private int currentPage = 0;
    /// The number of <b>single</b> pages currently in the book. This is calculated on GUI load based on visible sections.
    private int pageCount = 1; // Starts at 1 because the first single-page is the inside of the cover
    // ===================================================
	// Buttons
	// ===================================================
    private Button bookmark, next, previous, nextSection, previousSection, menu;

    public HandBookScreen() {
        super(Component.empty());
    }

	// ===================================================
	// Helper methods
	// ===================================================

    /// Converts the given single page index to a double-page index. Inverse of
    /// {@link HandBookScreen#doubleToSinglePage(int, boolean)}.
    /// 
    /// @param singlePageIndex The single-page index, which is the same as the page numbers actually displayed.
    /// @return The corresponding double-page index.
    public static int singleToDoublePage(int singlePageIndex) {
        // Yes, this is trivial, but if I ever change the numbering it'll be useful. It's also more descriptive.
        return singlePageIndex / 2;
    }

    /// Converts the given double-page index to a single-page index. Inverse of
    /// {@link HandBookScreen#singleToDoublePage(int)}.
    /// 
    /// @param doublePageIndex The double-page index, as stored in {@link HandBookScreen#currentPage}.
    /// @param rightHandPage   True to return the page on the right (1 greater), false for the left-hand page.
    /// @return The corresponding single-page index.
    public static int doubleToSinglePage(int doublePageIndex, boolean rightHandPage) {
        return rightHandPage ? doublePageIndex * 2 + 1 : doublePageIndex * 2;
    }

    /// Returns whether the given page index refers to a right-hand page or a left-hand page.
    /// 
    /// @param page The single-page index, which is the same as the page number actually displayed.
    /// @return True if the given page index refers to a right-hand page, false if it is a left-hand page.
    public static boolean isRightPage(int page) {
        return page % 2 == 1;
    }

    /// Adds a format tag to the handbook. All occurrences of the given tag string preceded by a # will be replaced with
    /// the result of the given value string on GUI load. The value string, therefore, can be anything that should be
    /// input dynamically, as long as it does not change while the GUI is open. Examples include wizardry's version,
    /// the various element colours and the keys assigned to wizardry's controls.
    /// 
    /// @param tag   The tag string, as defined in the handbook JSON file, excluding the # character. Cannot include spaces.
    /// @param value The string to replace occurrences of the given format tag with. Can include spaces but not the # character.
    public static void addFormatTag(String tag, String value) {
        FORMAT_TAGS.put(tag, value);
    }

    private static void initFormatTags() {
        addFormatTag("next_spell_key", EBKeyBinding.NEXT_SPELL.getTranslatedKeyMessage().getString());
        addFormatTag("previous_spell_key", EBKeyBinding.PREVIOUS_SPELL.getTranslatedKeyMessage().getString());
        addFormatTag("example_charging_loss", "" + (EBServerConfig.MANA_PER_CRYSTAL.get() - 30));
        addFormatTag("mana_per_crystal", "" + EBServerConfig.MANA_PER_CRYSTAL.get());
        addFormatTag("novice_max_charge", "" + SpellTiers.NOVICE.getMaxCharge());
        addFormatTag("apprentice_max_charge", "" + SpellTiers.APPRENTICE.getMaxCharge());
        addFormatTag("advanced_max_charge", "" + SpellTiers.ADVANCED.getMaxCharge());
        addFormatTag("master_max_charge", "" + SpellTiers.MASTER.getMaxCharge());
        addFormatTag("version", "0.8.6-alpha");
        addFormatTag("mcversion", "1.20.1");

        addFormatTag("colour_novice", "§7");
        addFormatTag("colour_apprentice", SpellTiers.APPRENTICE.getColor().toString());
        addFormatTag("colour_advanced", SpellTiers.ADVANCED.getColor().toString());
        addFormatTag("colour_master", SpellTiers.MASTER.getColor().toString());

        addFormatTag("colour_fire", Elements.FIRE.getColor().toString());
        addFormatTag("colour_ice", Elements.ICE.getColor().toString());
        addFormatTag("colour_lightning", Elements.LIGHTNING.getColor().toString());
        addFormatTag("colour_necromancy", Elements.NECROMANCY.getColor().toString());
        addFormatTag("colour_earth", Elements.EARTH.getColor().toString());
        addFormatTag("colour_sorcery", Elements.SORCERY.getColor().toString());
        addFormatTag("colour_healing", Elements.HEALING.getColor().toString());

        addFormatTag("colour_reset", "§0");
    }

    /// Called from preInit in the main mod class (via the proxies) to initialise the handbook (parses the JSON file
    /// and constructs the relevant data structures), and again on each resource reload (changing the language triggers
    /// a resource reload).
    public static void loadHandbookFile(ResourceManager manager) {
        if (manager == null) {
            EBLogger.error("Tried to reload the handbook file, but received a null resource manager. Aborting!");
            return;
        }

        List<Resource> handbookFiles = getHandbookResource(manager);

        if (!handbookFiles.isEmpty()) {
            // Wipes all the maps before repopulating them
            images.clear();
            sections.clear();
            contentsList.clear();
            colours.clear();

            bookmarkSection = null; // Also need to wipe the reference to the old bookmarked section

            for (Resource handbookFile : handbookFiles) {
                BufferedReader reader;
                try {
                    reader = new BufferedReader(new InputStreamReader(handbookFile.open(), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                JsonElement je = gson.fromJson(reader, JsonElement.class);
                JsonObject json = je.getAsJsonObject();

                GsonHelper.getAsJsonObject(json, "colours").entrySet().forEach(e -> colours.put(e.getKey(),
                        Color.decode(e.getValue().getAsString()).getRGB()));

                // Repopulates the remaining maps
                Image.populate(images, json);
                CraftingRecipe.populate(recipes, json);
                Section.populate(sections, json);

                sectionList = List.copyOf(sections.values());

                if (sections.isEmpty()) {
                    EBLogger.warn("Handbook has no sections! Aborting loading...");
                    return;
                }

                bookmarkSection = GsonHelper.getAsString(json, "bookmark_start_section");
                if (!sections.containsKey(bookmarkSection))
                    throw new JsonSyntaxException("Section with id " + bookmarkSection + " is undefined");
            }
        }

        // The first resource load on startup is done before the packet handler is loaded
        if (Minecraft.getInstance().player != null)
            Services.NETWORK_HELPER.sendToServer(new RequestAdvancementSyncPacketC2S());
    }

    /// Retrieves the handbook JSON file for the current language and returns its IResource object. If a handbook file
    /// cannot be found for the current language, a message is printed to the console and the method attempts to retrieve
    /// the default file instead (English-US). If this file cannot be found, the resulting error is printed to the
    /// console and the method returns null.
    /// 
    /// @param manager The resource manager instance to use.
    /// @return The handbook JSON file, as an IResource, or null if it was not found.
    private static List<Resource> getHandbookResource(ResourceManager manager) {

        // TODO: Implement resource pack stacking to allow addon mods and texture packs to add/overwrite content

        Resource handbookFile = null;
        List<Resource> handbookFiles = new ArrayList<>();

        try {
            handbookFile = manager.getResourceOrThrow(new ResourceLocation(WizardryMainMod.MOD_ID, "texts/handbook_"
                    + Minecraft.getInstance().getLanguageManager().getSelected() + ".json"));
        } catch (Exception e) {
            EBLogger.info("Wizard handbook JSON file missing for the current language (" + Minecraft.getInstance()
                    .getLanguageManager().getSelected() + "). Using default (English-US) instead.");

            try {
                handbookFile = manager.getResourceOrThrow(DEFAULT);
            } catch (IOException x) {
                EBLogger.error("Couldn't find file: " + DEFAULT + ". The file may be missing; please try re-downloading and reinstalling Wizardry.", x);
            }
        }

        handbookFiles.add(handbookFile);

        for (String id : ADDONS) {
            // Addons
            EBLogger.info("Registering addon Wizard's Handbook contents for " + id);
            try {
                handbookFile = manager.getResourceOrThrow(new ResourceLocation(id, "texts/handbook_"
                        + Minecraft.getInstance().getLanguageManager().getSelected() + ".json"));
            } catch (Exception e) {
                EBLogger.info("Wizard handbook JSON file missing for the current language (" + Minecraft.getInstance()
                        .getLanguageManager().getSelected() + "). Using default (English-US) instead.");

                try {
                    handbookFile = manager.getResourceOrThrow(new ResourceLocation(id, "texts/handbook_en_us.json"));
                } catch (IOException x) {
                    EBLogger.error("Couldn't find file: " + DEFAULT + ". The file may be missing; please try re-downloading and reinstalling Wizardry.", x);
                }
            }
            handbookFiles.add(handbookFile);
        }

        return handbookFiles;
    }

    // Overridden to make it public
    public static void updateUnlockStatus(boolean showToasts, List<ResourceLocation> completedAdvancements) {
        sections.values().forEach(s -> s.updateUnlockStatus(showToasts, completedAdvancements));
    }

    // JSON Parsing / Data Construction

    public static void registerAddonHandbookContent(String id) {
        if (!ADDONS.contains(id)) {
            ADDONS.add(id);
        }
    }

    @Override
    protected void init() {
        super.init();
        initFormatTags();

        final int left = this.width / 2 - GUI_WIDTH / 2;
        final int top = this.height / 2 - GUI_HEIGHT / 2;

        recipes.values().forEach(CraftingRecipe::load);

        this.addRenderableWidget(this.next = new GuiButtonTurnPage(left + GUI_WIDTH - BUTTON_INSET_X - GuiButtonTurnPage.WIDTH,
                top + GUI_HEIGHT - BUTTON_INSET_Y - GuiButtonTurnPage.HEIGHT,
                Type.NEXT_PAGE, BOOK_LOCATION, TEXTURE_WIDTH, TEXTURE_HEIGHT,
                this::next
        ));
        this.addRenderableWidget(this.previous = new GuiButtonTurnPage(left + BUTTON_INSET_X,
                top + GUI_HEIGHT - BUTTON_INSET_Y - GuiButtonTurnPage.HEIGHT,
                Type.PREVIOUS_PAGE, BOOK_LOCATION, TEXTURE_WIDTH, TEXTURE_HEIGHT,
                this::previous
        ));
        this.addRenderableWidget(this.nextSection = new GuiButtonTurnPage(left + GUI_WIDTH - BUTTON_INSET_X - GuiButtonTurnPage.WIDTH - BUTTON_SPACING,
                top + GUI_HEIGHT - BUTTON_INSET_Y - GuiButtonTurnPage.HEIGHT,
                Type.NEXT_SECTION, BOOK_LOCATION, TEXTURE_WIDTH, TEXTURE_HEIGHT,
                this::pageSection
        ));
        this.addRenderableWidget(this.previousSection = new GuiButtonTurnPage(left + BUTTON_INSET_X + BUTTON_SPACING,
                top + GUI_HEIGHT - BUTTON_INSET_Y - GuiButtonTurnPage.HEIGHT,
                Type.PREVIOUS_SECTION, BOOK_LOCATION, TEXTURE_WIDTH, TEXTURE_HEIGHT,
                this::pageSection
        ));
        this.addRenderableWidget(this.menu = new GuiButtonTurnPage(left + GUI_WIDTH / 2 - 28,
                top + GUI_HEIGHT - BUTTON_INSET_Y - GuiButtonTurnPage.HEIGHT,
                Type.CONTENTS, BOOK_LOCATION, TEXTURE_WIDTH, TEXTURE_HEIGHT,
                this::menu
        ));
        this.addRenderableWidget(this.bookmark = new GuiButtonInvisible(left + 130, top + 172, 11, 19, this::bookmark) {
            @Override
            public void playDownSound(@NotNull SoundManager handler) {
                handler.play(SimpleSoundInstance.forUI(EBSounds.MISC_PAGE_TURN.get(), 1));
            }
        });

        pageCount = 1;

        // Clears instances of all images and recipes
        images.values().forEach(Image::clearInstances);
        recipes.values().forEach(CraftingRecipe::clearInstances);

        // Formats all the unlocked sections in order
        for (Section section : sections.values()) {
            if (section.isUnlocked()) {
                pageCount = section.format(this::linkButton, font, pageCount, left, top);
                section.getButtons().forEach(this::addRenderableWidget);
            }
        }

        contentsList.values().forEach(c -> c.getButtons().forEach(this::addRenderableWidget));

        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(EBSounds.MISC_BOOK_OPEN.get(), 1));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int left = this.width / 2 - GUI_WIDTH / 2;
        int top = this.height / 2 - GUI_HEIGHT / 2;

        // Main background
        DrawingUtils.drawTexturedRect(guiGraphics, BOOK_LOCATION, left, top, 0, 0, GUI_WIDTH, GUI_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        // First page background
        if (currentPage == 0) {
            DrawingUtils.drawTexturedRect(guiGraphics, BOOK_LOCATION, left, top, 368, 0, GUI_WIDTH / 2, GUI_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            previous.visible = false;
            previousSection.visible = false; // Not worth testing if we're in the first section every frame
            menu.visible = false;
        } else {
            previous.visible = true;
            previousSection.visible = true;
            menu.visible = true;
        }

        // Last page background
        if (currentPage == singleToDoublePage(pageCount)) {
            DrawingUtils.drawTexturedFlippedRect(guiGraphics, BOOK_LOCATION, left + GUI_WIDTH / 2, top, 368, 0, GUI_WIDTH / 2, GUI_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT, true, false);
            next.visible = false;
            nextSection.visible = false;
        } else {
            next.visible = true;
            nextSection.visible = true;
        }

        // Page numbers
        if (currentPage > 0) {
            String pageNumber = "" + doubleToSinglePage(currentPage, false);
            guiGraphics.drawString(font, pageNumber,
                    left + TEXT_INSET_X + PAGE_WIDTH / 2 - font.width(pageNumber) / 2,
                    top + GUI_HEIGHT - PAGE_NUMBER_INSET,
                    DrawingUtils.BLACK,
                    false
            );
        }
        if (currentPage < singleToDoublePage(pageCount)) {
            String pageNumber = "" + doubleToSinglePage(currentPage, true);
            guiGraphics.drawString(font, pageNumber,
                    left + GUI_WIDTH - TEXT_INSET_X - PAGE_WIDTH / 2 - font.width(pageNumber) / 2,
                    top + GUI_HEIGHT - PAGE_NUMBER_INSET,
                    DrawingUtils.BLACK,
                    false
            );
        }

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        // Main content
        contentsList.values().forEach(c -> {
            if (c.section.isUnlocked()) c.draw(guiGraphics, font, currentPage, left, top);
        });
        sections.values().forEach(s -> {
            if (s.isUnlocked()) s.draw(guiGraphics, font, BOOK_LOCATION, currentPage, left, top);
        });
        // These only get populated if the sections are unlocked so no checks are necessary
        images.values().forEach(i -> i.draw(guiGraphics, font, BOOK_LOCATION, currentPage, left, top));
        recipes.values().forEach(r -> r.draw(guiGraphics, font, BOOK_LOCATION, itemRenderer, currentPage, left, top));

        // Buttons
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Bookmark
        if (currentPage == singleToDoublePage(sections.get(bookmarkSection).startPage) + bookmarkPage) {
            // If the current page is the bookmarked page, the (invisible) bookmark button is disabled
            bookmark.visible = false;
            DrawingUtils.drawTexturedRect(guiGraphics, BOOK_LOCATION, left + 138, top, 299, 0, 11, 191, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else {
            bookmark.visible = true;
            bookmark.setX(left +
                    (currentPage > singleToDoublePage(sections.get(bookmarkSection).startPage) + bookmarkPage ? 130 : 147));
            DrawingUtils.drawTexturedRect(guiGraphics, BOOK_LOCATION, bookmark.getX(), top,
                    bookmark.isMouseOver(mouseX, mouseY) ? 310 : 288, 0, 11, 191, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        // Recipe tooltips
        recipes.values().forEach(r -> r.drawTooltips(guiGraphics, font, currentPage, left, top, mouseX, mouseY));
    }

    /*
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (modifiers == GLFW.GLFW_REPEAT) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    */

	// ===================================================
	// Controls
	// ===================================================
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 1) {
            // Right-clicking of bookmark
            if (bookmark.isMouseOver(mouseX, mouseY)) {
                // this.selectedButton = bookmark;
                for (String key : sections.keySet()) {
                    // The bookmark is assumed to bookmark the left-hand page
                    if (sections.get(key).containsPage(doubleToSinglePage(currentPage, false)))
                        bookmarkSection = key;
                }

                bookmarkPage = currentPage - singleToDoublePage(sections.get(bookmarkSection).startPage);
            }
        } else {
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        return true;
    }

    protected void previous(Button button) {
        if (button.active && this.currentPage > 0) {
            currentPage--;
        }
    }

    protected void next(Button button) {
        if (button.active && currentPage < singleToDoublePage(pageCount))
            currentPage++;
    }

    protected void pageSection(Button button) {
        if (button.active && button == nextSection || button == previousSection) {
            Section currentSection = null;
            if (currentPage < singleToDoublePage(pageCount)) {
                // Find current section
                for (Section section : sections.values()) {
                    if (section.containsPage(doubleToSinglePage(currentPage, button == nextSection))) {
                        currentSection = section;
                        break;
                    }
                }
            }

            List<Section> visibleSections = new ArrayList<>(sectionList);
            visibleSections.removeIf(s -> !s.isUnlocked());

            int index = currentSection == null ? visibleSections.size() : visibleSections.indexOf(currentSection);
            if (button == nextSection) {
                if (index + 1 < visibleSections.size()) {
                    currentPage = singleToDoublePage(visibleSections.get(index + 1).startPage);
                } else {
                    currentPage = singleToDoublePage(pageCount);
                }
            } else {
                if (index > 0) {
                    currentPage = singleToDoublePage(visibleSections.get(index - 1).startPage);
                }
            }
        }
    }

    protected void menu(Button button) {
        if (button.active) {
            currentPage = singleToDoublePage(sections.get("main_contents").startPage);
        }
    }

    protected void bookmark(Button button) {
        if (button.active && bookmarkSection != null) {
            currentPage = singleToDoublePage(sections.get(bookmarkSection).startPage) + bookmarkPage;
        }
    }

    public void linkButton(Button button) {
        if (button.active) {
            if (button instanceof GuiButtonHyperlink.Internal internal) {
                currentPage = singleToDoublePage(internal.target.startPage);
            } else if (button instanceof GuiButtonHyperlink.External external) {
                Style style = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, external.link.getString()));
                this.handleComponentClicked(style);
            }
        }
    }
}
