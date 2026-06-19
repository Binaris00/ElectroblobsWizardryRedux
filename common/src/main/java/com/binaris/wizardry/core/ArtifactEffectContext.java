package com.binaris.wizardry.core;

import net.minecraft.world.item.ItemStack;

/**
 * Execution context for artifact-based event effects.
 * This class encapsulates the {@link ItemStack} of the artifact that triggered the event.
 */
public class ArtifactEffectContext implements IEffectContext {
    private final ItemStack artifact;

    /**
     * Constructs a new context for an artifact event.
     *
     * @param artifact The artifact item stack
     */
    public ArtifactEffectContext(ItemStack artifact) {
        this.artifact = artifact;
    }

    /**
     * Gets the artifact item stack associated with this event execution.
     *
     * @return The artifact item stack
     */
    public ItemStack getArtifact() {
        return artifact;
    }
}
