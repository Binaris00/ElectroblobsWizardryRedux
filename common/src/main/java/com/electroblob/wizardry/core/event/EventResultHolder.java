package com.electroblob.wizardry.core.event;

public class EventResultHolder<T> {
    private final EventResult result;
    private final T object;

    private EventResultHolder(EventResult result, T object) {
        this.result = result;
        this.object = object;
    }

    public static <T> EventResultHolder<T> success(T object) {
        return new EventResultHolder<>(EventResult.SUCCESS, object);
    }

    public static <T> EventResultHolder<T> fail(T object) {
        return new EventResultHolder<>(EventResult.FAIL, object);
    }

    public EventResult getResult() {
        return result;
    }

    public T getObject() {
        return object;
    }
}
