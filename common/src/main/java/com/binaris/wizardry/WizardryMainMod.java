package com.binaris.wizardry;

import com.binaris.wizardry.api.EBLogger;
import com.binaris.wizardry.content.ForfeitRegistry;
import com.binaris.wizardry.setup.registries.EBAdvancementTriggers;
import com.binaris.wizardry.setup.registries.EBArgumentTypeRegistry;
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
