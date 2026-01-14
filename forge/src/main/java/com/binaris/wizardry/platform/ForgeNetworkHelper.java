package com.binaris.wizardry.platform;

import com.binaris.wizardry.core.networking.abst.Message;
import com.binaris.wizardry.core.platform.services.INetworkHelper;
import com.binaris.wizardry.network.EBForgeNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class ForgeNetworkHelper implements INetworkHelper {
    @Override
    public <T extends Message> void sendTo(ServerPlayer pPlayer, T pMessage) {
        EBForgeNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> pPlayer), pMessage);
    }

    @Override
    public <T extends Message> void sendToServer(T pMessage) {
        EBForgeNetwork.INSTANCE.sendToServer(pMessage);
    }

    @Override
    public <T extends Message> void sendToTracking(ServerLevel pLevel, BlockPos pPos, T pMessage) {
        EBForgeNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> pLevel.getChunkAt(pPos)), pMessage);
    }

    @Override
    public <T extends Message> void sendToTracking(Entity pEntity, T pMessage) {
        EBForgeNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> pEntity), pMessage);
    }

    @Override
    public <T extends Message> void sendToDimension(MinecraftServer server, T packet, ResourceKey<Level> dimension) {
        if (server == null) return;
        EBForgeNetwork.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dimension), packet);
    }
}
