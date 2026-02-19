package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.OLDEBCONFIG;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public final class EBLootTables {
    private static final Set<ResourceLocation> LOOT_TABLES = Sets.newHashSet();
    public static final ResourceLocation DUNGEON_ADDITIONS = register("chests/dungeon_additions");
    public static final ResourceLocation DISPENSER_ADDITIONS = register("chests/jungle_dispenser_additions");
    public static final ResourceLocation JUNK_FISHING_ADDITIONS = register("gameplay/fishing/junk_additions");
    public static final ResourceLocation TREASURE_FISHING_ADDITIONS = register("gameplay/fishing/treasure_additions");
    public static final ResourceLocation SUBSET_ARCANE_TOMES = register("subsets/arcane_tomes");
    public static final ResourceLocation SUBSET_ARMOR_UPGRADES = register("subsets/armor_upgrades");
    public static final ResourceLocation SUBSET_ELEMENTAL_CRYSTALS = register("subsets/elemental_crystals");
    public static final ResourceLocation SUBSET_EPIC_ARTIFACTS = register("subsets/epic_artifacts");
    public static final ResourceLocation SUBSET_RARE_ARTIFACTS = register("subsets/rare_artifacts");
    public static final ResourceLocation SUBSET_UNCOMMON_ARTIFACTS = register("subsets/uncommon_artifacts");
    public static final ResourceLocation SUBSET_WIZARD_ARMOR = register("subsets/wizard_armor");
    public static final ResourceLocation SUBSET_WAND_UPGRADES = register("subsets/wand_upgrades");
    public static final ResourceLocation SHRINE = register("chests/shrine");

    private static final List<Pair<ResourceLocation, LootPool>> LOOT_INJECTIONS = new ArrayList<>();

    private EBLootTables() {
    }

    private static ResourceLocation register(String location) {
        return register(WizardryMainMod.location(location));
    }

    private static ResourceLocation register(ResourceLocation location) {
        if (LOOT_TABLES.add(location)) {
            return location;
        }
        throw new IllegalArgumentException(location + " is already a registered built-in loot table");
    }

    // =================
    // LOOT INJECTION - Used to add custom loot to existing loot tables
    // =================

    /**
     * You should add your injections here, not only add them to the list or just creating the members.
     */
    public static void initInjections() {
        // Check all the locations from config and add the loot pool to each of them
        Arrays.stream(OLDEBCONFIG.lootInjectionLocations).forEach(location -> LOOT_INJECTIONS.add(Pair.of(location, createAdditivePool(DUNGEON_ADDITIONS, 1))));

        LOOT_INJECTIONS.add(Pair.of(new ResourceLocation("chests/gameplay/fishing/junk"), createAdditivePool(JUNK_FISHING_ADDITIONS, 4)));
        LOOT_INJECTIONS.add(Pair.of(new ResourceLocation("chests/gameplay/fishing/treasure"), createAdditivePool(TREASURE_FISHING_ADDITIONS, 4)));
        LOOT_INJECTIONS.add(Pair.of(new ResourceLocation("chests/jungle_temple_dispenser"), createAdditivePool(DISPENSER_ADDITIONS, 1)));

        if (!OLDEBCONFIG.injectMobDrops) return;
        // For each entity, if it's in the modifiableMobs or a hostile mob, add the loot pool
        BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
            ResourceLocation lootTable = entityType.getDefaultLootTable();
            ResourceLocation entityName = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            // mobs that aren't a hostile one and normally shouldn't drop anything
            if (Arrays.asList(OLDEBCONFIG.modifiableMobs).contains(entityName)) {
                LOOT_INJECTIONS.add(Pair.of(lootTable, createAdditivePool(WizardryMainMod.location("entities/mob_additions"), 1)));
            } else {
                // if it's a hostile mob
                if (!entityType.getCategory().isFriendly())
                    LOOT_INJECTIONS.add(Pair.of(lootTable, createAdditivePool(WizardryMainMod.location("entities/mob_additions"), 1)));
            }
        });
    }

    public static void applyInjections(BiConsumer<ResourceLocation, LootPool> injector) {
        if (LOOT_INJECTIONS.isEmpty()) initInjections();
        LOOT_INJECTIONS.forEach(loot -> injector.accept(loot.getFirst(), loot.getSecond()));
    }

    private static LootPool createAdditivePool(ResourceLocation entry, int weight) {
        return LootPool.lootPool().add(LootTableReference.lootTableReference(entry).setWeight(weight).setQuality(0)).setRolls(ConstantValue.exactly(1)).setBonusRolls(UniformGenerator.between(0, 1)).build();
    }
}
