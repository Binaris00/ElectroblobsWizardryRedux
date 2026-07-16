package com.binaris.wizardry.core.event;

/**
 * Dummy interface to use events
 */
public interface EventListener<E> {
    /**
     * Handles the event
     *
     * @param event the event instance
     */
    void onEvent(E event);
}
