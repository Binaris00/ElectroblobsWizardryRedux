package com.binaris.wizardry.core;

/// Interface representing event-driven behavior for active mob effects (potions). Classes implementing this interface can
/// override callbacks in [IEventEffect] to apply logic when the entity with the active mob effect undergoes game events
/// (ticking, hurting entities, being hurt, spell casting, etc.).
///
/// @see IEventEffect
/// @see MobEffectContext
public interface IMobEventEffect extends IEventEffect<MobEffectContext> {
}
