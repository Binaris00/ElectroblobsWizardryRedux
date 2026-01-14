package com.binaris.wizardry.core.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import com.binaris.wizardry.api.content.event.abstr.WizardryEvent;

/**
 * Internal use for <b>Electroblob's Wizardry</b>
 * <br><br>
 * Base interface to create {@link WizardryCancelableEvent WizardryCancelableEvent}
 * and {@link WizardryEvent WizardryEvent}
 * <br>
 * You don't need to use or access to this interface, this could help
 * you if you want to make events for you mod
 *
 */
public interface IWizardryEvent {
    boolean isCanceled();

    void setCanceled(boolean cancel);

    boolean canBeCanceled();
}
