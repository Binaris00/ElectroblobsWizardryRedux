package com.electroblob.wizardry;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.LoaderEnvironment;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.setup.ClientSetup;
import com.electroblob.wizardry.setup.CommonSetup;
import net.minecraft.resources.ResourceLocation;

public final class WizardryMainMod {
    public static final String MOD_ID = "ebwizardry";
    public static final String MOD_NAME = "Electroblob's Wizardry";
    public static final String MOD_VERSION = null;
    private static LoaderEnvironment loader;

    public static void init(LoaderEnvironment environment) {
        loader = environment;
        //noinspection ResultOfMethodCallIgnored
        EBLogger.load();
        CommonSetup.init();
        if(isClientSide()) ClientSetup.init();
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static boolean isClientSide() {
        return false;
    }

    public static boolean isFabric() {
        return loader == LoaderEnvironment.FABRIC;
    }

    public static boolean isForge(){
        return loader == LoaderEnvironment.FORGE;
    }
}
