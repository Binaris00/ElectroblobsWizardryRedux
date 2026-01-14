package com.binaris.wizardry.network;

import com.binaris.wizardry.core.networking.abst.Message;
import com.binaris.wizardry.core.networking.c2s.BlockUsePacketC2S;
import com.binaris.wizardry.core.networking.c2s.ControlInputPacketC2S;
import com.binaris.wizardry.core.networking.c2s.SpellAccessPacketC2S;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class EBFabricServerNetwork {
    public static void registerC2SMessages() {
        registerServerMessage(BlockUsePacketC2S.ID, BlockUsePacketC2S::new);
        registerServerMessage(ControlInputPacketC2S.ID, ControlInputPacketC2S::new);
        registerServerMessage(SpellAccessPacketC2S.ID, SpellAccessPacketC2S::new);
    }

    private static <T extends Message> void registerServerMessage(ResourceLocation id, Function<FriendlyByteBuf, T> decoder) {
        ServerPlayNetworking.registerGlobalReceiver(id, ((server, player, handler, buf, responseSender) -> {
            T packet = decoder.apply(buf);
            server.execute(() -> packet.handleServer(server, player));
        }));


    }
}
