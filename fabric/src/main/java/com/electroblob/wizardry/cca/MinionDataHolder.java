package com.electroblob.wizardry.cca;

import com.electroblob.wizardry.api.MinionData;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.NotNull;

public class MinionDataHolder implements MinionDataComponent, AutoSyncedComponent {
    MinionData minionData;
    Mob provider;

    public MinionDataHolder(Mob mob) {
        this.provider = mob;
        this.minionData = new MinionData(provider);
    }

    @Override
    public MinionData getMinionData() {
        return this.minionData;
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag) {
        this.minionData = minionData.deserializeNBT(tag);
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        minionData.serializeNBT(tag);
    }
}
