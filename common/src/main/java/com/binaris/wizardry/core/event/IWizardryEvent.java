package com.binaris.wizardry.core.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import com.binaris.wizardry.api.content.event.abstr.WizardryEvent;

/**
 * Base interface to create {@link WizardryCancelableEvent WizardryCancelableEvent} and
 * {@link WizardryEvent WizardryEvent}
 */
public interface IWizardryEvent {
    /**
     * Checks if the event is canceled, avoiding the base implementation. Always needs to check {@link #canBeCanceled()} first
     *
     * @return true if the event is canceled, false otherwise
     */
    boolean isCanceled();

    /**
     * Sets the event canceled state, avoiding the base implementation. Always needs to check {@link #canBeCanceled()} first
     *
     * @param cancel true to cancel the event, false otherwise
     */
    void setCanceled(boolean cancel);

    /**
     * Marks if the event can be canceled (Normally you use {@link WizardryCancelableEvent WizardryCancelableEvent} for this)
     *
     * @return true if the event can be canceled, false otherwise
     */
    boolean canBeCanceled();
}
