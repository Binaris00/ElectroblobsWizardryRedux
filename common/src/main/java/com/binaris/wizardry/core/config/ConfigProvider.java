package com.binaris.wizardry.core.config;

import com.binaris.wizardry.core.config.option.ConfigOption;
import com.binaris.wizardry.core.config.util.ConfigType;

import java.util.Collection;

public interface ConfigProvider {
    String getModid();

    ConfigType getType();

    default String getConfigName() {
        return getModid() + "-" + getType().name().toLowerCase();
    }

    Collection<ConfigOption<?>> build();
}
