package com.electroblob.wizardry.fabric;

import com.electroblob.wizardry.api.LoaderEnvironment;
import com.electroblob.wizardry.fabric.registry.*;
import net.fabricmc.api.ModInitializer;

import com.electroblob.wizardry.WizardryMainMod;


public final class WizardryFabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        WizardryMainMod.init(LoaderEnvironment.FABRIC);
        BlockRegistryFabric.register();
        ItemRegistryFabric.register();
        EntityRegistryFabric.register();
        SpellRegistryFabric.register();
        CreativeTabRegistryFabric.register();
        MobEffectRegistryFabric.register();
    }
}
