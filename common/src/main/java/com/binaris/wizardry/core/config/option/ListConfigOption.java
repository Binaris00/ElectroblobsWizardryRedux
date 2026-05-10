package com.binaris.wizardry.core.config.option;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public class ListConfigOption<T> extends ConfigOption<List<T>> {
    public ListConfigOption(String key, List<T> defaultValue, Codec<T> elementCodec) {
        super(key, defaultValue, elementCodec.listOf(), ConfigOptionType.DEFAULT);
    }

    @Override
    public Optional<String> validate(List<T> newValue) {
        if (newValue == null) return Optional.of("config.ebwizardry.list_option.null");
        return Optional.empty();
    }

    public static <E> ListConfigOption<E> of(String key, List<E> defaultValue, Codec<E> elementCodec) {
        return new ListConfigOption<>(key, defaultValue, elementCodec);
    }

    public static ListConfigOption<ResourceLocation> resourceLocation(String key, List<ResourceLocation> defaultValue) {
        return new ListConfigOption<>(key, defaultValue, ResourceLocation.CODEC);
    }
}