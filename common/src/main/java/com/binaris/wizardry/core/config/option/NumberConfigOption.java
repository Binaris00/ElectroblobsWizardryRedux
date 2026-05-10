package com.binaris.wizardry.core.config.option;

import com.mojang.serialization.Codec;

import java.util.Optional;

public class NumberConfigOption<T extends Number & Comparable<T>> extends ConfigOption<T> {
    protected  T minValue;
    protected  T maxValue;

    public NumberConfigOption(String key, T defaultValue, T minValue, T maxValue, Codec<T> codec) {
        super(key, defaultValue, codec, ConfigOptionType.SLIDER);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public static ConfigOption<Integer> integer(String key, int defaultValue, int minValue, int maxValue) {
        return new NumberConfigOption<>(key, defaultValue, minValue, maxValue,Codec.INT);
    }

    public static ConfigOption<Float> floating(String key, float defaultValue, float minValue, float maxValue) {
        return new NumberConfigOption<>(key, defaultValue, minValue, maxValue, Codec.FLOAT);
    }

    public static ConfigOption<Double> doublePrecision(String key, double defaultValue, double minValue, double maxValue) {
        return new NumberConfigOption<>(key, defaultValue, minValue, maxValue, Codec.DOUBLE);
    }


    @Override
    public Optional<String> validate(T newValue) {
        if (newValue.compareTo(minValue) < 0 || newValue.compareTo(maxValue) > 0) {
            return Optional.of("config.ebwizardry.number_option.out_of_range");
        }
        return Optional.empty();
    }

    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }
}
