package com.binaris.wizardry.core;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.ArtifactItem;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Interface representing the effects of artifacts in the mod.
 * This provides methods that can be overridden to define custom behavior for artifacts based on events. These methods
 * are loaded by the {@link ArtifactItem ArtifactItem} class to register the effects
 * inside the event bus.
 * <p>
 * You have all the freedom to add a custom implementation of this interface for your own artifacts, but if you do so,
 * you must ensure to load the events yourself.
 */
public interface IArtifactEffect {
    /**
     * Called when the player is responsible for killing an entity (if player carries the artifact in their hotbar or accessories)
     * to apply the artifact's effect.
     *
     * @param player   The player wearing the artifact
     * @param level    The level the player is in
     * @param artifact The artifact stack
     */
    default void onTick(Player player, Level level, ItemStack artifact) {
    }

    /**
     * Called when the player is responsible for killing an entity (if player carries the artifact in their hotbar or accessories)
     * to apply the artifact's effect.
     *
     * @param player     The player wearing the artifact
     * @param deadEntity The entity that was killed
     * @param source     The damage source
     * @param artifact   The artifact stack
     */
    default void onKillEntity(Player player, LivingEntity deadEntity, DamageSource source, ItemStack artifact) {
    }

    /**
     * Called when the player is responsible for hurting an entity (if player carries the artifact in their hotbar or accessories)
     * to apply the artifact's effect.
     *
     * @param player        The player wearing the artifact
     * @param damagedEntity The entity that was hurt
     * @param source        The damage source
     * @param amount        The amount of damage (mutable)
     * @param canceled      Whether the damage event has been canceled
     * @param artifact      The artifact stack
     */
    default void onHurtEntity(Player player, LivingEntity damagedEntity, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
    }

    /**
     * Called when the player is hurt (if player carries the artifact in their hotbar or accessories) to apply the artifact's effect
     *
     * @param player  The player wearing the artifact
     * @param source  The damage source
     * @param amount  The amount of damage (mutable)
     * @param canceled Whether the damage event has been canceled
     * @param artifact   The artifact stack
     */
    default void onPlayerHurt(Player player, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
    }

    /**
     * Called before a spell is cast (if player carries the artifact in their hotbar or accessories) to apply the artifact's
     * effect.
     *
     * @param event    The spell cast event
     * @param artifact The artifact stack
     */
    default void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
    }

    /**
     * Called after a spell is cast (if player carries the artifact in their hotbar or accessories) to apply the artifact's
     * effect.
     *
     * @param event    The spell cast event
     * @param artifact The artifact stack
     */
    default void onSpellPostCast(SpellCastEvent.Post event, ItemStack artifact) {
    }
}