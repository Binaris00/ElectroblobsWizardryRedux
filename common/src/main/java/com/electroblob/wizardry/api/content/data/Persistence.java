package com.electroblob.wizardry.api.content.data;

public enum Persistence {

    NEVER(false, false),
    DIMENSION_CHANGE(false, true),
    RESPAWN(true, false),
    ALWAYS(true, true);

    private boolean persistsOnRespawn, persistsOnDimensionChange;

    Persistence(boolean persistsOnRespawn, boolean persistsOnDimensionChange) {
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
