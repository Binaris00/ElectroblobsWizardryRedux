package com.binaris.wizardry.api.client.util;

import com.binaris.wizardry.content.data.SpellGlyphData;

/**
 * Hey! I'm just a client handler for the glyph data
 *
 */
public final class GlyphClientHandler {
    public static GlyphClientHandler INSTANCE = new GlyphClientHandler();
    private SpellGlyphData glyphData;

    private GlyphClientHandler() {
    }

    public SpellGlyphData getGlyphData() {
        return glyphData;
    }

    public void setGlyphData(SpellGlyphData glyphData) {
        this.glyphData = glyphData;
    }
}
