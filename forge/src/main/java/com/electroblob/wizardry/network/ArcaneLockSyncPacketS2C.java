package com.electroblob.wizardry.network;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.capabilities.ArcaneLockDataHolder;
import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ArcaneLockSyncPacketS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("arcane_lock_sync");
    private final BlockPos pos;
    private final CompoundTag data;

    public ArcaneLockSyncPacketS2C(BlockPos pos, CompoundTag data) {
        this.pos = pos;
        this.data = data;
    }

    public ArcaneLockSyncPacketS2C(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.data = buf.readNbt();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeNbt(data);
    }

    @Override
    public void handleClient(Minecraft minecraft, Player player) {
        if(minecraft.level == null) return;

        BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        if(blockEntity == null) return;

        blockEntity.getCapability(ArcaneLockDataHolder.INSTANCE).ifPresent(arcaneLockData ->
                arcaneLockData.deserializeNBT(data));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}

