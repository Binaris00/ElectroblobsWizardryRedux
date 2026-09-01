package com.binaris.wizardry.client.gui.screens.handbook;

import com.binaris.wizardry.api.client.util.DrawingUtils;
import com.binaris.wizardry.api.content.util.JavaUtils;
import com.binaris.wizardry.client.gui.button.GuiButtonHyperlink;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/// Instances of this class represent tables of contents in the wizard's handbook. Each {@link Section} can have a
/// single table of contents, which can reference any other sections in the handbook (though it is normal to list
/// top-level sections in a main contents and have subsections listed in their respective parent sections' contents).
/// <br>
/// This class handles JSON parsing, formatting and drawing of the contents itself, working on a line-by-line basis
/// (as opposed to sections, which work on a page-by-page basis). It also stores its own list of buttons.
/// 
/// @author Electroblob
/// @author Crystal
/// @since Wizardry 0.8.6-alpha
public class Contents {
    // Final fields are mandatory, the rest are optional
    public final String id;
    public final Section section;
    private final List<List<Button>> buttons;
    private final List<Section> entries;
    private boolean hyperlinks = true;
    private boolean pageNumbers = true;
    private String separator = ".";
    // Derived fields, not specifically defined in JSON
    private int startPage;
    private int startLine;
    private List<Section> visibleEntries;

    private Contents(String id, Section section) {
        this.id = id;
        this.section = section;
        this.entries = new ArrayList<>();
        this.buttons = new ArrayList<>();
        this.visibleEntries = new ArrayList<>();
    }

    /// Parses the given JSON object and constructs a new {@code Contents} from it, setting all the relevant fields
    /// and references.
    /// 
    /// @param parent The parent section for this contents.
    /// @param json A JSON object representing the contents to be constructed. This must contain at least an "id" string.
    /// @return The resulting {@code Contents} object.
    /// @throws JsonSyntaxException if at any point the JSON object is found to be invalid.

    public static Contents fromJson(Section parent, JsonObject json) {

        Contents contents = new Contents(GsonHelper.getAsString(json, "id"), parent);

        contents.hyperlinks = GsonHelper.getAsBoolean(json, "hyperlinks", true);
        contents.pageNumbers = GsonHelper.getAsBoolean(json, "page_numbers", true);
        contents.separator = GsonHelper.getAsString(json, "separator", ".");

        return contents;
    }

    /// Returns an unmodifiable, flattened collection of all the buttons in this contents.
    public Collection<Button> getButtons() {
        return JavaUtils.flatten(buttons);
    }

    public void addEntry(Section section) {
        this.entries.add(section);
    }

    /// Draws this contents for the given double-page spread and shows/hides buttons accordingly. Will draw nothing
    /// if the given page is outside of this contents.
    /// 
    /// @param font       The font renderer object.
    /// @param doublePage The index of the <b>double-page</b> to be drawn.
    /// @param left       The x coordinate of the left side of the GUI.
    /// @param top        The y coordinate of the top of the GUI.
    public void draw(GuiGraphics guiGraphics, Font font, int doublePage, int left, int top) {
        // Show/hide buttons
        int i = 0;

        for (List<Button> list : buttons) {
            final int i1 = i++;
            list.forEach(b -> b.visible = HandBookScreen.singleToDoublePage(startPage + i1) == doublePage);
        }

        if (!pageNumbers) return; // No page numbers means only the buttons are drawn
        // FONT_HEIGHT may change between fonts, so this is calculated here. With the default font it's 14.
        final int maxLineNumber = HandBookScreen.PAGE_HEIGHT / font.lineHeight;

        int leftIndex = HandBookScreen.doubleToSinglePage(doublePage, false);
        // Relative indices of the pages to be rendered - often these will be outside the section entirely
        int[] visiblePages = {leftIndex - startPage, leftIndex - startPage + 1};

        for (int page : visiblePages) {
            if (page >= 0 && page < visibleEntries.size() / maxLineNumber + 1) {
                int x = left + (HandBookScreen.isRightPage(startPage + page) ? HandBookScreen.GUI_WIDTH - HandBookScreen.TEXT_INSET_X - HandBookScreen.PAGE_WIDTH : HandBookScreen.TEXT_INSET_X);
                int y = top + HandBookScreen.TEXT_INSET_Y + startLine * font.lineHeight;

                for (Section entry : this.visibleEntries) {
                    if (entry.isUnlocked()) {
                        int nameWidth = font.width(entry.title);
                        String dotsAndNumber = " " + entry.startPage;

                        while (font.width(dotsAndNumber) < HandBookScreen.PAGE_WIDTH - nameWidth - 2) {
                            dotsAndNumber = separator + dotsAndNumber;
                        }

                        guiGraphics.drawString(font, dotsAndNumber, x + HandBookScreen.PAGE_WIDTH - font.width(dotsAndNumber), y, DrawingUtils.BLACK, false);

                        if (!hyperlinks) guiGraphics.drawString(font, entry.title, x, y, DrawingUtils.BLACK, false);
                        y += font.lineHeight;
                    }
                }
            }
        }
    }

    /// Called on GUI load to format the section and all subsections, contents tables and other elements. Does not
    /// perform any actual drawing.
    /// 
    /// @param font      The font renderer object, for measurement purposes.
    /// @param startPage The index of the first page (single side, not double-page) of this section.
    /// @param startLine The index of the first line of this contents.
    /// @param left      The x coordinate of the left side of the GUI.
    /// @param top       The y coordinate of the top of the GUI.
    /// @return The number of lines this contents takes up.
    /// @throws JsonSyntaxException if at any point the formatting is found to be invalid.
    public int format(Button.OnPress onPress, Font font, int startPage, int startLine, int left, int top) {
        this.buttons.clear();
        this.visibleEntries = new ArrayList<>(entries); // Need to copy the collection first!
        this.visibleEntries.removeIf(s -> !s.isUnlocked());

        if (hyperlinks) {
            // FONT_HEIGHT may change between fonts, so this is calculated here. With the default font it's 14.
            final int maxLineNumber = HandBookScreen.PAGE_HEIGHT / font.lineHeight;

            this.startPage = startPage;
            this.startLine = startLine;

            List<Button> list = new ArrayList<>(maxLineNumber);
            for (Section entry : this.visibleEntries) {
                int x = HandBookScreen.isRightPage(startPage) ? left + HandBookScreen.GUI_WIDTH - HandBookScreen.TEXT_INSET_X - HandBookScreen.PAGE_WIDTH : left + HandBookScreen.TEXT_INSET_X;
                int y = top + HandBookScreen.TEXT_INSET_Y + startLine * font.lineHeight;

                list.add(new GuiButtonHyperlink.Internal(x, y, font, entry.title, entry, 0, "", maxLineNumber - startLine, HandBookScreen.isRightPage(startPage), false, onPress));
                startLine++;

                if (startLine == maxLineNumber) {
                    startLine = 0;
                    startPage++;
                    buttons.add(list);
                    list = new ArrayList<>(maxLineNumber); // If there are no more entries this will be discarded anyway
                }
            }

            buttons.add(list);
        }
        // Returning this is kind of trivial at the moment but if we ever wanted to add a header or something,
        // it would be more useful.
        return visibleEntries.size();
    }
}
