package com.binaris.wizardry.core.networking.c2s;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.networking.abst.Message;
import com.binaris.wizardry.core.networking.s2c.AdvancementSyncPacketS2C;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

/// <b>\[Client-> Server\]</b> Fired on resource reload to request that the server re-sync the player's advancements.
public class RequestAdvancementSyncPacketC2S implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("request_advancement_sync");

    public static RequestAdvancementSyncPacketC2S decode(FriendlyByteBuf buf) {
        return new RequestAdvancementSyncPacketC2S();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        // Don't need to put anything in here!
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        // Just to make sure that the side is correct
        List<ResourceLocation> advancements = new ArrayList<>();

        for (Advancement advancement : server.getAdvancements().getAllAdvancements()) {
            if (player.getAdvancements().getOrStartProgress(advancement).isDone())
                advancements.add(advancement.getId());
        }

        AdvancementSyncPacketS2C packet = new AdvancementSyncPacketS2C(true, advancements);
        Services.NETWORK_HELPER.sendTo(player, packet);
    }
}
