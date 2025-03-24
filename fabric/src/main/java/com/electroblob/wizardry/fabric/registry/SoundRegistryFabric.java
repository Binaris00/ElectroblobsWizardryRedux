package com.electroblob.wizardry.fabric.registry;

import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class SoundRegistryFabric {
    private SoundRegistryFabric(){}

    public static void register() {
        EBRegister.registerSounds(collection ->
                collection.forEach((group) ->
                        Registry.register(BuiltInRegistries.SOUND_EVENT, group.getKey(), group.getValue().get()))
        );
    }
}
