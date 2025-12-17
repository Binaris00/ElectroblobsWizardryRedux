package com.electroblob.wizardry.core;

import com.electroblob.wizardry.api.content.event.EBLivingDeathEvent;
import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.item.ArtefactItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

// TODO Improvement, maybe I should abstract the logic of the events here

/**
 * Interface representing the effects of artefacts in the mod.
 * This provides methods that can be overridden to define custom behaviour for artefacts based on events. These methods
 * are loaded by the {@link ArtefactItem ArtefactItem} class to register the effects
 * inside the event bus.
 * <br><br>
 * You have all the freedom to add a custom implementation of this interface for your own artefacts, but if you do so,
 * you must ensure to load the events yourself.
 */
public interface IArtefactEffect {
    default void onTick(LivingEntity entity, Level level, ItemStack stack) {
    }

    default void onDeath(EBLivingDeathEvent event, ItemStack stack) {
    }

    default void onSpellPreCast(SpellCastEvent.Pre event, ItemStack stack) {
    }

    default void onSpellPostCast(SpellCastEvent.Post event, ItemStack stack) {
    }

    default void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
    }
}
