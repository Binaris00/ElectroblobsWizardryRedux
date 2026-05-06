package com.binaris.wizardry.core.config.option;

import com.mojang.serialization.Codec;

import java.util.Optional;

/**
 * A class representing a single configuration option. It holds the key, default value,
 * current value, codec for serialization, and type of the option.
 *
 * @param <T> The type of the configuration option.
 *
 * @see ConfigOptionType
 */
public abstract class ConfigOption<T> {
    private final String key;
    private final T defaultValue;
    private T value;
    private final Codec<T> codec;
    private final ConfigOptionType type;

    protected String translationNameKey;
    protected String translationDescriptionKey;

    public ConfigOption(String key, T defaultValue, Codec<T> codec, ConfigOptionType type) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.codec = codec;
        this.type = type;
    }

    public ConfigOption(String key, T defaultValue, Codec<T> codec) {
        this(key, defaultValue, codec, ConfigOptionType.DEFAULT);
    }

    public abstract Optional<String> validate(T newValue);

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Codec<T> getCodec() {
        return codec;
    }

    public T getDefault() {
        return defaultValue;
    }

    public ConfigOptionType getType() {
        return type;
    }

    public String getRawDescription() {
        return translationDescriptionKey;
    }

    public String getRawName() {
        return translationNameKey;
    }
}