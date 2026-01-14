package com.binaris.wizardry.client.sound;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.util.EntityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;

/**
 * Encapsulate all the work and info needed to cast spells in the mod, this contains 3 subclasses
 * that are used depending on how you're casting a spell
 */
public abstract class SoundLoopSpell extends SoundLoop {
    final Spell spell;

    public SoundLoopSpell(SoundEvent start, SoundEvent loop, SoundEvent end, float volume, ISoundFactory factory, Spell spell) {
        super(start, loop, end, SoundSource.PLAYERS, volume, factory);
        this.spell = spell;
    }

    /**
     * Checks if a sound loop for the given entity and spell already exists
     */
    public static boolean hasActiveLoop(LivingEntity entity, Spell spell) {
        return SoundLoop.hasActiveLoopMatching(loop ->
                loop instanceof SoundLoopSpellEntity spellLoop &&
                        spellLoop.source == entity &&
                        spellLoop.spell == spell
        );
    }

    protected abstract boolean stillCasting(Spell spell);

    @Override
    public void tick() {
        if (stillCasting(spell)) {
            super.tick();
        } else {
            endLoop();
        }
    }

    /**
     * Sound loop from a spell cast by an entity
     **/
    public static class SoundLoopSpellEntity extends SoundLoopSpell {
        private final LivingEntity source;

        public SoundLoopSpellEntity(SoundEvent start, SoundEvent loop, SoundEvent end, Spell spell, LivingEntity source, float volume, float pitch) {
            super(start, loop, end, volume, (sound, category, v, repeat) -> new MovingSoundEntity<>(source, sound, category, v, pitch, repeat), spell);
            this.source = source;
        }

        @Override
        protected boolean stillCasting(Spell spell) {
            return EntityUtil.isCasting(source, spell);
        }
    }

    public static abstract class SoundLoopSpellLocation extends SoundLoopSpell {
        public SoundLoopSpellLocation(SoundEvent start, SoundEvent loop, SoundEvent end, Spell spell, double x, double y, double z, float sndVolume, float sndPitch) {
            super(start, loop, end, sndVolume, (sound, category, v, r) ->
                    new AbstractSoundInstance(sound, category, Minecraft.getInstance().level.random) {
                        {
                            this.x = (float) x;
                            this.y = (float) y;
                            this.z = (float) z;
                            this.looping = r;
                            this.volume = v;
                            this.pitch = sndPitch;
                        }
                    }, spell);
        }
    }

    /**
     * Sound loop from a spell cast by a dispenser
     **/
    public static class SoundLoopSpellDispenser extends SoundLoopSpellLocation {
        private final DispenserBlockEntity source;

        public SoundLoopSpellDispenser(SoundEvent start, SoundEvent loop, SoundEvent end, Spell spell, Level world, double x, double y, double z, float sndVolume, float sndPitch) {
            super(start, loop, end, spell, x, y, z, sndVolume, sndPitch);

            BlockEntity tileentity = world.getBlockEntity(BlockPos.containing(x, y, z));

            if (tileentity instanceof DispenserBlockEntity) this.source = (DispenserBlockEntity) tileentity;
            else
                throw new NullPointerException(String.format("Playing continuous spell sound: no dispenser found at %s, %s, %s", x, y, z));
        }

        @Override
        protected boolean stillCasting(Spell spell) {
            return false;
            // TODO DISPENSER CASTING DATA
            //return DispenserCastingData.get(source).currentlyCasting() == spell;
        }
    }

    /**
     * Sound loop from a spell cast just by the location, normally a /cast command
     **/
    public static class SoundLoopSpellPosTimed extends SoundLoopSpellLocation {
        private int timeLeft;

        public SoundLoopSpellPosTimed(SoundEvent start, SoundEvent loop, SoundEvent end, Spell spell, int duration, double x, double y, double z, float sndVolume, float sndPitch) {
            super(start, loop, end, spell, x, y, z, sndVolume, sndPitch);
            this.timeLeft = duration;
        }

        @Override
        public void tick() {
            super.tick();
            timeLeft--;
        }

        @Override
        protected boolean stillCasting(Spell spell) {
            return timeLeft > 0;
        }
    }
}
