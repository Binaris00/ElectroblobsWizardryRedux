package com.binaris.wizardry.core.event;

/// Base interface for [WizardryEventBus], used to work as a blueprint for register-handle events
///
/// You don't need to use or access to this interface, this could help for normal stuff, this only helps you in case you
/// want to create a custom event bus for your mod.
public interface EventRegistry {
    /// Registers a new listener for a specific event type with the default priority order.
    ///
    /// @param <E>        the specific type of wizardry event
    /// @param eventClass the class of the event to listen for
    /// @param listener   the listener callback that will handle the event
    <E extends IWizardryEvent> void register(Class<E> eventClass, EventListener<E> listener);

    /// Registers a new listener for a specific event type with a custom priority execution order.
    ///
    /// @param <E>        the specific type of wizardry event
    /// @param eventClass the class of the event to listen for
    /// @param listener   the listener callback that will handle the event
    /// @param priority   the priority order determining when this listener fires relative to others
    <E extends IWizardryEvent> void register(Class<E> eventClass, EventListener<E> listener, EventPriorityOrder priority);

    /// Fires an event, distributing it to all registered listeners in their specified priority order.
    ///
    /// @param <E>   the specific type of wizardry event
    /// @param event the event instance to dispatch
    /// @return `true` if the event was successfully posted and processed; `false` otherwise
    /// (e.g., if the event was canceled)
    <E extends IWizardryEvent> boolean fire(E event);
}