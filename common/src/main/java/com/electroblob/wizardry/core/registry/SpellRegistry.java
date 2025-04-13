package com.electroblob.wizardry.core.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Spell;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

// TODO: Probably going to change stuff, just to help the addon creation
public final class SpellRegistry {
    private static final ResourceKey<Registry<Spell>> KEY = ResourceKey.createRegistryKey(new ResourceLocation(WizardryMainMod.MOD_ID, "spells"));
    private static boolean locationsBeenInitialized;
    private static boolean isInAssignPeriod;

    private static Supplier<Set<Map.Entry<ResourceKey<Spell>, Spell>>> ENTRY_GETTER;

    public static ResourceKey<Registry<Spell>> key(){
        return KEY;
    }

    public static Set<Map.Entry<ResourceKey<Spell>, Spell>> entrySet() {
        return ENTRY_GETTER.get();
    }

    public static void initEntryGetter(Supplier<Set<Map.Entry<ResourceKey<Spell>, Spell>>> getter) {
        if (ENTRY_GETTER == null) ENTRY_GETTER = getter;
    }


    public static void initializeSpellLocations() {
        if(!locationsBeenInitialized) {
            isInAssignPeriod = true;
            entrySet().forEach(entry -> {
                var location = entry.getKey().location();
                var spell = entry.getValue();
                spell.assignLocation(location);
            });
            isInAssignPeriod = false;
            locationsBeenInitialized = true;
        }
    }

    public static @Nullable Spell get(ResourceLocation location){
        for(Map.Entry<ResourceKey<Spell>, Spell> entry : entrySet()){
            if(entry.getValue().getLocation().equals(location)) return entry.getValue();
        }

        return null;
    }


    public static boolean isInAssignPeriod() {
        return isInAssignPeriod;
    }

}