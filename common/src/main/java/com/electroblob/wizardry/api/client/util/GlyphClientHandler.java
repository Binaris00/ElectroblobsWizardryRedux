package com.electroblob.wizardry.api.client.util;

import com.electroblob.wizardry.content.data.SpellGlyphData;

/**
 * Hey! I'm just a client handler for the glyph data
 * */
public final class GlyphClientHandler {
    private SpellGlyphData glyphData;
    public static GlyphClientHandler INSTANCE = new GlyphClientHandler();

    private GlyphClientHandler() {}

    public SpellGlyphData getGlyphData() {
        return glyphData;
    }

    public void setGlyphData(SpellGlyphData glyphData) {
        this.glyphData = glyphData;
    }
}
