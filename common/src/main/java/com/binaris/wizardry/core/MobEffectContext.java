package com.binaris.wizardry.core;

import net.minecraft.world.effect.MobEffectInstance;

/**
 * Execution context for mob-effect-based event effects.
 * This class encapsulates the {@link MobEffectInstance} that triggered the event.
 */
public class MobEffectContext implements IEffectContext {
    private final MobEffectInstance effect;

    /**
     * Constructs a new context for a mob effect event.
     *
     * @param effect The active mob effect instance
     */
    public MobEffectContext(MobEffectInstance effect) {
        this.effect = effect;
    }

    /**
     * Gets the active mob effect instance associated with this event execution.
     *
     * @return The active mob effect instance
     */
    public MobEffectInstance getEffect() {
        return effect;
    }
}
