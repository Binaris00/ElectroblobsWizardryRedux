package com.binaris.wizardry.core.config;

import com.mojang.serialization.Codec;

/**
 * A class representing a single configuration option. It holds the key, default value, current value, and a codec for serialization.
 *
 * @param <T> The type of the configuration option.
 */
public class ConfigOption<T> {
    private final String key;
    private final T defaultValue;
    private T value;
    private final Codec<T> codec;

    public ConfigOption(String key, T defaultValue, Codec<T> codec) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.codec = codec;
    }

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
}