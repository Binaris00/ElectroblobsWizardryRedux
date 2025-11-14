package com.electroblob.wizardry.core.event;

/**
 * Internal use for <b>Electroblob's Wizardry</b>
 * <br><br>
 * Dummy interface to use events
 * <br>
 * You don't need to use or access to this interface, this could help
 * you if you want to make events for you mod
 *
 */
public interface EventListener<E> {
    void onEvent(E event);
}
