package com.electroblob.wizardry.setup.registries;

import net.minecraft.world.item.Item;

import java.util.HashMap;

// TODO just a really quick wand upgrade imp
public final class WandUpgrades {
    private static final HashMap<Item, String> UPGRADES = new HashMap<>();

    private WandUpgrades(){}

    public static void registerSpecialUpgrade(Item upgrade, String identifier){
        if(UPGRADES.containsValue(identifier))
            throw new IllegalArgumentException("Duplicate wand upgrade identifier: " + identifier);
        UPGRADES.put(upgrade, identifier);
    }

    public static void initUpgrades(){
        UPGRADES.put(EBItems.CONDENSER_UPGRADE.get(), "condenser");
        UPGRADES.put(EBItems.STORAGE_UPGRADE.get(), "storage");
        UPGRADES.put(EBItems.SIPHON_UPGRADE.get(), "siphon");
        UPGRADES.put(EBItems.RANGE_UPGRADE.get(), "range");
        UPGRADES.put(EBItems.DURATION_UPGRADE.get(), "duration");
        UPGRADES.put(EBItems.COOLDOWN_UPGRADE.get(), "cooldown");
        UPGRADES.put(EBItems.BLAST_UPGRADE.get(), "blast");
        UPGRADES.put(EBItems.ATTUNEMENT_UPGRADE.get(), "attunement");
        UPGRADES.put(EBItems.MELEE_UPGRADE.get(), "melee");
    }

    public static String getIdentifier(Item upgrade){
        return UPGRADES.get(upgrade);
    }

    public static HashMap<Item, String> getWandUpgrades() {
        return UPGRADES;
    }
}
