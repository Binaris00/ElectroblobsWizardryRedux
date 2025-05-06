package com.electroblob.wizardry;

import com.electroblob.wizardry.api.LoaderEnvironment;
import com.electroblob.wizardry.setup.ClientSetup;
import com.electroblob.wizardry.setup.CommonSetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Random;

public final class WizardryMainMod {
    public static final String MOD_ID = "ebwizardry";
    public static final String MOD_NAME = "Electroblob's Wizardry";
    public static final String MOD_VERSION = null;
    // TODO
    public static boolean tisTheSeason = false;
    private static LoaderEnvironment loader;
    private static Random random;

    public static void init(LoaderEnvironment environment) {
        loader = environment;
        CommonSetup.init();
        if(isClientSide()) ClientSetup.init();
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    /** Returns a random based on the entity's UUID and tick count.
     * Only used in forfeit and this is not really the best to do this, but I'm just avoiding to
     * sync a random-number between the server and the client.
     * */
    public static Random getRandom(Entity entity){
        if(random == null) random = new Random((entity.getUUID().getMostSignificantBits() ^ entity.getUUID().getLeastSignificantBits()
                ^ entity.tickCount));
        return random;
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
