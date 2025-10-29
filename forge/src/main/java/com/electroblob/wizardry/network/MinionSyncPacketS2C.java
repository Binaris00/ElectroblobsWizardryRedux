package com.electroblob.wizardry.network;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.capabilities.MinionDataHolder;
import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

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
    public void handleClient(Minecraft minecraft, Player player) {
        if(minecraft.level == null) return;

        Entity entity = minecraft.level.getEntity(entityId);
        if(entity == null) return;

        entity.getCapability(MinionDataHolder.INSTANCE).ifPresent(minionData ->
                minionData.deserializeNBT(data));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
