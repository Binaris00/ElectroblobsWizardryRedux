package com.binaris.wizardry.core.networking.s2c;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.networking.ClientMessageHandler;
import com.binaris.wizardry.core.networking.abst.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SpellGlyphPacketS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("spell_glyph_packet");
    public HashMap<ResourceLocation, String> names;
    public HashMap<ResourceLocation, String> descriptions;


    public SpellGlyphPacketS2C(HashMap<ResourceLocation, String> names, HashMap<ResourceLocation, String> descriptions) {
        this.names = (names != null) ? names : new HashMap<>();
        this.descriptions = (descriptions != null) ? descriptions : new HashMap<>();
    }

    public SpellGlyphPacketS2C(FriendlyByteBuf pBuf) {
        names = new HashMap<>();
        descriptions = new HashMap<>();

        int size = pBuf.readVarInt();

        for (int i = 0; i < size; i++) {
            ResourceLocation key = pBuf.readResourceLocation();
            String name = pBuf.readUtf();
            String description = pBuf.readUtf();
            names.put(key, name);
            descriptions.put(key, description);
        }
    }

    @Override
    public void encode(FriendlyByteBuf pBuf) {
        // use the union of keys to avoid missing entries if one map has extra keys
        Set<ResourceLocation> keys = new HashSet<>(names.keySet());
        keys.addAll(descriptions.keySet());

        pBuf.writeVarInt(keys.size());

        for (ResourceLocation key : keys) {
            pBuf.writeResourceLocation(key);
            String name = names.get(key);
            if (name == null) name = "";
            String desc = descriptions.get(key);
            if (desc == null) desc = "";
            pBuf.writeUtf(name);
            pBuf.writeUtf(desc);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void handleClient() {
        ClientMessageHandler.spellGlyph(this);
    }

    public HashMap<ResourceLocation, String> getDescriptions() {
        return descriptions;
    }

    public HashMap<ResourceLocation, String> getNames() {
        return names;
    }
}
