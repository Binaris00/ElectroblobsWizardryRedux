package com.binaris.wizardry.core;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Generic interface representing event-driven behaviors triggered by active game contexts. This can represent either the
 * passive effects of equipped artifacts or active mob effects on an entity. Classes implementing this interface can override
 * default callbacks to inject custom behavior on key game events.
 *
 * @param <C> The context type implementing {@link IEffectContext}
 */
public interface IEventEffect<C extends IEffectContext> {
    /**
     * Called every game tick on the effect context is active.
     *
     * @param user    The user of the effect
     * @param level   The level the user is in
     * @param context The effect context (contains the artifact or mob effect instance)
     */
    default void onTick(LivingEntity user, Level level, C context) {

    }

    /**
     * Called when the user is responsible for killing an entity while the context is active.
     *
     * @param user       The user of the effect
     * @param deadEntity The entity that was killed
     * @param source     The damage source
     * @param context    The effect context (contains the artifact or mob effect instance)
     */
    default void onKillEntity(LivingEntity user, LivingEntity deadEntity, DamageSource source, C context) {

    }

    /**
     * Called when the user is responsible for hurting an entity while the context is active.
     *
     * @param user          The user of the effect
     * @param damagedEntity The entity that was hurt
     * @param source        The damage source
     * @param amount        The amount of damage (mutable)
     * @param canceled      Whether the damage event has been canceled
     * @param context       The effect context (contains the artifact or mob effect instance)
     */
    default void onHurtEntity(LivingEntity user, LivingEntity damagedEntity, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, C context) {

    }

    /**
     * Called when the user is hurt while the context is active.
     *
     * @param user     The user of the effect
     * @param source   The damage source
     * @param amount   The amount of damage (mutable)
     * @param canceled Whether the damage event has been canceled
     * @param context  The effect context (contains the artifact or mob effect instance)
     */
    default void onUserHurt(LivingEntity user, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, C context) {

    }

    /**
     * Called before a spell is cast while the effect context is active.
     *
     * @param event   The spell cast event
     * @param context The effect context (contains the artifact or mob effect instance)
     */
    default void onSpellPreCast(SpellCastEvent.Pre event, C context) {
    }

    /**
     * Called after a spell is cast while the effect context is active.
     *
     * @param event   The spell cast event
     * @param context The effect context (contains the artifact or mob effect instance)
     */
    default void onSpellPostCast(SpellCastEvent.Post event, C context) {
    }

}
