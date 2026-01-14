package com.binaris.wizardry.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class MovingSoundEntity<T extends Entity> extends AbstractTickableSoundInstance {
    protected final T source;

    public MovingSoundEntity(T entity, SoundEvent sound, SoundSource category, float volume, float pitch, boolean repeat) {
        super(sound, category, entity.level().random);
        this.source = entity;
        this.looping = repeat;
        this.volume = volume;
        this.pitch = pitch;
        this.delay = 0;
    }

    @Override
    public void tick() {
        if (!this.source.isAlive()) {
            this.stop();
        } else {
            this.x = (float) this.source.getX();
            this.y = (float) this.source.getY();
            this.z = (float) this.source.getZ();
        }
    }
}
