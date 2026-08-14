package com.binaris.wizardry.api.content.data;

/// Marks the persistence of a spell variable in the spell manager data.
public enum VarPersistence {
    NEVER(false, false),
    DIMENSION_CHANGE(false, true),
    RESPAWN(true, false),
    ALWAYS(true, true);

    private final boolean persistsOnRespawn;
    private final boolean persistsOnDimensionChange;

    VarPersistence(boolean persistsOnRespawn, boolean persistsOnDimensionChange) {
        this.persistsOnRespawn = persistsOnRespawn;
        this.persistsOnDimensionChange = persistsOnDimensionChange;
    }

    public boolean persistsOnRespawn() {
        return persistsOnRespawn;
    }

    public boolean persistsOnDimensionChange() {
        return persistsOnDimensionChange;
    }
}
