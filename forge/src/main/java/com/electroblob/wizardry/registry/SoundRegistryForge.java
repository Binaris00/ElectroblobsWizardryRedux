package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class SoundRegistryForge {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WizardryMainMod.MOD_ID);

    public static void register() {
        EBRegister.registerSounds((soundCollection) -> soundCollection.forEach((group) ->
                SOUND_EVENTS.register(group.getKey(), group.getValue()))
        );
    }

    private SoundRegistryForge() {}
}
