package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.api.content.DeferredObject;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

// TODO just a really quick wand upgrade imp
public final class WandUpgrades {
    private static final HashMap<DeferredObject<Item>, String> UPGRADES = new HashMap<>();

    private WandUpgrades(){}

    public static void registerSpecialUpgrade(DeferredObject<Item> upgrade, String identifier){
        if(UPGRADES.containsValue(identifier))
            throw new IllegalArgumentException("Duplicate wand upgrade identifier: " + identifier);
        UPGRADES.put(upgrade, identifier);
    }

    public static void initUpgrades(){
        UPGRADES.put(EBItems.CONDENSER_UPGRADE, "condenser");
        UPGRADES.put(EBItems.STORAGE_UPGRADE, "storage");
        UPGRADES.put(EBItems.SIPHON_UPGRADE, "siphon");
        UPGRADES.put(EBItems.RANGE_UPGRADE, "range");
        UPGRADES.put(EBItems.DURATION_UPGRADE, "duration");
        UPGRADES.put(EBItems.COOLDOWN_UPGRADE, "cooldown");
        UPGRADES.put(EBItems.BLAST_UPGRADE, "blast");
        UPGRADES.put(EBItems.ATTUNEMENT_UPGRADE, "attunement");
        UPGRADES.put(EBItems.MELEE_UPGRADE, "melee");
    }

    public static String getIdentifier(Item upgrade){
        for(Map.Entry<DeferredObject<Item>, String> entry : UPGRADES.entrySet()){
            if(entry.getKey().get().equals(upgrade)) return entry.getValue();
        }
        return "";
    }

    public static HashMap<DeferredObject<Item>, String> getWandUpgrades() {
        return UPGRADES;
    }
}
