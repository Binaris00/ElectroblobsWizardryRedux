package com.electroblob.wizardry.core;

import com.electroblob.wizardry.api.content.ConfigValue;

public final class EBConfig {
    public static final double MIN_PRECISE_DOUBLE = -9_007_199_254_740_992.0;
    public static final double MAX_PRECISE_DOUBLE = 9_007_199_254_740_991.0;
    public static final float MIN_PRECISE_FLOAT = -16_777_216;
    public static final float MAX_PRECISE_FLOAT = 16_777_215;

    public static boolean damageTypePerElement = false;
    public static boolean playersMoveEachOther = true;
    public static boolean replaceVanillaFallDamage = true;

    public final ConfigValue<Double> defaultMana = new ConfigValue<>(100.0, MIN_PRECISE_DOUBLE, MAX_PRECISE_DOUBLE);

    static EBConfig instantiate() {
        return new EBConfig();
    }
    private EBConfig() {}
}
