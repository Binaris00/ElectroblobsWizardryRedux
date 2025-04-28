package com.electroblob.wizardry.network;

import com.electroblob.wizardry.core.networking.abst.Message;
import com.electroblob.wizardry.core.networking.c2s.BlockUsePacketC2S;
import com.electroblob.wizardry.core.networking.c2s.ControlInputPacketC2S;
import com.electroblob.wizardry.core.networking.c2s.SpellAccessPacketC2S;
import com.electroblob.wizardry.core.networking.s2c.SpellGlyphPacketS2C;
import com.electroblob.wizardry.core.networking.s2c.TestParticlePacketS2C;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class EBFabricNetwork {
    public static void registerC2SMessages() {
        registerServerMessage(BlockUsePacketC2S.ID, BlockUsePacketC2S::new);
        registerServerMessage(ControlInputPacketC2S.ID, ControlInputPacketC2S::new);
        registerServerMessage(SpellAccessPacketC2S.ID, SpellAccessPacketC2S::new);
    }

    public static void registerS2CMessages() {
        registerClientMessage(TestParticlePacketS2C.ID, TestParticlePacketS2C::new);
        registerClientMessage(SpellGlyphPacketS2C.ID, SpellGlyphPacketS2C::new);
    }

    private static <T extends Message> void registerClientMessage(ResourceLocation id, Function<FriendlyByteBuf, T> decoder) {
        ClientPlayNetworking.registerGlobalReceiver(id, (client, handler, buf, responseSender) -> {
            T packet = decoder.apply(buf);
            client.execute(() -> packet.handleClient(client, client.player));
        });
    }

    private static <T extends Message> void registerServerMessage(ResourceLocation id, Function<FriendlyByteBuf, T> decoder) {
        ServerPlayNetworking.registerGlobalReceiver(id, ((server, player, handler, buf, responseSender) -> {
            T packet = decoder.apply(buf);
            server.execute(() -> packet.handleServer(server, player));
        }));


    }
}
