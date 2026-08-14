package com.binaris.wizardry.core.config.option;

import com.mojang.serialization.Codec;

import java.util.Optional;

public class BoolConfigOption extends ConfigOption<Boolean> {
    public BoolConfigOption(String key, Boolean defaultValue) {
        super(key, defaultValue, Codec.BOOL, ConfigOptionType.SWITCH);
    }

    public static ConfigOption<Boolean> booleanOption(String key, boolean defaultValue) {
        return new BoolConfigOption(key, defaultValue);
    }

    @Override
    public Optional<String> validate(Boolean newValue) {
        // Boolean values don't need validation
        return Optional.empty();
    }
}
