package com.electroblob.wizardry;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.content.ForfeitRegistry;
import com.electroblob.wizardry.setup.registries.EBAdvancementTriggers;
import com.electroblob.wizardry.setup.registries.EBEntities;
import com.electroblob.wizardry.setup.registries.WandUpgrades;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Random;

public final class WizardryMainMod {
    public static final String MOD_ID = "ebwizardry";
    public static final String MOD_NAME = "Electroblob's Wizardry";
    /** TODO Bool used to save the logic when it's christmas!! */
    public static boolean IS_THE_SEASON = false;
    private static Random random;

    public static void init() {
        SpellProperty.load();
        EBEventHelper.register();
        WandUpgrades.initUpgrades();
        ForfeitRegistry.register();
        EBAdvancementTriggers.register();

        EBLogger.info("Electroblob's Wizardry Started");
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    /** Returns a random based on the entity's UUID and tick count.
     * Only used in forfeit.
     * <br><br>
     * Deprecated: This is not really the best to do this, but I'm just avoiding to
     * sync a random-number between the server and the client.
     * */
    @Deprecated
    public static Random getRandom(Entity entity){
        if(random == null) random = new Random((entity.getUUID().getMostSignificantBits() ^ entity.getUUID().getLeastSignificantBits()
                ^ entity.tickCount));
        return random;
    }
}
