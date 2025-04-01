package com.electroblob.wizardry;

import com.electroblob.wizardry.api.LoaderEnvironment;
import com.electroblob.wizardry.registry.*;
import net.fabricmc.api.ModInitializer;


public final class WizardryFabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        WizardryMainMod.init(LoaderEnvironment.FABRIC);
        BlockRegistryFabric.register();
        ItemRegistryFabric.register();
        EntityRegistryFabric.register();
        TierRegistryFabric.register();
        ElementRegistryFabric.register();
        SpellRegistryFabric.register();
        CreativeTabRegistryFabric.register();
        MobEffectRegistryFabric.register();

        SoundRegistryFabric.register();
    }
}
