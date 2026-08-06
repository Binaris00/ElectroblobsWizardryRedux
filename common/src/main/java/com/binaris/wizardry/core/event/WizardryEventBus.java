package com.binaris.wizardry.core.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import com.binaris.wizardry.api.content.event.abstr.WizardryEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/// Internal use for **Electroblob's Wizardry**
///
/// Help to register and fire all the events used in the mod.
///
///   - Use [WizardryEventBus#register(Class, EventListener)] to register your custom event listeners inside your mod init
///   - Use [WizardryEventBus#fire(IWizardryEvent)] to fire any event when you want it
///   - Check [`WizardryCancelableEvent`][WizardryCancelableEvent]
///     and [`WizardryEvent`][WizardryEvent] to create custom events
///
/// You don't need to use or access to this interface, this could help
/// you if you want to make events for you mod
public class WizardryEventBus implements EventRegistry {
    private static final WizardryEventBus INSTANCE = new WizardryEventBus();
    private final Map<Class<? extends IWizardryEvent>, List<PrioritizedListener<? extends IWizardryEvent>>> listeners = new HashMap<>();

    public static WizardryEventBus getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized <E extends IWizardryEvent> void register(Class<E> eventClass, EventListener<E> listener) {
        register(eventClass, listener, EventPriorityOrder.NORMAL);
    }

    @Override
    public synchronized <E extends IWizardryEvent> void register(Class<E> eventClass, EventListener<E> listener, EventPriorityOrder priority) {
        List<PrioritizedListener<? extends IWizardryEvent>> eventListeners = listeners.computeIfAbsent(eventClass, key -> new ArrayList<>());
        eventListeners.add(new PrioritizedListener<>(listener, priority, eventListeners.size()));
        eventListeners.sort(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized <E extends IWizardryEvent> boolean fire(E event) {
        List<PrioritizedListener<? extends IWizardryEvent>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (PrioritizedListener<? extends IWizardryEvent> prioritizedListener : eventListeners) {
                ((EventListener<E>) prioritizedListener.listener).onEvent(event);
            }
        }

        return event.canBeCanceled() && event.isCanceled();
    }

    /// Fires an event, distributing it to all registered listeners in their specified priority order. This is the same as using
    /// [#fire(IWizardryEvent)]
    ///
    /// @param <E>   the specific type of wizardry event
    /// @param event the event instance to dispatch
    /// @return `true` if the event was successfully posted and processed; `false` otherwise
    /// (e.g., if the event was canceled)
    public static <E extends IWizardryEvent> boolean fireEvent(E event){
        return getInstance().fire(event);
    }

    /// Associates an [EventListener] with an [EventPriorityOrder] and its insertion order to preserve
    /// deterministic execution when fired.
    ///
    /// Implements [Comparable] to sort listeners first by their priority (highest priority first),
    /// and secondarily by their FIFO (First-In, First-Out) registration sequence if priorities match.
    ///
    /// @param <E> the specific type of wizardry event this listener handles
    private record PrioritizedListener<E extends IWizardryEvent>(EventListener<E> listener, EventPriorityOrder priority,
                                                                     int registrationOrder) implements Comparable<PrioritizedListener<?>> {
        @Override
            public int compareTo(PrioritizedListener<?> other) {
                int priorityCompare = Integer.compare(other.priority.ordinal(), this.priority.ordinal());
                if (priorityCompare != 0) {
                    return priorityCompare;
                }
                return Integer.compare(this.registrationOrder, other.registrationOrder);
            }
        }
}