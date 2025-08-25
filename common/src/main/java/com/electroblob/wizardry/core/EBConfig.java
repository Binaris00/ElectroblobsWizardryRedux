package com.electroblob.wizardry.core;

import com.electroblob.wizardry.api.content.ConfigValue;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// Todo EBConfig? Still no used, contains some fields that were in the Constant class, need to rewrite
public final class EBConfig {
    public static final double MIN_PRECISE_DOUBLE = -9_007_199_254_740_992.0;
    public static final double MAX_PRECISE_DOUBLE = 9_007_199_254_740_991.0;
    public static final float MIN_PRECISE_FLOAT = -16_777_216;
    public static final float MAX_PRECISE_FLOAT = 16_777_215;

    public static final int MANA_PER_SHARD = 10;
    public static final int MANA_PER_CRYSTAL = 100;
    public static final int GRAND_CRYSTAL_MANA = 400;

    public static final int NON_ELEMENTAL_UPGRADE_BONUS = 3;

    public static boolean damageTypePerElement = false;
    public static boolean playersMoveEachOther = true;
    public static boolean replaceVanillaFallDamage = true;
    public static boolean showSpellHUD = true;
    public static boolean showChargeMeter = true;
    public static boolean preventBindingSameSpellTwiceToWands = false;
    public static boolean singleUseSpellBooks = false;
    public static boolean legacyWandLevelling = false;
    public static boolean wandsMustBeHeldToDecrementCooldown = false;
    public static boolean discoveryMode = true;
    public static double forfeitChance = 0.2; // 2

    public static float COOLDOWN_REDUCTION_PER_LEVEL = 0.15f;
    public static final float STORAGE_INCREASE_PER_LEVEL = 0.15f;
    public static float POTENCY_INCREASE_PER_TIER = 0.15f;
    public static float DURATION_INCREASE_PER_LEVEL = 0.25f;
    public static float RANGE_INCREASE_PER_LEVEL = 0.25f;
    public static float BLAST_RADIUS_INCREASE_PER_LEVEL = 0.25f;
    public static final int CONDENSER_TICK_INTERVAL = 50;
    public static final int UPGRADE_STACK_LIMIT = 3;
    public static boolean booksPauseGame = true;

    private static final String[] DEFAULT_LOOT_INJECTION_LOCATIONS = {"minecraft:chests/simple_dungeon",
            "minecraft:chests/abandoned_mineshaft", "minecraft:chests/desert_pyramid", "minecraft:chests/jungle_temple",
            "minecraft:chests/stronghold_corridor", "minecraft:chests/stronghold_crossing",
            "minecraft:chests/stronghold_library", "minecraft:chests/igloo_chest", "minecraft:chests/woodland_mansion",
            "minecraft:chests/end_city_treasure"};

    public static ResourceLocation[] lootInjectionLocations = toResourceLocations(DEFAULT_LOOT_INJECTION_LOCATIONS);

    public static Map<Pair<ResourceLocation, Short>, Integer> currencyItems = new HashMap<>();

    public final ConfigValue<Double> defaultMana = new ConfigValue<>(100.0, MIN_PRECISE_DOUBLE, MAX_PRECISE_DOUBLE);


    private static ResourceLocation[] toResourceLocations(String... strings) {
        return Arrays.stream(strings).map(s -> new ResourceLocation(s.toLowerCase(Locale.ROOT).trim())).toArray(ResourceLocation[]::new);
    }
    private EBConfig() {}
}
