package com.binaris.wizardry.core;

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

/// Client-side sound manager that plays spell loop sounds, moving entity sounds, and
/// spell charge-up sounds.
///
/// All methods must only be called on the physical client. Spell loop sounds
/// use the {@code SoundLoop} / {@code SoundLoopSpell} system to play a three-phase sequence
/// (start, loop, end) tied to either an entity or a world position. Moving sounds attach
/// a {@code MovingSoundEntity} to an entity so the audio follows it as it moves. The charge
/// sound uses {@code MovingSoundSpellCharge} and is triggered during wand charge-up.
public final class ClientSpellSoundManager {

    /// Plays a three-phase spell sound loop (start, loop, end) attached to a living
    /// entity, provided no other loop for the same spell is already active on that entity.
    ///
    /// Convenience overload that accepts a {@code SoundEvent[]} of at least 3 entries.
    /// Delegates to the four-sound overload after extracting the three individual events.
    ///
    /// @param entity the entity the sound follows.
    /// @param spell the spell associated with this sound loop.
    /// @param sounds array of at least 3 sound events: start, loop, end.
    /// @param volume sound volume.
    /// @param pitch sound pitch.
    public static void playSpellSoundLoop(LivingEntity entity, Spell spell, SoundEvent[] sounds, float volume, float pitch) {
        if (sounds.length < 3)
            throw new IllegalArgumentException("Tried to play a continuous spell sound using an array of sound events, but the given array contained less than 3 sound events!");
        playSpellSoundLoop(entity, spell, sounds[0], sounds[1], sounds[2], volume, pitch);
    }

    /// Plays a three-phase spell sound loop (start, loop, end) attached to a living
    /// entity, deduplicating by spell and entity.
    ///
    /// Uses {@code SoundLoopSpell.hasActiveLoop} to check for an existing loop for the
    /// same entity+spell pair before creating a new {@code SoundLoopSpellEntity}.
    ///
    /// @param entity the entity the sound follows.
    /// @param spell the spell associated with this sound loop.
    /// @param start the sound to play once at the beginning.
    /// @param loop the sound that loops while the spell is active.
    /// @param end the sound to play once at the end.
    /// @param volume sound volume.
    /// @param pitch sound pitch.
    public static void playSpellSoundLoop(LivingEntity entity, Spell spell, SoundEvent start, SoundEvent loop, SoundEvent end, float volume, float pitch) {
        if (!SoundLoopSpell.hasActiveLoop(entity, spell)) {
            SoundLoop.addLoop(new SoundLoopSpell.SoundLoopSpellEntity(start, loop, end, spell, entity, volume, pitch));
        }
    }

    /// Plays a three-phase spell sound loop at a fixed world position.
    ///
    /// Convenience overload that accepts a {@code SoundEvent[]} of at least 3 entries.
    /// Delegates to the position-based overload after extracting the three individual events.
    ///
    /// @param world the level to play in.
    /// @param x world X coordinate.
    /// @param y world Y coordinate.
    /// @param z world Z coordinate.
    /// @param spell the spell associated with this sound loop.
    /// @param sounds array of at least 3 sound events: start, loop, end.
    /// @param volume sound volume.
    /// @param pitch sound pitch.
    /// @param duration the loop duration in ticks, or -1 for indefinite.
    public static void playSpellSoundLoop(Level world, double x, double y, double z, Spell spell, SoundEvent[] sounds, float volume, float pitch, int duration) {
        if (sounds.length < 3)
            throw new IllegalArgumentException("Tried to play a continuous spell sound using an array of sound events, but the given array contained less than 3 sound events!");
        playSpellSoundLoop(world, x, y, z, spell, sounds[0], sounds[1], sounds[2], volume, pitch, duration);
    }

    /// Plays a three-phase spell sound loop at a fixed world position, either timed
    /// or indefinite.
    ///
    /// If {@code duration} is -1, creates a {@code SoundLoopSpellDispenser} (used for
    /// dispenser-fired spells). Otherwise, creates a {@code SoundLoopSpellPosTimed} that
    /// stops after the given number of ticks.
    ///
    /// @param world the level to play in.
    /// @param x world X coordinate.
    /// @param y world Y coordinate.
    /// @param z world Z coordinate.
    /// @param spell the spell associated with this sound loop.
    /// @param start the sound to play once at the beginning.
    /// @param loop the sound that loops while active.
    /// @param end the sound to play once at the end.
    /// @param volume sound volume.
    /// @param pitch sound pitch.
    /// @param duration ticks to loop, or -1 for indefinite.
    public static void playSpellSoundLoop(Level world, double x, double y, double z, Spell spell, SoundEvent start, SoundEvent loop, SoundEvent end, float volume, float pitch, int duration) {
        if (duration == -1)
            SoundLoop.addLoop(new SoundLoopSpell.SoundLoopSpellDispenser(start, loop, end, spell, world, x, y, z, volume, pitch));
        else
            SoundLoop.addLoop(new SoundLoopSpell.SoundLoopSpellPosTimed(start, loop, end, spell, duration, x, y, z, volume, pitch));
    }

    /// Plays a sound that moves with an entity (used for projectiles, constructs, and
    /// fast-moving entities that need positional audio).
    ///
    /// Creates and plays a {@code MovingSoundEntity} through the Minecraft client sound
    /// manager, which tracks the entity's position each tick for spatial audio.
    ///
    /// @param entity the entity the sound follows.
    /// @param sound the sound event to play.
    /// @param category the sound category (ambient, players, hostile, etc.).
    /// @param volume sound volume.
    /// @param pitch sound pitch.
    /// @param repeat if true the sound loops until the entity is removed.
    public static void playMovingSound(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, boolean repeat) {
        Minecraft.getInstance().getSoundManager().play(new MovingSoundEntity<>(entity, sound, category, volume, pitch, repeat));
    }

    /// Plays the wand charge-up sound attached to an entity, used when a player is
    /// charging a spell via a {@code WandItem} or other {@code ICastItem}.
    ///
    /// Creates a {@code MovingSoundSpellCharge} positioned at the entity and played through
    /// the Minecraft client sound manager.
    ///
    /// @param entity the entity charging the spell (normally a player).
    public static void playChargeSound(LivingEntity entity) {
        Minecraft.getInstance().getSoundManager().play(new MovingSoundSpellCharge(entity, EBSounds.ITEM_WAND_CHARGEUP.get(), SoundSource.PLAYERS, 2.5f, 1.4f, false));
    }
}
