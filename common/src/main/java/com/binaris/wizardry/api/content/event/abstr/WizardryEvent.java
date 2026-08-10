package com.binaris.wizardry.api.content.event.abstr;

import com.binaris.wizardry.core.event.EventListener;
import com.binaris.wizardry.core.event.IWizardryEvent;
import com.binaris.wizardry.core.event.WizardryEventBus;

/// Base event class to use if you want to create custom events.
///
/// @see WizardryEventBus#fire(IWizardryEvent) WizardryEventBus#fire
/// @see WizardryEventBus#register(Class, EventListener) WizardryEventBus#register
public abstract class WizardryEvent implements IWizardryEvent {
    @Override
    public final boolean canBeCanceled() {
        return false;
    }

    @Override
    public final boolean isCanceled() {
        return false;
    }

    @Override
    public final void setCanceled(boolean cancel) {
    }
}
