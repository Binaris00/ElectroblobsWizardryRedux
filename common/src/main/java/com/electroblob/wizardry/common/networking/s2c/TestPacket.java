package com.electroblob.wizardry.common.networking.s2c;

import com.electroblob.wizardry.common.networking.EBClientHandler;
import com.electroblob.wizardry.common.networking.EBPacket;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class TestPacket extends EBPacket {

    public String message;

    public TestPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(message);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        message = buf.readUtf();
    }

//    @Override
//    public void handle(Supplier<NetworkManager.PacketContext> ctx) {
//        EBClientHandler.handleTestPacket(ctx, this);
//    }

    public static TestPacket create(String message) {
        TestPacket packet = new TestPacket(null);
        packet.message = message;
        return packet;
    }

}
