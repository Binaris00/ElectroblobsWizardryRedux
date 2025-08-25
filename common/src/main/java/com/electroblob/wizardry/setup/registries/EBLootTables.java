package com.electroblob.wizardry.setup.registries;

import com.electroblob.wizardry.WizardryMainMod;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public final class EBLootTables {
    private static final Set<ResourceLocation> LOOT_TABLES = Sets.newHashSet();
    private EBLootTables(){}

    public static final ResourceLocation DUNGEON_ADDITIONS = register("chests/dungeon_additions");
    public static final ResourceLocation SUBSET_ARCANE_TOMES = register("subsets/arcane_tomes");
    public static final ResourceLocation SUBSET_ARMOR_UPGRADES = register("subsets/armor_upgrades");
    public static final ResourceLocation SUBSET_ELEMENTAL_CRYSTALS = register("subsets/elemental_crystals");
    public static final ResourceLocation SUBSET_EPIC_ARTEFACTS = register("subsets/epic_artefacts");
    public static final ResourceLocation SUBSET_RARE_ARTEFACTS = register("subsets/rare_artefacts");
    public static final ResourceLocation SUBSET_UNCOMMON_ARTEFACTS = register("subsets/uncommon_artefacts");
    public static final ResourceLocation SUBSET_WIZARD_ARMOR = register("subsets/wizard_armor");
    public static final ResourceLocation SUBSET_WAND_UPGRADES = register("subsets/wand_upgrades");


    private static ResourceLocation register(String location){
        return register(WizardryMainMod.location(location));
    }

    private static ResourceLocation register(ResourceLocation location){
        if(LOOT_TABLES.add(location)){
           return location;
        } else {
            throw new IllegalArgumentException(location + " is already a registered built-in loot table");
        }
    }
}
