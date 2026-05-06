package com.binaris.wizardry.core.config.option;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class ResourceLocationConfigOption extends ConfigOption<ResourceLocation> {
    public ResourceLocationConfigOption(String key, ResourceLocation defaultValue) {
        super(key, defaultValue, ResourceLocation.CODEC, ConfigOptionType.DEFAULT);
    }

    @Override
    public Optional<String> validate(ResourceLocation newValue) {
        return Optional.empty();
    }
}
