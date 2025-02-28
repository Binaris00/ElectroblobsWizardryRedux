package com.electroblob.wizardry;

import com.electroblob.wizardry.api.common.ConfigValue;

public final class EBConfig {

    // These values are the lowest and highest a double can go before precision loss (assuming you're incrementing and decrementing them)
    // When doubles are this high in value they aren't precise fractionally
    public static final double MIN_PRECISE_DOUBLE = -9_007_199_254_740_992.0;
    public static final double MAX_PRECISE_DOUBLE = 9_007_199_254_740_991.0;

    public static final float MIN_PRECISE_FLOAT = -16_777_216;
    public static final float MAX_PRECISE_FLOAT = 16_777_215;

    public final ConfigValue<Double> defaultMana = new ConfigValue<>(100.0, MIN_PRECISE_DOUBLE, MAX_PRECISE_DOUBLE);


    static EBConfig instantiate() {
        return new EBConfig();
    }
    private EBConfig() {}
}
