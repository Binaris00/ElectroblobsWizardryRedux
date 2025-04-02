package com.electroblob.wizardry.core;

import com.electroblob.wizardry.api.content.spell.Spell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
//
//    public final void playSoundLoop(Level world, LivingEntity entity, int ticksInUse) {
//        if (ticksInUse == 0 && world.isClientSide)
//            Wizardry.proxy.playSpellSoundLoop(entity, this, this.sounds, SoundSource.PLAYERS, volume, pitch + pitchVariation * (world.random.nextFloat() - 0.5f));
//    }
//
//    public final void playSoundLoop(Level world, double x, double y, double z, int ticksInUse, int duration) {
//        if (ticksInUse == 0 && world.isClientSide) {
//            Wizardry.proxy.playSpellSoundLoop(world, x, y, z, this, this.sounds, SoundSource.PLAYERS, volume, pitch + pitchVariation * (world.random.nextFloat() - 0.5f), duration);
//        }
//    }
}
