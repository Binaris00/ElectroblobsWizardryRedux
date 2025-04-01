package com.electroblob.wizardry.common.core;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.common.spell.Element;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class ElementRegistry {
    private static final ResourceKey<Registry<Element>> KEY = ResourceKey.createRegistryKey(new ResourceLocation(WizardryMainMod.MOD_ID, "elements"));
    private static boolean locationsBeenInitialized;
    private static boolean isInAssignPeriod;

    private static Supplier<Set<Map.Entry<ResourceKey<Element>, Element>>> ENTRY_GETTER;

    public static ResourceKey<Registry<Element>> key(){
        return KEY;
    }

    public static Set<Map.Entry<ResourceKey<Element>, Element>> entrySet() {
        return ENTRY_GETTER.get();
    }

    public static void initEntryGetter(Supplier<Set<Map.Entry<ResourceKey<Element>, Element>>> getter) {
        if (ENTRY_GETTER == null) ENTRY_GETTER = getter;
    }


    public static void initializeSpellLocations() {
        if(!locationsBeenInitialized) {
            isInAssignPeriod = true;
//            entrySet().forEach(entry -> {
//                var location = entry.getKey().location();
//                var element = entry.getValue();
//                element.assignLocation(location);
//            });
            isInAssignPeriod = false;
            locationsBeenInitialized = true;
        }
    }


    public static boolean isInAssignPeriod() {
        return isInAssignPeriod;
    }

}