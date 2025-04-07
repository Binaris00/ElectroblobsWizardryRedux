package com.electroblob.wizardry.client.sound;

import com.electroblob.wizardry.core.event.EBClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import java.util.HashSet;
import java.util.Set;

/**
 * Handle all the active sound loops with the {@link SoundLoop#onClientTick(EBClientTickEvent)}
 * Saves all the info for the three basic sounds (start, loop, end) and handle all the logic in the event
 * <br><br>
 * This class only works as a blueprint for situations that you need to play a sound loop, for example:
 * {@link com.electroblob.wizardry.core.SpellSoundManager SpellSoundManager}, all the logic here is just for simplify
 * the process of doing a sound loop
 * */
public abstract class SoundLoop extends AbstractTickableSoundInstance {
    private static final Set<SoundLoop> activeLoops = new HashSet<>();
    private final SoundInstance startPrimer;

    private final SoundInstance start;
    private final SoundInstance loop;
    private final SoundInstance end;

    private boolean looping = false;
    private boolean needsRemoving = false;

    public SoundLoop(SoundEvent start, SoundEvent loop, SoundEvent end, SoundSource category, float volume, ISoundFactory factory) {
        super(end, category, Minecraft.getInstance().level.random);
        this.startPrimer = factory.create(start, category, 0.00001f, false);
        this.start = factory.create(start, category, volume, false);
        this.loop = factory.create(loop, category, volume, true);
        this.end = factory.create(end, category, volume, false);
    }

    /**
     * Used to know when the sound is ready to be looped, all the
     * rest of the tick logic is handled in the client tick {@link SoundLoop#onClientTick(EBClientTickEvent)}
     * */
    @Override
    public void tick() {
        if (!looping && !Minecraft.getInstance().getSoundManager().isActive(startPrimer)) {
            Minecraft.getInstance().getSoundManager().play(loop);
            looping = true;
        }
    }

    public static void addLoop(SoundLoop loop) {
        activeLoops.add(loop);
        Minecraft.getInstance().getSoundManager().play(loop.startPrimer);
        Minecraft.getInstance().getSoundManager().playDelayed(loop.start, 2);
    }

    public void endLoop() {
        Minecraft.getInstance().getSoundManager().play(end);
        this.needsRemoving = true;
    }

    protected void stopStartAndLoop() {
        Minecraft.getInstance().getSoundManager().stop(startPrimer);
        Minecraft.getInstance().getSoundManager().stop(start);
        Minecraft.getInstance().getSoundManager().stop(loop);
    }

    public static void onClientTick(EBClientTickEvent event){
        activeLoops.stream().filter(s -> s.needsRemoving).forEach(SoundLoop::stopStartAndLoop);
        activeLoops.removeIf(s -> s.needsRemoving);
        activeLoops.forEach(SoundLoop::tick);
    }

    @FunctionalInterface
    public interface ISoundFactory {
        SoundInstance create(SoundEvent sound, SoundSource category, float volume, boolean repeat);
    }
}
