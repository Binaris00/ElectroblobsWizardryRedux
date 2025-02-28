package com.electroblob.wizardry.common.networking;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public abstract class EBPacket {

    public EBPacket(FriendlyByteBuf buf) {
        if(buf != null) fromBytes(buf);
    }

    public abstract void toBytes(FriendlyByteBuf buf);
    public abstract void fromBytes(FriendlyByteBuf buf);
    //public abstract void handle(Supplier<NetworkManager.PacketContext> ctx);
}
