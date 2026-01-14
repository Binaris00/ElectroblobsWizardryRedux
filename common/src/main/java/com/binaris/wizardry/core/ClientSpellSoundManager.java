package com.binaris.wizardry.core;

import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.client.sound.MovingSoundEntity;
import com.binaris.wizardry.client.sound.MovingSoundSpellCharge;
import com.binaris.wizardry.client.sound.SoundLoop;
import com.binaris.wizardry.client.sound.SoundLoopSpell;
import com.binaris.wizardry.setup.registries.EBSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class ClientSpellSoundManager {
    public static void playSpellSoundLoop(LivingEntity entity, Spell spell, SoundEvent[] sounds, float volume, float pitch) {
        if (sounds.length < 3)
            throw new IllegalArgumentException("Tried to play a continuous spell sound using an array of sound events, but the given array contained less than 3 sound events!");
        playSpellSoundLoop(entity, spell, sounds[0], sounds[1], sounds[2], volume, pitch);
    }

    public static void playSpellSoundLoop(LivingEntity entity, Spell spell, SoundEvent start, SoundEvent loop, SoundEvent end, float volume, float pitch) {
        if (!SoundLoopSpell.hasActiveLoop(entity, spell)) {
            SoundLoop.addLoop(new SoundLoopSpell.SoundLoopSpellEntity(start, loop, end, spell, entity, volume, pitch));
        }
    }

    public static void playSpellSoundLoop(Level world, double x, double y, double z, Spell spell, SoundEvent[] sounds, float volume, float pitch, int duration) {
        if (sounds.length < 3)
            throw new IllegalArgumentException("Tried to play a continuous spell sound using an array of sound events, but the given array contained less than 3 sound events!");
        playSpellSoundLoop(world, x, y, z, spell, sounds[0], sounds[1], sounds[2], volume, pitch, duration);
    }

    public static void playSpellSoundLoop(Level world, double x, double y, double z, Spell spell, SoundEvent start, SoundEvent loop, SoundEvent end, float volume, float pitch, int duration) {
        if (duration == -1)
            SoundLoop.addLoop(new SoundLoopSpell.SoundLoopSpellDispenser(start, loop, end, spell, world, x, y, z, volume, pitch));
        else
            SoundLoop.addLoop(new SoundLoopSpell.SoundLoopSpellPosTimed(start, loop, end, spell, duration, x, y, z, volume, pitch));
    }

    /**
     * Used when you want to create a sound that's moving along with an entity (projectiles, or fast-entities need this).
     *
     * @param entity   entity that's going to be linked with the sound.
     * @param sound    sound event
     * @param category sound category
     * @param volume   volume of the sound
     * @param repeat   if the sound is going to be looping with the entity
     */
    public static void playMovingSound(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, boolean repeat) {
        Minecraft.getInstance().getSoundManager().play(new MovingSoundEntity<>(entity, sound, category, volume, pitch, repeat));
    }

    /**
     * Plays the spell charge sound, used inside a {@link ISpellCastingItem SpellCastingItem}
     * when the item is on charge time.
     *
     * @param entity entity that's going to listen the sound (normally a player)
     */
    public static void playChargeSound(LivingEntity entity) {
        Minecraft.getInstance().getSoundManager().play(new MovingSoundSpellCharge(entity, EBSounds.ITEM_WAND_CHARGEUP.get(), SoundSource.PLAYERS, 2.5f, 1.4f, false));
    }
}
