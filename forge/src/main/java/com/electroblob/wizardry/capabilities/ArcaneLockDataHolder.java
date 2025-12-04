package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.ArcaneLockData;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.network.ArcaneLockSyncPacketS2C;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ArcaneLockDataHolder implements INBTSerializable<CompoundTag>, ArcaneLockData {
    public static final ResourceLocation LOCATION = WizardryMainMod.location("arcane_lock_data");
    public static final Capability<ArcaneLockDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});
    private UUID ownerUUID = null;
    private final BlockEntity provider;

    public ArcaneLockDataHolder(BlockEntity provider) {
        this.provider = provider;
    }

    private void sync() {
        if (provider.getLevel() != null && !this.provider.getLevel().isClientSide()) {
            CompoundTag tag = this.serializeNBT();
            ArcaneLockSyncPacketS2C packet = new ArcaneLockSyncPacketS2C(this.provider.getBlockPos(), tag);
            Services.NETWORK_HELPER.sendToTracking((net.minecraft.server.level.ServerLevel) this.provider.getLevel(), this.provider.getBlockPos(), packet);
        }
    }

    @Override
    public boolean isArcaneLocked() {
        return ownerUUID != null;
    }

    @Override
    public void setArcaneLockOwner(String ownerUUID) {
        if(ownerUUID == null){
            this.ownerUUID = null;
        } else {
            this.ownerUUID = UUID.fromString(ownerUUID);
        }
        sync();
    }

    @Override
    public void clearArcaneLockOwner() {
        this.ownerUUID = null;
        sync();
    }

    @Override
    public @Nullable UUID getArcaneLockOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if(this.ownerUUID != null) {
            tag.putString(NBT_KEY, this.ownerUUID.toString());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if(tag.contains(NBT_KEY)){
            this.ownerUUID = UUID.fromString(tag.getString(NBT_KEY));
        } else {
            this.ownerUUID = null;
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<ArcaneLockDataHolder> holder;

        public Provider(BlockEntity entity) {
            this.holder = LazyOptional.of(() -> new ArcaneLockDataHolder(entity));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return ArcaneLockDataHolder.INSTANCE.orEmpty(capability, holder.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return holder.orElseThrow(NullPointerException::new).serializeNBT();
        }
        @Override
        public void deserializeNBT(CompoundTag arg) {
            holder.orElseThrow(NullPointerException::new).deserializeNBT(arg);
        }
    }
}
