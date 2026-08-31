package com.binaris.wizardry.client.gui.screens.handbook;

import com.binaris.wizardry.api.client.util.DrawingUtils;
import com.binaris.wizardry.api.content.util.JavaUtils;
import com.binaris.wizardry.client.gui.button.GuiButtonHyperlink;
import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.core.config.EBClientConfig;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Instances of this class represent sections in the wizard's handbook. As of wizardry 0.8.6, this class handles
 * everything within the section itself, including JSON parsing, unlock triggers and drawing the actual text.
 * Sections may now also be nested and have other elements within them, such as images and a table of contents, a
 * behaviour which is also handled within this class.
 * <p></p>
 * The formatting of the book is now done 'dynamically' - that is, the exact positions and page numbers of
 * sections, images and so on are determined on GUI load and depend on which of the previous sections have been
 * unlocked, amongst other factors. This means that all of the unlocked sections must be formatted in order on GUI
 * load, so that each section knows the previous section's length and therefore where to start.
 *
 * @author Electroblob
 * @author Crystal
 * @since Wizardry 0.8.6-alpha
 */
// Because these are now generated on resource pack reload (not on handbook open, as before), this class can no longer
// be a non-static inner class
public class Section {
    private static final Set<String> SPACELESS_LANGUAGES = ImmutableSet.of("zh_cn", "zh_tw");
    private final List<List<Button>> buttons;
    /**
     * A list of <b>single</b> pages, which are themselves lists of paragraphs (each paragraph is a single
     * string which may include line breaks and other escape characters).
     */
    private final List<List<String>> pages;
    private final Map<String, Section> subsections;
    // Final fields are mandatory (none here though), the rest are optional
    public String title;
    /**
     * The <b>single-page</b> index of the first page of this section.
     */
    public int startPage;
    private String[] rawText;
    private Contents contents;
    // Derived fields, not explicitly defined in JSON
    private ResourceLocation[] triggers;
    private boolean centreX, centreY;
    private boolean unlocked = false;
    private boolean isNew = false;

    private Section() {
        this.buttons = new ArrayList<>();
        this.pages = new ArrayList<>();
        this.subsections = new LinkedHashMap<>();
    }

    /**
     * Parses the given JSON object and constructs a new {@code Section} from it, setting all the relevant fields
     * and references. This method converts the JSON object to a {@code Section} object and retrieves any resources;
     * the section is not formatted in any way until GUI load, in {@link Section#format(net.minecraft.client.gui.components.Button.OnPress, Font, int, int, int)}.
     *
     * @param json A JSON object representing the section to be constructed. This must contain at least a "title"
     *             string.
     * @return The resulting {@code Section} object.
     * @throws JsonSyntaxException if at any point the JSON object is found to be invalid.
     */
    public static Section fromJson(JsonObject json) {
        Section section = new Section();
        section.title = GsonHelper.getAsString(json, "title", "");

        if (GsonHelper.isStringValue(json, "include_in_contents")) {
            String id = GsonHelper.getAsString(json, "include_in_contents");
            Contents belongsTo = HandBookScreen.contentsList.get(id);

            if (belongsTo == null) {
                throw new JsonSyntaxException("Expected include_in_contents to be the id of a previously defined contents, but no contents with the id " + id + " exists yet.");
            } else {
                belongsTo.addEntry(section);
            }
        }

        if (GsonHelper.isObjectNode(json, "contents")) {
            section.contents = Contents.fromJson(section, GsonHelper.getAsJsonObject(json, "contents"));
            HandBookScreen.contentsList.put(section.contents.id, section.contents);
        }

        if (GsonHelper.isArrayNode(json, "text")) {
            section.rawText = Streams.stream(GsonHelper.getAsJsonArray(json, "text"))
                    .map(e -> GsonHelper.convertToString(e, "element of array text"))
                    .toArray(String[]::new);
        }

        if (GsonHelper.isArrayNode(json, "triggers")) {
            section.triggers = Streams.stream(GsonHelper.getAsJsonArray(json, "triggers"))
                    .map(e -> new ResourceLocation(GsonHelper.convertToString(e, "element of array triggers")))
                    .toArray(ResourceLocation[]::new);
            // TODO: Can we validate this and throw a JSON exception if no such advancement exists?
        }

        if (GsonHelper.isObjectNode(json, "centre")) {
            JsonObject centre = GsonHelper.getAsJsonObject(json, "centre");
            section.centreX = GsonHelper.getAsBoolean(centre, "x", false);
            section.centreY = GsonHelper.getAsBoolean(centre, "y", false);
        }

        // The only benefit of having subsections (other than logical grouping) is that the parent section can
        // automatically be unlocked if one of the subsections is.
        if (GsonHelper.isObjectNode(json, "sections")) {
            populate(section.subsections, json);
        }

        return section;
    }

    public static void populate(Map<String, Section> map, JsonObject json) {
        JsonObject sectionsObject = GsonHelper.getAsJsonObject(json, "sections");

        // Need to iterate over these since we don't know what they're called or how many there are
        for (Map.Entry<String, JsonElement> entry : sectionsObject.entrySet()) {

            String key = entry.getKey(); // Find out what each element is called, this will be the sections map key

            Section section = fromJson(entry.getValue().getAsJsonObject());
            map.put(key, section);
            map.putAll(section.subsections);
        }
    }

    public Collection<Button> getButtons() {
        return JavaUtils.flatten(buttons);
    }

    /**
     * Returns true if the given page is within this section, false if not (or if the section is locked).
     */
    public boolean containsPage(int page) {
        return this.isUnlocked() && startPage <= page && startPage + pages.size() > page;
    }

    /**
     * Returns true if this section is unlocked for the client player, false if not. Always returns true if
     * handbook progression is disabled in the config.
     */
    public boolean isUnlocked() {
        if (Minecraft.getInstance().player.isCreative()) return true;
        if (!EBClientConfig.HANDBOOK_PROGRESSION.get()) return true; // Always unlocked if handbook progression is off
        if (triggers == null) return true; // If no triggers were defined, the section is unlocked from the start

        // A section is automatically unlocked if one of its subsections is unlocked
        for (Section subsection : subsections.values()) {
            if (subsection.isUnlocked()) return true;
        }

        return unlocked;
    }

    /**
     * Returns true if this section has been unlocked and not read yet. Also returns true if any subsections are new.
     */
    public boolean isNew() {
        if (!EBClientConfig.HANDBOOK_PROGRESSION.get()) return false;
        return isNew || this.subsections.values().stream().anyMatch(Section::isNew);
    }

    /**
     * Actually draws the contents of the given section for the given double-page spread. Will do nothing if the
     * given page is outside of this section.
     *
     * @param font       The font renderer object.
     * @param doublePage The index of the <b>double-page</b> to be drawn.
     * @param left       The x coordinate of the left side of the GUI.
     * @param top        The y coordinate of the top of the GUI.
     */
    // This method is supposed to be 'idiot-proof' in the sense that the code calling it need not check whether the
    // section actually needs drawing, so it can just dumbly call draw(...) for all the sections in order.
    public void draw(GuiGraphics guiGraphics, Font font, ResourceLocation texture, int doublePage, int left, int top) {
        // Show/hide buttons

        int i = 0;
        for (List<Button> list : buttons) {
            final int i1 = i++;
            list.forEach(b -> b.visible = HandBookScreen.singleToDoublePage(startPage + i1) == doublePage);
        }

        int leftIndex = HandBookScreen.doubleToSinglePage(doublePage, false);
        // Relative indices of the pages to be rendered - often these will be outside the section entirely
        int[] visiblePages = {leftIndex - startPage, leftIndex - startPage + 1};

        for (int page : visiblePages) {

            if (page >= 0 && page < pages.size()) {
                List<String> lines = pages.get(page);
                int x = left + (HandBookScreen.isRightPage(startPage + page) ? HandBookScreen.GUI_WIDTH - HandBookScreen.TEXT_INSET_X - HandBookScreen.PAGE_WIDTH : HandBookScreen.TEXT_INSET_X);
                int y = top + HandBookScreen.TEXT_INSET_Y;
                if (centreY)
                    y += HandBookScreen.PAGE_HEIGHT / 2 - lines.size() / 2 * font.lineHeight;

                for (String line : lines) {
                    if (line.startsWith(HandBookScreen.FORMAT_MARKER + HandBookScreen.RULER_TAG)) {
                        DrawingUtils.drawTexturedRect(guiGraphics, texture, x - 1, y - 1, 0, HandBookScreen.GUI_HEIGHT, HandBookScreen.PAGE_WIDTH + 2, 9, HandBookScreen.TEXTURE_WIDTH, HandBookScreen.TEXTURE_HEIGHT);
                    } else {
                        int lx = centreX ? x + HandBookScreen.PAGE_WIDTH / 2 - font.width(line) / 2 : x;
                        guiGraphics.drawString(font, line, lx, y, DrawingUtils.BLACK, false);
                    }

                    y += font.lineHeight;
                }

                isNew = false; // Now a page has been drawn, the player must have seen it so it's not new any more
            }
        }
    }

    /**
     * Called on GUI load to format the section, contents tables and other elements, <b>excluding</b> subsections.
     * Does not perform any actual drawing.
     *
     * @param font      The font renderer object, for measurement purposes.
     * @param startPage The index of the first page (single side, not double-page) of this section.
     * @param left      The x coordinate of the left side of the GUI.
     * @param top       The y coordinate of the top of the GUI.
     * @return The <b>single-page</b> index of the next blank page after the end of this section.
     * @throws JsonSyntaxException if at any point the formatting is found to be invalid.
     */
    public int format(Button.OnPress onPress, Font font, int startPage, int left, int top) {
        this.buttons.clear();
        this.pages.clear();

        // FONT_HEIGHT may change between fonts, so this is calculated here. With the default font it's 14.
        final int maxLineNumber = HandBookScreen.PAGE_HEIGHT / font.lineHeight;

        this.startPage = startPage;

        // First everything is added to a single list of lines, then it is split into pages.
        List<String> lines = new ArrayList<>();

        // Adds the header if present
        if (!this.title.isEmpty()) {
            lines.add(this.title);
            lines.add(HandBookScreen.FORMAT_MARKER + HandBookScreen.RULER_TAG);
        }

        // Adds space for the contents if it exists
        if (this.contents != null) {
            lines.addAll(Collections.nCopies(this.contents.format(onPress, font, startPage, lines.size(), left, top), ""));
            // Line break between contents and first paragraph
            if ((lines.size() % maxLineNumber) != 0) lines.add("");
        }

        if (this.rawText != null) {
            // Paragraphs are defined as a JSON list because it makes it easier to arrange them properly across pages
            // - using multiple line breaks would mean having to find and remove them when at the top of a page.
            for (String paragraph : this.rawText) {
                // (lines.size() % maxLineNumber) gives the number of lines on the current page
                // (lines.size() / maxLineNumber) gives the index of the current page minus the value of startPage

                // Formats the paragraph

                String raw = paragraph; // For error messages

                // Images (images must be separate paragraphs)
                if (paragraph.startsWith(HandBookScreen.FORMAT_MARKER + HandBookScreen.IMAGE_TAG)) {
                    String[] arguments = paragraph.split("\\s", 2);
                    if (arguments.length < 2)
                        throw new JsonSyntaxException("Missing image name in string " + StringUtils.abbreviate(raw, 50));

                    Image image = HandBookScreen.images.get(arguments[1]);
                    if (image == null)
                        throw new JsonSyntaxException("Image with id " + arguments[1] + " is undefined");

                    // Starts a new page if the image will not fit on the current one
                    if ((lines.size() % maxLineNumber) * font.lineHeight + image.getHeight(font) > HandBookScreen.PAGE_HEIGHT) {
                        // Remaining number of lines on the page
                        lines.addAll(Collections.nCopies(maxLineNumber - (lines.size() % maxLineNumber), ""));
                    }

                    if (image.getWidth() > HandBookScreen.PAGE_WIDTH) EBLogger.warn("Image with id " + arguments[1]
                            + "has a width (" + image.getWidth() + ") greater than the maximum page width (" + HandBookScreen.PAGE_WIDTH
                            + "), it will extend beyond the page area.");

                    if (image.getHeight(font) > HandBookScreen.PAGE_HEIGHT)
                        EBLogger.warn("Image with id " + arguments[1]
                                + "has a height (" + image.getHeight(font) + ") greater than the maximum page height (" + HandBookScreen.PAGE_HEIGHT
                                + "), it will extend beyond the page area.");

                    int page = startPage + (lines.size() / maxLineNumber);
                    image.addInstance(page, HandBookScreen.PAGE_WIDTH / 2 - image.getWidth() / 2 + (HandBookScreen.isRightPage(page) ?
                                    HandBookScreen.GUI_WIDTH - HandBookScreen.TEXT_INSET_X - HandBookScreen.PAGE_WIDTH : HandBookScreen.TEXT_INSET_X),
                            HandBookScreen.TEXT_INSET_Y + (lines.size() % maxLineNumber) * font.lineHeight);

                    // Height of the image in lines, rounded up
                    // Uses a single space instead of an empty string so that the page trimming doesn't remove them
                    lines.addAll(Collections.nCopies(image.getHeight(font) / font.lineHeight, " "));
                    lines.add(""); // The last one is removable though, since it's actually extra space

                } else if (paragraph.startsWith(HandBookScreen.FORMAT_MARKER + HandBookScreen.RECIPE_TAG)) { // Recipes (recipes must be separate paragraphs)
                    String[] arguments = paragraph.split("\\s", 2);
                    if (arguments.length < 2)
                        throw new JsonSyntaxException("Missing recipe name in string " + StringUtils.abbreviate(raw, 50));

                    CraftingRecipe recipe = HandBookScreen.recipes.get(arguments[1]);
                    if (recipe == null)
                        throw new JsonSyntaxException("Recipe with id " + arguments[1] + " is undefined");

                    // Starts a new page if the recipe will not fit on the current one
                    if ((lines.size() % maxLineNumber) * font.lineHeight + CraftingRecipe.HEIGHT > HandBookScreen.PAGE_HEIGHT) {
                        // Remaining number of lines on the page, plus the first blank one on the new page
                        lines.addAll(Collections.nCopies(maxLineNumber - (lines.size() % maxLineNumber), " "));
                    }

                    int page = startPage + (lines.size() / maxLineNumber);
                    if (lines.size() % maxLineNumber == 0) lines.add(" ");
                    int startLine = lines.size() % maxLineNumber - 1;

                    recipe.addInstance(page, (HandBookScreen.isRightPage(page) ? HandBookScreen.GUI_WIDTH - HandBookScreen.TEXT_INSET_X - HandBookScreen.PAGE_WIDTH : HandBookScreen.TEXT_INSET_X),
                            HandBookScreen.TEXT_INSET_Y + startLine * font.lineHeight);

                    // Height of the recipe in lines, rounded up
                    // Uses a single space instead of an empty string so that the page trimming doesn't remove them
                    lines.addAll(Collections.nCopies(CraftingRecipe.HEIGHT / font.lineHeight - 1, " "));
                    // This time we're not adding an extra space because it's not really needed
                } else { // All other paragraphs
                    // Formatting
                    for (Map.Entry<String, String> entry : HandBookScreen.FORMAT_TAGS.entrySet()) {
                        paragraph = paragraph.replace(HandBookScreen.FORMAT_MARKER + entry.getKey(), entry.getValue());
                    }

                    // Hyperlinks
                    int linkStart;
                    while ((linkStart = paragraph.indexOf(HandBookScreen.HYPERLINK_MARKER)) > -1) { // Ooh an assignment and a comparison in one...
                        int linkEnd = paragraph.indexOf(HandBookScreen.HYPERLINK_MARKER, linkStart + 1);

                        if (linkEnd < 0)
                            throw new JsonSyntaxException("Un-closed hyperlink marker in string " + StringUtils.abbreviate(raw, 50));
                        if (linkStart == 0) linkStart++;

                        List<String> upToLink = DrawingUtils.listFormattedStringToWidth(font, paragraph.substring(0, linkStart), HandBookScreen.PAGE_WIDTH);
                        if (upToLink.isEmpty())
                            throw new NullPointerException("Paragraphs cannot start with '@': " + paragraph);

                        String linkRaw = paragraph.substring(linkStart, linkEnd + 1);
                        String[] arguments = paragraph.substring(linkStart + 1, linkEnd).split("\\s", 2);
                        String suffix = "";

                        // Account for trailing punctuation, except in languages that don't use spaces such as Chinese
                        LanguageManager manager = Minecraft.getInstance().getLanguageManager();
                        boolean spaceless = SPACELESS_LANGUAGES.contains(manager.getLanguage(manager.getSelected()) == null ? "en_us" : manager.getSelected());
                        if (!spaceless) {
                            paragraph.substring(linkEnd).split("\\s", 2)[0].substring(1); // substring(1) to remove the @
                        }

                        // The index of the single page currently being formatted, relative to the section
                        int pageRelative = (lines.size() + upToLink.size() - 1) / maxLineNumber;
                        // The overall index of the single page currently being formatted
                        int page = startPage + pageRelative;
                        // The line number on this page
                        int lineNumber = (lines.size() + upToLink.size() - 1) % maxLineNumber;

                        int x = HandBookScreen.isRightPage(page) ? left + HandBookScreen.GUI_WIDTH - HandBookScreen.TEXT_INSET_X - HandBookScreen.PAGE_WIDTH : left + HandBookScreen.TEXT_INSET_X;
                        int y = top + HandBookScreen.TEXT_INSET_Y + lineNumber * font.lineHeight;

                        // Adds any missing sub-lists
                        while (this.buttons.size() <= pageRelative) {
                            this.buttons.add(new ArrayList<>());
                        }

                        // The button id only does what you use it for, so we're just not using it at all.
                        this.buttons.get(pageRelative).add(GuiButtonHyperlink.create(x, y, font, upToLink, arguments, suffix, maxLineNumber - lineNumber - 1, HandBookScreen.isRightPage(page), spaceless, onPress));

                        // The link button should exactly overlay the display text in the main string
                        // If the link has no display text specified, it displays the unformatted target string
                        paragraph = paragraph.replace(linkRaw, arguments[arguments.length - 1]);
                    }

                    lines.addAll(DrawingUtils.listFormattedStringToWidth(font, paragraph, HandBookScreen.PAGE_WIDTH));
                }

                // Line break between paragraphs (the last one will just be deleted later)
                if ((lines.size() % maxLineNumber) != 0) lines.add("");
            }
        }

        // Splits lines into pages

        List<String> page = new ArrayList<>();
        pages.add(page);

        while (!lines.isEmpty()) {

            if (page.size() == maxLineNumber) {
                // Removes blank lines at the end of the page
                while (page.get(page.size() - 1).isEmpty()) page.remove(page.size() - 1);
                // Adds a new page
                pages.add(page = new ArrayList<>());
            }

            String line = lines.remove(0);

            // Prevents blank lines at the start of the page
            if (!page.isEmpty() || !line.isEmpty()) page.add(line);
        }

        return startPage + pages.size();
    }

    /**
     * Called on login and advancement completion to update this section's unlock status and display toast
     * notifications if applicable.
     */
    public void updateUnlockStatus(boolean showToasts, List<ResourceLocation> completedAdvancements) {
        if (triggers == null) return;

        List<ResourceLocation> completed = new ArrayList<>(List.copyOf(completedAdvancements));
        completed.retainAll(Arrays.asList(triggers));

        // Only shows the toast when the section was locked before and is now unlocked
        if (!this.unlocked && !completed.isEmpty() && showToasts && EBClientConfig.HANDBOOK_PROGRESSION.get()) {
            // Mmmmm toast...
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getToasts().addToast(new HandbookToast(this));
            this.isNew = true;
        }

        // Currently, this will not take subsections into account
        this.unlocked = !completed.isEmpty();
    }
}
