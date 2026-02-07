package com.binaris.wizardry.core;

import com.binaris.wizardry.api.content.event.EBLivingDeathEvent;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.ArtifactItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// TODO Improvement, maybe I should abstract the logic of the events here

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
    default void onTick(LivingEntity entity, Level level, ItemStack stack) {
    }

    default void onPlayerKill(EBLivingDeathEvent event, ItemStack stack) {
    }

    default void onSpellPreCast(SpellCastEvent.Pre event, ItemStack stack) {
    }

    default void onSpellPostCast(SpellCastEvent.Post event, ItemStack stack) {
    }

    default void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
    }

    default void onPlayerHurt(EBLivingHurtEvent event, ItemStack stack){
    }
}
