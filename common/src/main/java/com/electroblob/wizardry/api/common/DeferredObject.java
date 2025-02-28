package com.electroblob.wizardry.api.common;

import java.util.function.Supplier;

public class DeferredObject<T> implements Supplier<T> {
    private Supplier<T> supplier;
    private T cachedValue;
    private boolean isInitialized = false;

    public DeferredObject(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (!isInitialized) {
            cachedValue = supplier.get();
            isInitialized = true;
            supplier = null; // Free up memory
        }
        return cachedValue;
    }
}

