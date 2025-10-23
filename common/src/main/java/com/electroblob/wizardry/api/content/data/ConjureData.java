package com.electroblob.wizardry.api.content.data;

/**
 * This is the interface that we will use later to interact with the conjure item data!! Loading this as an interface and
 * later implemented by the loaders from "scratch" is basically because we need a similar way to load-change data for
 * Fabric and forge, with this we're trying to create a mask that we will trust that the loaders won't have any problem
 * saving and handling the data.
 *
 * @see com.electroblob.wizardry.core.mixin.ConjureMixin
 */
public interface ConjureData {
    void tick();

    void lifetimeDecrement();

    int getLifetime();

    void setLifetime(int lifetime);

    int getMaxLifetime();

    void setMaxLifetime(int maxLifetime);

    boolean isSummoned();

    void setSummoned(boolean summoned);
}
