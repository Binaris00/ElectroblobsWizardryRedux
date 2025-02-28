package com.electroblob.wizardry.fabric.registry;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class MobEffectRegistryFabric {

    public static void register() {
        EBRegister.registerMobEffects((collection) ->
                collection.forEach((name, effect) ->
                    Registry.register(BuiltInRegistries.MOB_EFFECT, Wizardry.location(name), effect.get())
                )
        );
    }

    private MobEffectRegistryFabric() {}
}
