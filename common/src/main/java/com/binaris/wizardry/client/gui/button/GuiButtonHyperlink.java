package com.binaris.wizardry.client.gui.button;

import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.client.util.DrawingUtils;
import com.binaris.wizardry.client.gui.screens.handbook.HandBookScreen;
import com.binaris.wizardry.client.gui.screens.handbook.Section;
import com.binaris.wizardry.setup.registries.EBSounds;
import com.google.gson.JsonSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuiButtonHyperlink extends Button {
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    /**
     * Pulse period of links to new sections, in milliseconds.
     */
    private static final float PULSATION_PERIOD = 1500;
    private final int indent;
    private final List<String> lines;
    private final int linesLeft;
    private final Font font;

    public GuiButtonHyperlink(int x, int y, Font font, String text, int indent, String suffix, int linesLeft, boolean rightPage, boolean spaceless, OnPress onPress) {
        super(x, y, font.width(text), font.lineHeight, Component.empty(), onPress, DEFAULT_NARRATION);
        this.font = font;
        // Sometimes a link has punctuation or something after it that causes it to wrap onto a new line
        String linkWithSuffix = text + suffix;
        // If the string won't fit any words (or characters in a spaceless language) at the end of the current line,
        // treat it as if we started a new line
        String firstWord = spaceless ? linkWithSuffix.substring(0, 1) : linkWithSuffix.split("\\s")[0];
        if (font.width(firstWord) > HandBookScreen.PAGE_WIDTH - indent) {
            indent = 0;
            this.setY(this.getY() + font.lineHeight);
        }

        this.indent = indent; // Assigned here in case it was corrected above
        this.linesLeft = linesLeft;

        String line1 = DrawingUtils.listFormattedStringToWidth(font, linkWithSuffix, HandBookScreen.PAGE_WIDTH - indent).get(0);
        // Without trim(), there will be at least 1 leading space due to the custom wrapping
        String remainder = linkWithSuffix.substring(line1.length()).trim();

        // ... then wrap the rest to the normal width.
        lines = new ArrayList<>();
        lines.add(line1);
        // Some links are only one line, if this wasn't checked they would cause a StackOverflowError
        if (!remainder.isEmpty())
            lines.addAll(DrawingUtils.listFormattedStringToWidth(font, remainder, HandBookScreen.PAGE_WIDTH));

        // Removes the suffix if it exists (ugly as heck, but it works)
        if (!suffix.isEmpty()) {
            for (int i = lines.size() - 1; i >= 0; i--) {
                String line = lines.get(i);
                if (suffix.endsWith(line)) {
                    lines.remove(i);
                } else if (line.endsWith(suffix)) {
                    lines.set(i, line.substring(0, line.length() - suffix.length()));
                    break;
                }
            }
        }

        // Remove any lines that overflowed onto the next double-page
        if (rightPage) {
            while (lines.size() > linesLeft) lines.remove(lines.size() - 1);
        }
    }

    /**
     * Creates a new hyperlink button from the given arguments, automatically differentiating between URLs and sections.
     *
     * @param x         The x position of the button
     * @param y         The y position of the button
     * @param font      A reference to the FontRenderer object
     * @param upToLink  The paragraph (as a list of lines) up to the link, used to determine positioning and word wrap
     * @param arguments The link arguments - that is, everything between the two @ signs, split by spaces
     * @param suffix    The text directly after the link, up to the first whitespace; used for word wrap. Usually this is
     *                  either empty or contains a single punctuation mark.
     * @return The resulting button
     * @throws IllegalArgumentException if the given argument array is empty or contains more than 2 arguments
     * @throws JsonSyntaxException      if the specified link target is not a URL or a valid section ID
     */
    public static GuiButtonHyperlink create(int x, int y, Font font, List<String> upToLink, String[] arguments, String suffix, int linesLeft, boolean rightPage, boolean spaceless, OnPress onPress) {
        if (arguments.length == 0 || arguments.length > 2)
            throw new IllegalArgumentException("Incorrect array length!");

        GuiButtonHyperlink button;
        if (arguments[0].matches(URL_REGEX)) {
            button = new GuiButtonHyperlink.External(x, y, font, arguments[arguments.length - 1], arguments[0],
                    font.width(upToLink.get(upToLink.size() - 1)), suffix, linesLeft, rightPage, spaceless, onPress);
        } else {
            Section target = HandBookScreen.sections.get(arguments[0]);

            if (target == null)
                throw new JsonSyntaxException("Hyperlink points to nonexistent section id " + arguments[0]);

            button = new GuiButtonHyperlink.Internal(x, y, font, arguments[arguments.length - 1],
                    target, font.width(upToLink.get(upToLink.size() - 1)), suffix, linesLeft, rightPage, spaceless, onPress);

        }
        return button;
    }

    public boolean isHovered(Font font, double mouseX, double mouseY) {
        int i = 0;
        for (String line : lines) {
            int l = this.getX();
            if (i == 0) l += indent;
            int t = getY() + font.lineHeight * i;

            if (i > linesLeft) {
                l = l + HandBookScreen.GUI_WIDTH - 2 * HandBookScreen.TEXT_INSET_X - HandBookScreen.PAGE_WIDTH;
                t -= HandBookScreen.PAGE_HEIGHT - (HandBookScreen.PAGE_HEIGHT % font.lineHeight);
            }

            if (mouseX >= l && mouseY >= t && mouseX < l + font.width(line) && mouseY < t + font.lineHeight)
                return true;

            i++;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.active && this.visible && isHovered(this.font, mouseX, mouseY);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            this.isHovered = isHovered(this.font, mouseX, mouseY);
            int i = 0;
            for (String line : lines) {
                int l = getX();
                if (i == 0) l += indent;

                int t = getY() + font.lineHeight * i;

                if (i > linesLeft) {
                    l = l + HandBookScreen.GUI_WIDTH - 2 * HandBookScreen.TEXT_INSET_X - HandBookScreen.PAGE_WIDTH;
                    t -= HandBookScreen.PAGE_HEIGHT - (HandBookScreen.PAGE_HEIGHT % font.lineHeight);
                }

                guiGraphics.drawString(font, line, l, t, getColour(), false);

                i++;
            }
        }
    }

    protected int getColour() {
        return this.isHovered ? HandBookScreen.colours.get("highlight") : HandBookScreen.colours.get("hyperlink");
    }

    public static class Internal extends GuiButtonHyperlink {
        public final Section target;

        public Internal(int x, int y, Font font, String text, Section target, int indent, String suffix, int linesLeft, boolean rightPage, boolean spaceless, OnPress onPress) {
            super(x, y, font, text, indent, suffix, linesLeft, rightPage, spaceless, onPress);
            this.target = target;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!target.isUnlocked()) return false;
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void playDownSound(@NotNull SoundManager handler) {
            handler.play(SimpleSoundInstance.forUI(EBSounds.MISC_PAGE_TURN.get(), 1));
        }

        @Override
        protected int getColour() {
            if (!target.isUnlocked()) return HandBookScreen.colours.get("text");

            if (!this.isHovered && target.isNew() && !Minecraft.getInstance().player.isCreative()) {
                int c = HandBookScreen.colours.get("new_section");
                int d = HandBookScreen.colours.get("hyperlink");
                float f = (Mth.sin((Minecraft.getInstance().getDeltaFrameTime() % PULSATION_PERIOD) / PULSATION_PERIOD * 2 * (float) Math.PI) + 1) / 2f;

                return ClientUtils.mixColor(c, d, f);
            }
            return super.getColour();
        }

    }

    public static class External extends GuiButtonHyperlink {
        public final Component link;

        public External(int x, int y, Font font, String text, String url, int indent, String suffix, int linesLeft, boolean rightPage, boolean spaceless, OnPress onPress) {
            super(x, y, font, text, indent, suffix, linesLeft, rightPage, spaceless, onPress);
            this.link = Component.literal(url);
            link.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)).withColor(TextColor.fromLegacyFormat(ChatFormatting.DARK_BLUE));
        }
    }
}
