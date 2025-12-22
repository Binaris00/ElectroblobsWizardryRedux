package com.electroblob.wizardry.cca.entity;

import com.electroblob.wizardry.api.content.data.ContainmentData;
import com.electroblob.wizardry.cca.EBComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContainmentDataHolder implements ContainmentData, ComponentV3, AutoSyncedComponent {
    private final LivingEntity provider;
    private BlockPos containmentPos = null;

    public ContainmentDataHolder(LivingEntity provider) {
        this.provider = provider;
    }

    public void sync() {
        EBComponents.CONTAINMENT_DATA.sync(provider);
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
    public void readFromNbt(@NotNull CompoundTag tag) {
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

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        if (containmentPos != null) {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", containmentPos.getX());
            posTag.putInt("y", containmentPos.getY());
            posTag.putInt("z", containmentPos.getZ());
            tag.put("containmentPos", posTag);
        }
    }
}
