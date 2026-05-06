package com.binaris.wizardry.core.config;

import com.binaris.wizardry.core.config.option.ConfigOption;

import java.util.List;

public interface IConfigProvider {
    List<ConfigOption<?>> getOptions();
}

