package com.binaris.wizardry.core.networking.c2s;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.networking.abst.Message;
import com.binaris.wizardry.core.networking.s2c.AdvancementSyncPacketS2C;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

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
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        List<Advancement> advancements = new ArrayList<>();
        Map<ResourceLocation, AdvancementProgress> progresses = new HashMap<>();
        List<ResourceLocation> resourceLocations = new ArrayList<>();

        for (Advancement advancement : server.getAdvancements().getAllAdvancements()) {
            if (player.getAdvancements().getOrStartProgress(advancement).isDone())
                advancements.add(advancement);
        }

        for (Advancement advancement : advancements) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            progresses.put(advancement.getId(), progress);
            resourceLocations.add(advancement.getId());
        }

        ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(false, advancements, Set.of(), progresses);
        AdvancementSyncPacketS2C packet2 = new AdvancementSyncPacketS2C(true, resourceLocations.toArray(new ResourceLocation[0]));
        Services.NETWORK_HELPER.sendToServer(packet2);
        player.connection.send(packet);
    }
}
