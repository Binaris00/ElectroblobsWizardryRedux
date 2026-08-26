package com.binaris.wizardry.client.gui.screens.handbook;

import com.binaris.wizardry.api.client.util.DrawingUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Image {
    private static final int CAPTION_OFFSET = 4;
    private static final int TEXTURE_INSET_X = 180;
    private static final int BORDER = 1;
    // Final fields are mandatory, the rest are optional
    private final ResourceLocation location;
    private final int width, height;
    // Derived fields, not specifically defined in JSON
    private final Set<int[]> instances = new HashSet<>();
    private int textureWidth, textureHeight;
    private int u = 0, v = 0;
    private String caption = "";
    private boolean border = true;

    private Image(ResourceLocation location, int width, int height) {
        this.location = location;
        this.width = width;
        this.height = height;
    }

    /**
     * Parses the given JSON object and constructs a new {@code Image} from it, setting all the relevant fields
     * and references.
     *
     * @param json A JSON object representing the image to be constructed. This must contain at least a "location"
     *             string.
     * @return The resulting {@code Image} object.
     * @throws JsonSyntaxException if at any point the JSON object is found to be invalid.
     */
    public static Image fromJson(JsonObject json) {
        Image image = new Image(new ResourceLocation(GsonHelper.getAsString(json, "location")),
                GsonHelper.getAsInt(json, "width"), GsonHelper.getAsInt(json, "height"));

        image.u = GsonHelper.getAsInt(json, "u", 0);
        image.v = GsonHelper.getAsInt(json, "v", 0);
        image.textureWidth = GsonHelper.getAsInt(json, "texture_width", image.width);
        image.textureHeight = GsonHelper.getAsInt(json, "texture_height", image.height);
        image.caption = GsonHelper.getAsString(json, "caption", "");
        image.border = GsonHelper.getAsBoolean(json, "border", true);

        return image;
    }

    public static void populate(Map<String, Image> map, JsonObject json) {
        JsonObject sectionsObject = GsonHelper.getAsJsonObject(json, "images");

        // Need to iterate over these since we don't know what they're called or how many there are
        for (Map.Entry<String, JsonElement> entry : sectionsObject.entrySet()) {
            String key = entry.getKey(); // Find out what each element is called, this will be the sections map key

            Image image = fromJson(entry.getValue().getAsJsonObject());
            map.put(key, image);
        }
    }

    /**
     * Returns the width of the image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the total height of the image, including caption if it has one.
     */
    public int getHeight(Font font) {
        return caption.isEmpty() ? height : height + CAPTION_OFFSET + font.lineHeight;
    }

    /**
     * Adds an instance of this image to the list.
     *
     * @param page The index of the <b>single</b> page this image is on.
     * @param x    The x-coordinate of the top-left corner of the image, <i>relative</i> to the top-left corner of the GUI.
     * @param y    The y-coordinate of the top-left corner of the image, <i>relative</i> to the top-left corner of the GUI.
     */
    public void addInstance(int page, int x, int y) {
        instances.add(new int[]{page, x, y});
    }

    /**
     * Removes all instances of this image from the list.
     */
    public void clearInstances() {
        instances.clear();
    }

    /**
     * Draws all instances of this image that are located on the given double-page spread.
     *
     * @param font       The font renderer object.
     * @param doublePage The double-page index of the page to be drawn.
     * @param left       The x coordinate of the left side of the GUI.
     * @param top        The y coordinate of the top of the GUI.
     */
    public void draw(GuiGraphics guiGraphics, Font font, ResourceLocation texture, int doublePage, int left, int top) {
        // Images
        for (int[] instance : instances) {
            if (HandBookScreen.singleToDoublePage(instance[0]) == doublePage) {
                DrawingUtils.drawTexturedRect(guiGraphics, location, left + instance[1], top + instance[2], u, v, width, height, textureWidth, textureHeight);
                guiGraphics.drawString(font, "§o" + caption, left + instance[1] + width / 2 - font.width(caption) / 2,
                        top + instance[2] + height + CAPTION_OFFSET, HandBookScreen.colours.get("caption"), false);
            }
        }

        if (border) {
            // Borders - do this after all the images are drawn so we only have to bind the handbook texture again once
            for (int[] instance : instances) {
                if (HandBookScreen.singleToDoublePage(instance[0]) == doublePage) {
                    // Math.ceil accounts for odd-numbered image dimensions
                    DrawingUtils.drawTexturedFlippedRect(guiGraphics, texture,
                            left + instance[1] - BORDER, top + instance[2] - BORDER,
                            TEXTURE_INSET_X, HandBookScreen.GUI_HEIGHT,
                            width / 2 + BORDER, height / 2 + BORDER,
                            HandBookScreen.TEXTURE_WIDTH, HandBookScreen.TEXTURE_HEIGHT,
                            false, false
                    );
                    DrawingUtils.drawTexturedFlippedRect(guiGraphics, texture,
                            left + instance[1] + width / 2, top + instance[2] - BORDER,
                            TEXTURE_INSET_X, HandBookScreen.GUI_HEIGHT,
                            Mth.ceil(width / 2f) + BORDER, height / 2 + BORDER,
                            HandBookScreen.TEXTURE_WIDTH, HandBookScreen.TEXTURE_HEIGHT,
                            true, false
                    );
                    DrawingUtils.drawTexturedFlippedRect(guiGraphics, texture,
                            left + instance[1] - BORDER, top + instance[2] + height / 2,
                            TEXTURE_INSET_X, HandBookScreen.GUI_HEIGHT,
                            width / 2 + BORDER, Mth.ceil(height / 2f) + BORDER,
                            HandBookScreen.TEXTURE_WIDTH, HandBookScreen.TEXTURE_HEIGHT,
                            false, true
                    );
                    DrawingUtils.drawTexturedFlippedRect(guiGraphics, texture,
                            left + instance[1] + width / 2, top + instance[2] + height / 2,
                            TEXTURE_INSET_X, HandBookScreen.GUI_HEIGHT,
                            Mth.ceil(width / 2f) + BORDER, Mth.ceil(height / 2f) + BORDER,
                            HandBookScreen.TEXTURE_WIDTH, HandBookScreen.TEXTURE_HEIGHT,
                            true, true
                    );
                }
            }
        }
    }
}
