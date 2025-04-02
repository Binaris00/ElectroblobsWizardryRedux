package com.electroblob.wizardry.core.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Tier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class TierRegistry {
    private static final ResourceKey<Registry<Tier>> KEY = ResourceKey.createRegistryKey(new ResourceLocation(WizardryMainMod.MOD_ID, "tiers"));
    private static boolean locationsBeenInitialized;
    private static boolean isInAssignPeriod;

    private static Supplier<Set<Map.Entry<ResourceKey<Tier>, Tier>>> ENTRY_GETTER;

    public static ResourceKey<Registry<Tier>> key(){
        return KEY;
    }

    public static Set<Map.Entry<ResourceKey<Tier>, Tier>> entrySet() {
        return ENTRY_GETTER.get();
    }

    public static void initEntryGetter(Supplier<Set<Map.Entry<ResourceKey<Tier>, Tier>>> getter) {
        if (ENTRY_GETTER == null) ENTRY_GETTER = getter;
    }


    public static void initializeSpellLocations() {
        if(!locationsBeenInitialized) {
            isInAssignPeriod = false;
            locationsBeenInitialized = true;
        }
    }


    public static boolean isInAssignPeriod() {
        return isInAssignPeriod;
    }

}