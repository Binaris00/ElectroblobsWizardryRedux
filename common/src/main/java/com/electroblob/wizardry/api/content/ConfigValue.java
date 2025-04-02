package com.electroblob.wizardry.api.content;

public class ConfigValue<T extends Number> {
    private final T lowerbound;
    private final T upperbound;

    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }

    public ConfigValue(T lowerbound, T upperbound) {
        this(null, lowerbound, upperbound);
    }

    public ConfigValue(T defaultValue, T lowerbound, T upperbound) {
        this.value = defaultValue;
        this.lowerbound = lowerbound;
        this.upperbound = upperbound;
    }
}
