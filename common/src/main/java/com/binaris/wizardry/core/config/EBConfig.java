package com.binaris.wizardry.core.config;

import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.List;

public class EBConfig implements IConfigProvider {
    private static final ArrayList<ConfigOption<?>> options = new ArrayList<>();

    public static final ConfigOption<Boolean> SPELL_HUD_FLIP_X = booleanOption("spell_hud_flip_x", false);
    public static final ConfigOption<Boolean> SPELL_HUD_FLIP_Y = booleanOption("spell_hud_flip_y", false);

    static ConfigOption<Boolean> booleanOption(String key, Boolean defaultValue) {
        ConfigOption<Boolean> option = new ConfigOption<>(key, defaultValue, Codec.BOOL);
        addOption(option);
        return option;
    }

    static ConfigOption<Integer> intOption(String key, Integer defaultValue) {
        ConfigOption<Integer> option = new ConfigOption<>(key, defaultValue, Codec.INT);
        addOption(option);
        return option;
    }

    static <T> void addOption(ConfigOption<T> option) {
        options.add(option);
    }

    @Override
    public List<ConfigOption<?>> getOptions() {
        return options;
    }
}
