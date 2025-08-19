package com.electroblob.wizardry.setup;

import com.electroblob.wizardry.EBEventHelper;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.content.ForfeitRegistry;
import com.electroblob.wizardry.setup.registries.WandUpgrades;

public final class CommonSetup {

    public static void init() {
        SpellProperty.load();
        EBEventHelper.register();
        WandUpgrades.initUpgrades();
        ForfeitRegistry.register();

        EBLogger.info("Electroblob's Wizardry Started");
    }

    public static void setup() {
        informStart();
    }


    private static void informStart() {
    }

    private CommonSetup() {}
}
