package com.electroblob.wizardry;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.content.ForfeitRegistry;
import com.electroblob.wizardry.setup.registries.EBArgumentTypeRegistry;
import com.electroblob.wizardry.setup.registries.EBAdvancementTriggers;
import com.electroblob.wizardry.setup.registries.WandUpgrades;
import net.minecraft.resources.ResourceLocation;

public final class WizardryMainMod {
    public static final String MOD_ID = "ebwizardry";
    public static final String MOD_NAME = "Electroblob's Wizardry";
    /**
     * TODO Bool used to save the logic when it's christmas!!
     */
    public static boolean IS_THE_SEASON = false;

    public static void init() {
        EBEventHelper.register();
        WandUpgrades.initUpgrades();
        ForfeitRegistry.register();
        EBAdvancementTriggers.register();
        EBArgumentTypeRegistry.init();

        EBLogger.info("Electroblob's Wizardry Started");
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static ResourceLocation location(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }
}
