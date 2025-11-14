package com.electroblob.wizardry.api.content.event.abstr;

import com.electroblob.wizardry.core.event.EventListener;
import com.electroblob.wizardry.core.event.IWizardryEvent;

/**
 * Base event class to use if you want to create custom events
 * Just make a new subclass with this and fire the event when you need it
 *
 * @see com.electroblob.wizardry.core.event.WizardryEventBus#fire(IWizardryEvent) WizardryEventBus#fire
 * @see com.electroblob.wizardry.core.event.WizardryEventBus#register(Class, EventListener) WizardryEventBus#register
 *
 */
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
