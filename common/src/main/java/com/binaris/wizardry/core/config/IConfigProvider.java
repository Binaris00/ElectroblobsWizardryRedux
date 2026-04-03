package com.binaris.wizardry.core.config;

import java.util.List;

public interface IConfigProvider {
    List<ConfigOption<?>> getOptions();
}

