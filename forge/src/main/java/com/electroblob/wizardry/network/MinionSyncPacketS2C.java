package com.electroblob.wizardry.network;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class MinionSyncPacketS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("minion_sync");
    private final int entityId;
    private final CompoundTag data;

    public MinionSyncPacketS2C(int entityId, CompoundTag data) {
        this.entityId = entityId;
        this.data = data;
    }

    public MinionSyncPacketS2C(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeNbt(data);
    }

    @Override
    public void handleClient() {
        ClientMessageHandlerForge.minionSync(this);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public CompoundTag getData() {
        return data;
    }

    public int getEntityId() {
        return entityId;
    }
}
