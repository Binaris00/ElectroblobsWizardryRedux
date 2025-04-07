package com.electroblob.wizardry.core;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.client.sound.MovingSoundEntity;
import com.electroblob.wizardry.client.sound.MovingSoundSpellCharge;
import com.electroblob.wizardry.client.sound.SoundLoop;
import com.electroblob.wizardry.client.sound.SoundLoopSpell;
import com.electroblob.wizardry.setup.registries.EBSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

// TODO CHECK SOUND?
// This is probably a temp class until I make something better
public final class SpellSoundManager {
    public static void registerSpellSounds(){
//        SpellRegistry.entrySet().forEach((map) ->{
//            SoundEvent.createVariableRangeEvent(new ResourceLocation(map.getKey().location().getNamespace(), "spell." + map.getKey().location().getPath()));
//
//        });
    }

    public static void playSound(Level world, Spell spell, double x, double y, double z, int ticksInUse, int duration) {
        SoundEvent sound = SoundEvent.createVariableRangeEvent(new ResourceLocation(spell.getLocation().getNamespace(), "spell." + spell.getLocation().getPath()));
        world.playSound(null, x, y, z, sound, SoundSource.PLAYERS, spell.getVolume(), spell.getPitch() + spell.getPitchVariation() * (world.random.nextFloat() - 0.5f));
    }

    public static void playSpellSoundLoop(LivingEntity entity, Spell spell, SoundEvent[] sounds, float volume, float pitch) {
        if (sounds.length < 3) throw new IllegalArgumentException("Tried to play a continuous spell sound using an array " + "of sound events, but the given array contained less than 3 sound events!");
        playSpellSoundLoop(entity, spell, sounds[0], sounds[1], sounds[2], volume, pitch);
    }

    public static void playSpellSoundLoop(LivingEntity entity, Spell spell, SoundEvent start, SoundEvent loop, SoundEvent end, float volume, float pitch) {
        SoundLoop.addLoop(new SoundLoopSpell.SoundLoopSpellEntity(start, loop, end, spell, entity, volume, pitch));
    }

    public static void playSpellSoundLoop(Level world, double x, double y, double z, Spell spell, SoundEvent[] sounds, float volume, float pitch, int duration) {
        if (sounds.length < 3) throw new IllegalArgumentException("Tried to play a continuous spell sound using an array " + "of sound events, but the given array contained less than 3 sound events!");
        playSpellSoundLoop(world, x, y, z, spell, sounds[0], sounds[1], sounds[2], volume, pitch, duration);
    }

    public static void playSpellSoundLoop(Level world, double x, double y, double z, Spell spell, SoundEvent start, SoundEvent loop, SoundEvent end, float volume, float pitch, int duration) {
        if (duration == -1) {
            SoundLoop.addLoop(new SoundLoopSpell.SoundLoopSpellDispenser(start, loop, end, spell, world, x, y, z, volume, pitch));
        } else {
            SoundLoop.addLoop(new SoundLoopSpell.SoundLoopSpellPosTimed(start, loop, end, spell, duration, x, y, z, volume, pitch));
        }
    }

    public static void playMovingSound(Entity entity, SoundEvent sound, SoundSource category, float volume, float pitch, boolean repeat) {
        Minecraft.getInstance().getSoundManager().play(new MovingSoundEntity<>(entity, sound, category, volume, pitch, repeat));
    }

    public static void playChargeSound(LivingEntity entity) {
        Minecraft.getInstance().getSoundManager().play(new MovingSoundSpellCharge(entity, EBSounds.ITEM_WAND_CHARGEUP.get(), SoundSource.PLAYERS, 2.5f, 1.4f, false));
    }
}
