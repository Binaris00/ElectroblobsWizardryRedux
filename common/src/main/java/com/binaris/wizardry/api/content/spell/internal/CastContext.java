package com.binaris.wizardry.api.content.spell.internal;

import com.binaris.wizardry.api.content.spell.Spell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * <b>CastContext - Base Context for Spell Casting</b>
 * <p>
 * Base class that encapsulates the fundamental information needed to cast any spell in the mod.
 * All spell casting context variants inherit from this class, providing a unified interface for the spell system.
 * <p>
 * This class acts as a shared data container between all types of spell casts,
 * regardless of whether the caster is a player, an entity, or a location in the world.
 * <p>
 * <b>Context Hierarchy</b>
 * <pre>
 * CastContext (base)
 *  ├─ PlayerCastContext    - When a player casts from items (wands, scrolls)
 *  ├─ EntityCastContext    - When NPCs/mobs cast spells (with optional target)
 *  └─ LocationCastContext  - When cast from a fixed location (dispensers, constructs)
 * </pre>
 *
 * @see PlayerCastContext For casts initiated by players
 * @see EntityCastContext For casts initiated by entities
 * @see LocationCastContext For casts from fixed locations
 * @see Spell#cast(PlayerCastContext)
 */
public class CastContext {
    protected Level world;
    protected int castingTicks;
    protected SpellModifiers modifiers;
    protected LivingEntity caster;

    public CastContext(Level world, int castingTicks, SpellModifiers modifiers) {
        this.world = world;
        this.castingTicks = castingTicks;
        this.modifiers = modifiers;
        this.caster = null;
    }

    public CastContext(Level world, LivingEntity caster, int castingTicks, SpellModifiers modifiers) {
        this.world = world;
        this.castingTicks = castingTicks;
        this.modifiers = modifiers;
        this.caster = caster;
    }

    public LivingEntity caster() {
        return caster;
    }

    public Level world() {
        return world;
    }

    public int castingTicks() {
        return castingTicks;
    }

    public SpellModifiers modifiers() {
        return modifiers;
    }

    public void castingTicks(int tick) {
        this.castingTicks = tick;
    }

    public void modifiers(SpellModifiers modifiers) {
        this.modifiers = modifiers;
    }
}
