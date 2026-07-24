package com.binaris.wizardry.api.content.data;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/// Interface for arcane lock ownership data attached to container blocks.
///
/// Stores the UUID of the player who placed an arcane lock on a block (chest, pedestal, etc.).
/// The lock is considered active when an owner UUID is set; null means the block is unlocked.
///
/// Implementations handle syncing between server and client automatically when the owner changes.
///
/// '@see com.binaris.wizardry.cca.blockentity.ArcaneLockDataHolder (Fabric)'
/// '@see com.binaris.wizardry.capabilities.ArcaneLockDataHolder (Forge)'
public interface ArcaneLockData {
    String NBT_KEY = "arcaneLockOwner";

    /// Returns true if the block has an arcane lock owner set.
    /// Shorthand for checking whether the lock is active.
    ///
    /// @return true if locked (owner UUID is non-null), false otherwise
    boolean isArcaneLocked();

    /// Sets the arcane lock owner. Passing null clears the lock (unlocks the block).
    ///
    /// @param ownerUUID The owner's UUID as a string, or null to unlock
    void setArcaneLockOwner(String ownerUUID);

    /// Clears the arcane lock owner, unlocking the block.
    /// Equivalent to calling {@code setArcaneLockOwner(null)}.
    void clearArcaneLockOwner();

    /// Returns the UUID of the player who owns the arcane lock, or null if not locked.
    ///
    /// @return The owner's UUID, or null if the block is not arcane locked
    @Nullable UUID getArcaneLockOwnerUUID();
}
