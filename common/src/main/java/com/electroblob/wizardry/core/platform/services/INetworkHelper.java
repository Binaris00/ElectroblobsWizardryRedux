package com.electroblob.wizardry.core.platform.services;

import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface INetworkHelper {
    <T extends Message> void sendTo(ServerPlayer pPlayer, T pMessage);

    <T extends Message> void sendToServer(T pMessage);

    <T extends Message> void sendToTracking(ServerLevel pLevel, BlockPos pPos, T pMessage);

    <T extends Message> void sendToTracking(Entity pEntity, T pMessage);
}
