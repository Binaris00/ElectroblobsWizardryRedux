package com.electroblob.wizardry.api.content.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Represents containment data for living entities affected by the containment effect.
 * This data is used by the Shrine structure and Containment spell to restrict entity movement
 * within a specific area.
 */
public interface ContainmentData {
    /**
     * Gets the living entity that this containment data is associated with.
     *
     * @return the living entity provider
     */
    LivingEntity getProvider();

    /**
     * Gets the containment position for the containment effect, used for the Shrine structure and Containment spell.
     *
     * @return The BlockPos representing the containment position, or null if not contained.
     */
    @Nullable
    BlockPos getContainmentPos();

    /**
     * Sets the containment position for the containment effect, used for the Shrine structure and Containment spell.
     *
     * @param pos The BlockPos to set as the containment position, or null to clear it.
     */
    void setContainmentPos(@Nullable BlockPos pos);
}

