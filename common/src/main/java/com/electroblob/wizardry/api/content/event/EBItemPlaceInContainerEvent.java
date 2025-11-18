package com.electroblob.wizardry.api.content.event;

import com.electroblob.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * Event fired when a player attempts to place an item in a container (Inside the slot class's {@code mayPlace} method)
 * Can be cancelled to prevent the item from being placed.
 */
public class EBItemPlaceInContainerEvent extends WizardryCancelableEvent {
    private final ItemStack stack;
    private final Container container;

    public EBItemPlaceInContainerEvent(ItemStack stack, Container container) {
        this.stack = stack;
        this.container = container;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Container getContainer() {
        return container;
    }
}