package com.binaris.wizardry.api.client.util;

import com.binaris.wizardry.content.data.SpellGlyphData;

/// Client-side singleton that holds the per-world glyph data received from the server.
public final class GlyphClientHandler {
    public static GlyphClientHandler INSTANCE = new GlyphClientHandler();
    private SpellGlyphData glyphData;

    private GlyphClientHandler() {
    }

    /// Returns the currently stored glyph data, or null if not yet received from the server.
    ///
    /// @return the client's copy of spell glyph data, or null before the first sync.
    public SpellGlyphData getGlyphData() {
        return glyphData;
    }

    /// Stores the glyph data received from a {@code SpellGlyphPacketS2C}.
    ///
    /// @param glyphData the glyph data to store.
    public void setGlyphData(SpellGlyphData glyphData) {
        this.glyphData = glyphData;
    }
}
