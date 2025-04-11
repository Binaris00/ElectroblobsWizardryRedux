package com.electroblob.wizardry.core.event;

import com.electroblob.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import com.electroblob.wizardry.api.content.event.abstr.WizardryEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal use for <b>Electroblob's Wizardry</b>
 * <br><br>
 * Help to register and fire all the events used in the mod.
 * <ul>
 *     <li> Use {@link WizardryEventBus#register(Class, EventListener)} to register your custom event listeners inside your mod init </li>
 *     <li> Use {@link WizardryEventBus#fire(IWizardryEvent)} to fire any event when you want it </li>
 *     <li> Check {@link WizardryCancelableEvent WizardryCancelableEvent}
 *     and {@link WizardryEvent WizardryEvent} to create custom events </li>
 * </ul>
 * <br>
 * You don't need to use or access to this interface, this could help
 * you if you want to make events for you mod
 * */
public final class WizardryEventBus implements EventRegistry {
    private static final WizardryEventBus INSTANCE = new WizardryEventBus();
    private final Map<Class<? extends IWizardryEvent>, List<EventListener<? extends IWizardryEvent>>> listeners = new HashMap<>();

    @Override
    public synchronized <E extends IWizardryEvent> void register(Class<E> eventClass, EventListener<E> listener) {
        listeners.computeIfAbsent(eventClass, key -> new ArrayList<>()).add(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <E extends IWizardryEvent> boolean fire(E event) {
        List<EventListener<? extends IWizardryEvent>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (EventListener<? extends IWizardryEvent> listener : eventListeners) {
                ((EventListener<E>) listener).onEvent(event);
            }
        }

        return event.canBeCanceled() && event.isCanceled();
    }

    public static WizardryEventBus getInstance() {
        return INSTANCE;
    }
}