package com.binaris.wizardry.capabilities;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.data.MinionData;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.network.MinionSyncPacketS2C;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class MinionDataHolder implements INBTSerializable<CompoundTag>, MinionData {
    public static final ResourceLocation LOCATION = WizardryMainMod.location("minion_data");
    public static final Capability<MinionDataHolder> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final Mob provider;
    private int lifetime = -1;
    private boolean summoned = false;
    private boolean shouldDeleteGoals = false;
    private boolean shouldFollowOwner = false;
    private @Nullable UUID ownerUUID = null;
    private boolean shouldRestartGoals;

    public MinionDataHolder(Mob mob) {
        this.provider = mob;
    }

    private void sync() {
        if (!this.provider.level().isClientSide()) {
            CompoundTag tag = this.serializeNBT();

            MinionSyncPacketS2C packet = new MinionSyncPacketS2C(this.provider.getId(), tag);
            Services.NETWORK_HELPER.sendToTracking(this.provider, packet);
        }
    }

    @Override
    public Mob getProvider() {
        return this.provider;
    }

    @Override
    public void tick() {
        if (!summoned) return;

        if (goalRestart()) {
            updateGoals();
            markGoalRestart(false);
        }


        if (provider.tickCount > this.getLifetime() && this.getLifetime() > 0) {
            this.provider.discard();
        }

        if (provider.level().isClientSide && provider.level().random.nextInt(8) == 0) {
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(provider.xo, provider.yo + (provider.level().random.nextDouble() * 1.5 + 1), provider.zo).color(0.1f, 0.0f, 0.0f).spawn(provider.level());
        }

        setLifetime(getLifetime() - 1);
        sync();
    }

    @Override
    public int getLifetime() {
        return lifetime;
    }

    @Override
    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public boolean isSummoned() {
        return summoned;
    }

    @Override
    public void setSummoned(boolean summoned) {
        this.summoned = summoned;
        sync();
    }

    @Override
    public boolean shouldDeleteGoals() {
        return shouldDeleteGoals;
    }

    @Override
    public void setShouldDeleteGoals(boolean shouldDeleteGoals) {
        this.shouldDeleteGoals = shouldDeleteGoals;
    }

    @Override
    public boolean shouldFollowOwner() {
        return shouldFollowOwner;
    }

    @Override
    public void setShouldFollowOwner(boolean shouldFollowOwner) {
        this.shouldFollowOwner = shouldFollowOwner;
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
        sync();
    }

    @Override
    public Player getOwner() {
        return ownerUUID == null ? null : provider.level().getPlayerByUUID(ownerUUID);
    }

    @Override
    public void markGoalRestart(boolean shouldRestartGoals) {
        this.shouldRestartGoals = shouldRestartGoals;
    }

    @Override
    public boolean goalRestart() {
        return this.shouldRestartGoals;
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("lifetime", lifetime);
        tag.putBoolean("summoned", summoned);
        tag.putBoolean("shouldDeleteGoals", shouldDeleteGoals);
        tag.putBoolean("shouldFollowOwner", shouldFollowOwner);
        if (ownerUUID != null) {
            tag.putUUID("ownerUUID", ownerUUID);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.lifetime = tag.getInt("lifetime");
        this.summoned = tag.getBoolean("summoned");
        this.shouldDeleteGoals = tag.getBoolean("shouldDeleteGoals");
        this.shouldFollowOwner = tag.getBoolean("shouldFollowOwner");
        if (tag.contains("ownerUUID")) {
            this.ownerUUID = tag.getUUID("ownerUUID");
        }
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        private final LazyOptional<MinionDataHolder> dataHolder;

        public Provider(Mob mob) {
            this.dataHolder = LazyOptional.of(() -> new MinionDataHolder(mob));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
            return MinionDataHolder.INSTANCE.orEmpty(capability, dataHolder.cast());
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
