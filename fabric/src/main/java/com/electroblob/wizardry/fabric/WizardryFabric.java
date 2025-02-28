package com.electroblob.wizardry.fabric;

import com.electroblob.wizardry.api.LoaderEnvironment;
import com.electroblob.wizardry.fabric.registry.*;
import net.fabricmc.api.ModInitializer;

import com.electroblob.wizardry.Wizardry;


public final class WizardryFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Wizardry.init(LoaderEnvironment.FABRIC);
        BlockRegistryFabric.register();
        ItemRegistryFabric.register();
        EntityRegistryFabric.register();
        SpellRegistryFabric.register();
        CreativeTabRegistryFabric.register();
        MobEffectRegistryFabric.register();
    }
}
