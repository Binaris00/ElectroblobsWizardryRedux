package com.electroblob.wizardry.core.event;

/**
 * Internal use for <b>Electroblob's Wizardry</b>
 * <br><br>
 * Base interface for {@link WizardryEventBus}, used to work
 * as a blueprint for register-handle events
 * <br>
 * You don't need to use or access to this interface, this could help
 * you if you want to make events for you mod
 * */
public interface EventRegistry {
    <E extends IWizardryEvent> void register(Class<E> eventClass, EventListener<E> listener);
    <E extends IWizardryEvent> boolean fire(E event);
}