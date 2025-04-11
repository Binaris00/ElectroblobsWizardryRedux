package com.electroblob.wizardry.api.content.event.abstr;

import com.electroblob.wizardry.core.event.EventListener;
import com.electroblob.wizardry.core.event.IWizardryEvent;

/**
 * Base event class to use if you want to create custom events
 * Just make a new subclass with this and fire the event when you need it
 * <br><br>
 * Keep in mind if one event before you cancel the used event your
 * listener will be used anyway, if you don't want this,
 * use {@link WizardryCancelableEvent#isCanceled()} before doing anything
 *
 * @see com.electroblob.wizardry.core.event.WizardryEventBus#fire(IWizardryEvent) WizardryEventBus#fire
 * @see com.electroblob.wizardry.core.event.WizardryEventBus#register(Class, EventListener) WizardryEventBus#register
 * */
public abstract class WizardryCancelableEvent implements IWizardryEvent {
    private boolean isCanceled;

    @Override
    public final boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public void setCanceled(boolean cancel) {
        this.isCanceled = cancel;
    }

    @Override
    public final boolean canBeCanceled() {
        return true;
    }
}
