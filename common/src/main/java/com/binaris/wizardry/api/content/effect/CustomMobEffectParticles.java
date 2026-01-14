package com.binaris.wizardry.api.content.effect;

import net.minecraft.world.level.Level;

public interface CustomMobEffectParticles {
    void spawnCustomParticle(Level world, double x, double y, double z);

    default boolean shouldMixColour() {
        return false;
    }
}
