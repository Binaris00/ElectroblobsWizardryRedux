package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.core.networking.abst.Message;
import com.electroblob.wizardry.core.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;

public class FabricNetworkHelper implements INetworkHelper {
    @Override
    public <T extends Message> void sendTo(ServerPlayer pPlayer, T pMessage) {
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        pMessage.encode(byteBuf);
        ServerPlayNetworking.send(pPlayer, pMessage.getId(), byteBuf);
    }

    @Override
    public <T extends Message> void sendToServer(T pMessage) {
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        pMessage.encode(byteBuf);
        ClientPlayNetworking.send(pMessage.getId(), byteBuf);
    }

    @Override
    public <T extends Message> void sendToTracking(ServerLevel pLevel, BlockPos pPos, T pMessage) {
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        pMessage.encode(byteBuf);

        LevelChunk chunk = pLevel.getChunkAt(pPos);
        pLevel.getChunkSource().chunkMap.getPlayers(chunk.getPos(), false).forEach(serverPlayer -> {
            ServerPlayNetworking.send(serverPlayer, pMessage.getId(), byteBuf);
        });
    }

    @Override
    public <T extends Message> void sendToTracking(Entity pEntity, T pMessage) {
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        pMessage.encode(byteBuf);

        ((ServerChunkCache) pEntity.getCommandSenderWorld().getChunkSource())
                .broadcast(pEntity, ServerPlayNetworking.createS2CPacket(pMessage.getId(), byteBuf));
    }
}
