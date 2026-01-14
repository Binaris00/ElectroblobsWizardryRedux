package com.binaris.wizardry.client;

import com.binaris.wizardry.core.networking.abst.Message;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Handles messages received on the client side, we may only call client-side methods from here because we don't want to
 * accidentally reference client-only code on the server side.
 */
@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    public static void handleClientPacket(Message packet) {
        packet.handleClient();
    }
}
