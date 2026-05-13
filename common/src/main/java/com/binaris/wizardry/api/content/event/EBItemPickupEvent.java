package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Event fired when a player is about to pick up an item, just before adding it to the inventory. You can cancel this event
 * to prevent the item from being picked up.
 */
public class EBItemPickupEvent extends WizardryCancelableEvent {
    ItemEntity itemEntity;
    Player entity;

    public EBItemPickupEvent(ItemEntity itemEntity, Player entity) {
        this.itemEntity = itemEntity;
        this.entity = entity;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    public Player getEntity() {
        return entity;
    }
}
