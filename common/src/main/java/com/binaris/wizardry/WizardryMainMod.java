package com.binaris.wizardry;

import com.binaris.wizardry.api.EBLogger;
import com.binaris.wizardry.content.ForfeitRegistry;
import com.binaris.wizardry.core.config.EBConfig;
import com.binaris.wizardry.core.config.EBConfigManager;
import com.binaris.wizardry.core.config.IConfigProvider;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBAdvancementTriggers;
import com.binaris.wizardry.setup.registries.EBArgumentTypeRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public final class WizardryMainMod {
    public static final String MOD_ID = "ebwizardry";
    public static final String MOD_NAME = "Electroblob's Wizardry";
    public static boolean IS_THE_SEASON = false;

    public static EBConfigManager CONFIG;
    private static final ArrayList<EBConfigManager> CONFIG_MANAGERS = new ArrayList<>();

    public static void init() {
        CONFIG = registerConfigManager(MOD_ID, new EBConfig(), true);
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

    public static EBConfigManager registerConfigManager(String modid, IConfigProvider provider, boolean shouldSync) {
        EBConfigManager manager = new EBConfigManager(
                modid,
                Services.PLATFORM.getConfigDirectory().resolve(modid + ".json"),
                provider,
                shouldSync
        );
        manager.load();
        CONFIG_MANAGERS.add(manager);
        return manager;
    }

    public static void reloadConfigs() {
        CONFIG_MANAGERS.forEach(EBConfigManager::reload);
    }

    public static ArrayList<EBConfigManager> getConfigManagers() {
        return CONFIG_MANAGERS;
    }
}
