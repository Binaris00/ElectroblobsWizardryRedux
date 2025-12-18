package com.electroblob.wizardry.api.content.spell.internal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * <b>EntityCastContext - Casting Context for Entities</b>
 * <p>
 * Specialized context used when a <b>non-player entity</b> (NPC, mob, minion) casts a spell.
 * This context is essential for spells that can be used by both players and hostile or allied entities.
 * <p>
 * It provides information about the casting entity, the hand used for casting, and an optional target entity.
 *
 * @see CastContext The base class with shared information
 * @see PlayerCastContext For player casts
 * @see com.electroblob.wizardry.api.content.spell.Spell#canCastByEntity() Indicates if a spell can be cast by entities
 */
public class EntityCastContext extends CastContext {
    protected InteractionHand hand;
    // What if I'm a wizard that just want to chill out casting funny spells without damaging someone?
    protected @Nullable LivingEntity target;

    public EntityCastContext(Level world, LivingEntity caster, InteractionHand hand, int castingTicks, @Nullable LivingEntity target, SpellModifiers modifiers) {
        super(world, caster, castingTicks, modifiers);
        this.hand = hand;
        this.target = target;
    }

    public InteractionHand hand() {
        return hand;
    }

    public @Nullable LivingEntity target() {
        return target;
    }

    public void hand(InteractionHand hand) {
        this.hand = hand;
    }

    public void target(@Nullable LivingEntity target) {
        this.target = target;
    }
}
