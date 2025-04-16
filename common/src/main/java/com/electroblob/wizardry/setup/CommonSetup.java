package com.electroblob.wizardry.setup;

import com.electroblob.wizardry.EBEventHelper;
import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.core.registry.SpellRegistry;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.WandUpgrades;
import net.minecraft.network.chat.Component;

public final class CommonSetup {

    public static void init() {
        SpellProperty.load();
        EBEventHelper.register();
        //EBNetwork.bootstrap();
        WandUpgrades.initUpgrades();
    }

    public static void setup() {
        informStart();
        SpellRegistry.initializeSpellLocations();
        EBDamageSources.init();
    }


    private static void informStart() {
        EBLogger.info("Electroblob's Wizardry Started");
    }

    private CommonSetup() {}
}
