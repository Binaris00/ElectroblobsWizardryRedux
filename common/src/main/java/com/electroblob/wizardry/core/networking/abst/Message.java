package com.electroblob.wizardry.core.networking.abst;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface Message {
    ResourceLocation getId();

    void encode(FriendlyByteBuf pBuf);

    default void handleClient(Minecraft minecraft, Player player) {
    }

    default void handleServer(MinecraftServer server, ServerPlayer player) {
    }
}
