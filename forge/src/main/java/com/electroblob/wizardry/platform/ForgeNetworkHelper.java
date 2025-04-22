package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.core.networking.abst.Message;
import com.electroblob.wizardry.core.platform.services.INetworkHelper;
import com.electroblob.wizardry.network.EBForgeNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
}
