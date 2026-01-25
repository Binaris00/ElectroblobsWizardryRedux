package com.binaris.wizardry;

import com.binaris.wizardry.client.NotImplementedItems;
import com.binaris.wizardry.client.WizardryForgeClient;
import com.binaris.wizardry.content.menu.BookshelfMenu;
import com.binaris.wizardry.network.EBForgeNetwork;
import com.binaris.wizardry.registry.EBArgumentTypesForge;
import com.binaris.wizardry.registry.EBRegistriesForge;
import com.binaris.wizardry.setup.registries.WandUpgrades;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(WizardryMainMod.MOD_ID)
public final class WizardryForgeMod {
    public WizardryForgeMod() {
        WizardryMainMod.init();

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        EBRegistriesForge.tiers(modBus);
        EBRegistriesForge.elements(modBus);
        EBRegistriesForge.spells(modBus);
        EBArgumentTypesForge.register(modBus);

        modBus.addListener(WizardryForgeMod::commonSetup);
        if (FMLEnvironment.dist.isClient()) {
            modBus.addListener(WizardryForgeClient::clientSetup);
        }
    }

    public static void commonSetup(final FMLCommonSetupEvent event) {
        EBForgeNetwork.registerMessages();
        BookshelfMenu.initBookItems();
        WandUpgrades.initUpgrades();
        NotImplementedItems.init();
    }
}
