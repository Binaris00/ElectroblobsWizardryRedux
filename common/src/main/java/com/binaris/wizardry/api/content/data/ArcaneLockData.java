package com.binaris.wizardry.api.content.data;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ArcaneLockData {
    String NBT_KEY = "arcaneLockOwner";

    boolean isArcaneLocked();

    void setArcaneLockOwner(String ownerUUID);

    void clearArcaneLockOwner();

    @Nullable UUID getArcaneLockOwnerUUID();
}
