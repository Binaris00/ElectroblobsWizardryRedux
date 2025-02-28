package com.electroblob.wizardry.common.networking;

import com.electroblob.wizardry.common.networking.s2c.TestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

/**
 * Packets received by client from server
 */
public final class EBClientHandler {

//    public static void handleTestPacket(Supplier<NetworkManager.PacketContext> ctx, TestPacket packet) {
//        ctx.get().queue(() -> {
//            Minecraft.getInstance().player.sendSystemMessage(Component.literal(packet.message));
//        });
//    }

    private EBClientHandler() {}
}
