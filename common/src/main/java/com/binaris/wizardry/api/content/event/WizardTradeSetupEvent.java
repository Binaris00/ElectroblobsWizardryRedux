package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryEvent;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.content.entity.living.Wizard;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import javax.annotation.Nullable;

/// Event fired when a Wizard's trades have been generated and are about to be finalized.
///
/// Add-on developers can listen to this event to modify the wizard's trade list, for example,
/// to add custom items only when the wizard has a specific element, or to remove trades conditionally.
public class WizardTradeSetupEvent extends WizardryEvent {
    private final Wizard wizard;
    private final MerchantOffers trades;

    public WizardTradeSetupEvent(Wizard wizard, MerchantOffers trades) {
        this.wizard = wizard;
        this.trades = trades;
    }

    /**
     * Gets the wizard whose trades are being set up.
     *
     * @return The {@link Wizard} entity.
     */
    public Wizard getWizard() {
        return wizard;
    }

    /**
     * Gets the wizard's current level (1–5).
     *
     * @return The wizard level.
     */
    public int getWizardLevel() {
        return wizard.getWizardLevel();
    }

    /**
     * Gets the wizard's element, or {@code null} if the wizard has no specific element.
     *
     * @return The wizard's {@link Element}, or {@code null}.
     */
    @Nullable
    public Element getElement() {
        return wizard.getElement();
    }

    /**
     * Gets the mutable list of trades for this wizard.
     * Add or remove {@link MerchantOffer}s to customize what the wizard sells.
     *
     * @return The mutable {@link MerchantOffers} list.
     */
    public MerchantOffers getTrades() {
        return trades;
    }
}
