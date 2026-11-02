package com.binaris.wizardry.core.networking.c2s;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.networking.abst.Message;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/// Sent by the client once it has fully joined a world, asking the server to send all per-player data again.
public class RequestPlayerDataSyncC2S implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("request_player_data_sync");

    public RequestPlayerDataSyncC2S() {
    }

    public RequestPlayerDataSyncC2S(FriendlyByteBuf buf) {
    }

    @Override
    public void encode(FriendlyByteBuf pBuf) {
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        if (player == null) return;
        Services.OBJECT_DATA.syncPlayerData(player);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
