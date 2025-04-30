package com.electroblob.wizardry;

import com.electroblob.wizardry.api.LoaderEnvironment;
import com.electroblob.wizardry.network.EBFabricNetwork;
import com.electroblob.wizardry.setup.registries.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;


public final class WizardryFabricMod implements ModInitializer {
    @Override
    public void onInitialize() {

        WizardryMainMod.init(LoaderEnvironment.FABRIC);

        if(!WizardryMainMod.isClientSide()){
            WizardryFabricEvents.onServer();
        }

        EBBlocks.register(Registry::register);
        EBBlockEntities.register(Registry::register);
        EBItems.register(Registry::register);
        EBEntities.register(Registry::register);

        Tiers.register(EBRegistriesFabric.TIERS, Registry::register);
        Elements.register(EBRegistriesFabric.ELEMENTS, Registry::register);
        Spells.register(EBRegistriesFabric.SPELLS, Registry::register);

        EBCreativeTabs.register(Registry::register);
        EBMobEffects.register(Registry::register);
        EBSounds.register(Registry::register);
        EBEnchantments.register(Registry::register);
        EBMenus.register(Registry::register);

        EBFabricNetwork.registerC2SMessages();
    }
}
