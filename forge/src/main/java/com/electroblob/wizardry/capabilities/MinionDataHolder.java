package com.electroblob.wizardry.capabilities;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.MinionData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinionDataHolder implements INBTSerializable<CompoundTag> {
    public static final Capability<MinionDataHolder> MINION_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    MinionData minionData;
    Mob provider;

    public MinionDataHolder(Mob mob) {
        this.provider = mob;
        this.minionData = new MinionData(provider);
    }

    @Override
    public CompoundTag serializeNBT() {
        return minionData.serializeNBT(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        this.minionData = minionData.deserializeNBT(tag);
    }

    public MinionData getMinionData() {
        return minionData;
    }

    // ====================================================
    // Capability stuff
    // ====================================================
    public static void attachCapability(AttachCapabilitiesEvent<Entity> e) {
        if(e.getObject() instanceof Mob mob) {
            e.addCapability(WizardryMainMod.location("minion_data"), new MinionDataHolder.Provider(mob));
        }
    }

    public static MinionDataHolder get(Mob mob) {
        return mob.getCapability(MINION_DATA_CAPABILITY).orElse(new MinionDataHolder(mob));
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final LazyOptional<MinionDataHolder> data;

        public Provider(Mob mob) {
            this.data = LazyOptional.of(() -> new MinionDataHolder(mob));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
            return MINION_DATA_CAPABILITY.orEmpty(capability, data.cast());
        }

        @Override
        public CompoundTag serializeNBT() {
            return data.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            data.orElseThrow(NullPointerException::new).deserializeNBT(tag);
        }
    }
}
