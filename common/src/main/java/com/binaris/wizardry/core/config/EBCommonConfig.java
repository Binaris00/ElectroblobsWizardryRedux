package com.binaris.wizardry.core.config;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.core.config.option.BoolConfigOption;
import com.binaris.wizardry.core.config.option.ConfigOption;
import com.binaris.wizardry.core.config.util.ConfigType;

import java.util.ArrayList;
import java.util.Collection;

public final class EBCommonConfig implements ConfigProvider {
    private static final ArrayList<ConfigOption<?>> OPTIONS = new ArrayList<>();
    public static final EBCommonConfig INSTANCE = new EBCommonConfig();

    public static final ConfigOption<Boolean> ENABLE_DEBUG_COMMANDS = add(BoolConfigOption.booleanOption("enable_debug_commands", false));

    private static <T> ConfigOption<T> add(ConfigOption<T> option) {
        OPTIONS.add(option);
        return option;
    }

    @Override
    public String getModid() {
        return WizardryMainMod.MOD_ID;
    }

    @Override
    public ConfigType getType() {
        return ConfigType.COMMON;
    }

    @Override
    public Collection<ConfigOption<?>> build() {
        return OPTIONS;
    }


    private EBCommonConfig() {
    }
}
