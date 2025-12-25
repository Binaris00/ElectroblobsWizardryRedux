package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.data.ContainmentData;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.network.ContainmentSyncPacketS2C;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContainmentDataHolder implements INBTSerializable<CompoundTag>, ContainmentData {
    public static final ResourceLocation LOCATION = WizardryMainMod.location("containment_data");
    public static final Capability<ContainmentDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final LivingEntity provider;
    private BlockPos containmentPos = null;

    public ContainmentDataHolder(LivingEntity entity) {
        this.provider = entity;
    }

    private void sync() {
        if (!this.provider.level().isClientSide()) {
            CompoundTag tag = this.serializeNBT();
            ContainmentSyncPacketS2C packet = new ContainmentSyncPacketS2C(this.provider.getId(), tag);
            if (this.provider instanceof ServerPlayer serverPlayer) {
                Services.NETWORK_HELPER.sendTo(serverPlayer, packet);
            } else {
                Services.NETWORK_HELPER.sendToTracking(this.provider, packet);
            }
        }
    }

    @Override
    public LivingEntity getProvider() {
        return this.provider;
    }

    @Override
    public @Nullable BlockPos getContainmentPos() {
        return containmentPos;
    }

    @Override
    public void setContainmentPos(@Nullable BlockPos pos) {
        this.containmentPos = pos;
        sync();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (containmentPos != null) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", containmentPos.getX());
            posTag.putInt("y", containmentPos.getY());
            posTag.putInt("z", containmentPos.getZ());
            tag.put("containmentPos", posTag);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("containmentPos")) {
            CompoundTag posTag = tag.getCompound("containmentPos");
            int x = posTag.getInt("x");
            int y = posTag.getInt("y");
            int z = posTag.getInt("z");
            this.containmentPos = new BlockPos(x, y, z);
        } else {
            this.containmentPos = null;
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<ContainmentDataHolder> dataHolder;

        public Provider(LivingEntity entity) {
            this.dataHolder = LazyOptional.of(() -> new ContainmentDataHolder(entity));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return ContainmentDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return dataHolder.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag arg) {
            dataHolder.orElseThrow(NullPointerException::new).deserializeNBT(arg);
        }
    }
}
