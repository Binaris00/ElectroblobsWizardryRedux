package com.electroblob.wizardry.cca.blockentity;

import com.electroblob.wizardry.api.content.data.ArcaneLockData;
import com.electroblob.wizardry.cca.EBComponents;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ArcaneLockDataHolder implements ArcaneLockData, ComponentV3, AutoSyncedComponent {
    private UUID ownerUUID = null;
    private final BaseContainerBlockEntity provider;

    public ArcaneLockDataHolder(BaseContainerBlockEntity provider) {
        this.provider = provider;
    }

    public void sync(){
        EBComponents.ARCANE_LOCK_DATA.sync(provider);
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
    public @Nullable UUID getArcaneLockOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag) {
        if(tag.contains(NBT_KEY)){
            this.ownerUUID = UUID.fromString(tag.getString(NBT_KEY));
        } else {
            this.ownerUUID = null;
        }
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        if(this.ownerUUID != null){
            tag.putString(NBT_KEY, this.ownerUUID.toString());
        }
    }
}
