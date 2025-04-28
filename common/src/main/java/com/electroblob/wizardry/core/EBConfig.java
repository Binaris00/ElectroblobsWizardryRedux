package com.electroblob.wizardry.core;

import com.electroblob.wizardry.api.content.ConfigValue;

public final class EBConfig {
    public static final double MIN_PRECISE_DOUBLE = -9_007_199_254_740_992.0;
    public static final double MAX_PRECISE_DOUBLE = 9_007_199_254_740_991.0;
    public static final float MIN_PRECISE_FLOAT = -16_777_216;
    public static final float MAX_PRECISE_FLOAT = 16_777_215;

    public static final int MANA_PER_SHARD = 10;
    public static final int MANA_PER_CRYSTAL = 100;
    public static final int GRAND_CRYSTAL_MANA = 400;

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
    public static double forfeitChance = 0.7; // 2

    public static float COOLDOWN_REDUCTION_PER_LEVEL = 0.15f;
    public static final float STORAGE_INCREASE_PER_LEVEL = 0.15f;
    public static float POTENCY_INCREASE_PER_TIER = 0.15f;
    public static float DURATION_INCREASE_PER_LEVEL = 0.25f;
    public static float RANGE_INCREASE_PER_LEVEL = 0.25f;
    public static float BLAST_RADIUS_INCREASE_PER_LEVEL = 0.25f;
    public static final int CONDENSER_TICK_INTERVAL = 50;
    public static final int UPGRADE_STACK_LIMIT = 3;

    public final ConfigValue<Double> defaultMana = new ConfigValue<>(100.0, MIN_PRECISE_DOUBLE, MAX_PRECISE_DOUBLE);

    static EBConfig instantiate() {
        return new EBConfig();
    }
    private EBConfig() {}
}
