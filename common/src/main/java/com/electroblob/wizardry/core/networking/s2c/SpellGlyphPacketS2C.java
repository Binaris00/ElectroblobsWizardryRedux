package com.electroblob.wizardry.core.networking.s2c;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.util.ClientUtils;
import com.electroblob.wizardry.api.client.util.GlyphClientHandler;
import com.electroblob.wizardry.content.data.SpellGlyphData;
import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public class SpellGlyphPacketS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("spell_glyph_packet");
    public HashMap<ResourceLocation, String> names;
    public HashMap<ResourceLocation, String> descriptions;


    public SpellGlyphPacketS2C(HashMap<ResourceLocation, String> names, HashMap<ResourceLocation, String> descriptions) {
        this.names = names;
        this.descriptions = descriptions;
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
        pBuf.writeVarInt(names.size());

        for (ResourceLocation key : names.keySet()) {
            pBuf.writeResourceLocation(key);
            pBuf.writeUtf(names.get(key));
            pBuf.writeUtf(descriptions.getOrDefault(key, ""));
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        if (GlyphClientHandler.INSTANCE.getGlyphData() == null) {
            GlyphClientHandler.INSTANCE.setGlyphData(new SpellGlyphData());
        }

        ClientUtils.handleGlyphDataPacket(this);
    }

    public HashMap<ResourceLocation, String> getDescriptions() {
        return descriptions;
    }

    public HashMap<ResourceLocation, String> getNames() {
        return names;
    }
}
