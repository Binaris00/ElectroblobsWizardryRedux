package com.electroblob.wizardry.common.networking;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.common.networking.s2c.TestPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class EBNetwork {

//    private static final NetworkChannel CHANNEL = NetworkChannel.create(Wizardry.location("network"));
//
//    public static void bootstrap() {
//        CHANNEL.register(TestPacket.class, TestPacket::toBytes, TestPacket::new, TestPacket::handle);
//    }
//
//    public static void sendToPlayer(EBPacket packet, Player player) {
//        if(!(player instanceof ServerPlayer serverPlayer)){
//            // Throw error
//            return;
//        }
//        CHANNEL.sendToPlayer(serverPlayer, packet);
//    }
//
//
//    private EBNetwork() {}
}
