package com.binaris.wizardry.core.networking.abst;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public interface Message {
    ResourceLocation getId();

    void encode(FriendlyByteBuf pBuf);

    default void handleClient() {
    }

    default void handleServer(MinecraftServer server, ServerPlayer player) {
    }
}
