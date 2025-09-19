package com.electroblob.wizardry.network;

import com.electroblob.wizardry.core.networking.abst.Message;
import com.electroblob.wizardry.core.networking.s2c.SpellGlyphPacketS2C;
import com.electroblob.wizardry.core.networking.s2c.SpellPropertiesSyncS2C;
import com.electroblob.wizardry.core.networking.s2c.TestParticlePacketS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class EBFabricClientNetwork {
    public static void registerS2CMessages() {
        registerClientMessage(TestParticlePacketS2C.ID, TestParticlePacketS2C::new);
        registerClientMessage(SpellGlyphPacketS2C.ID, SpellGlyphPacketS2C::new);
        registerClientMessage(SpellPropertiesSyncS2C.ID, SpellPropertiesSyncS2C::new);
    }

    private static <T extends Message> void registerClientMessage(ResourceLocation id, Function<FriendlyByteBuf, T> decoder) {
        ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buf, responseSender) -> {
            T packet = decoder.apply(buf);
            client.execute(() -> packet.handleClient(client, client.player));
        });
    }
}
