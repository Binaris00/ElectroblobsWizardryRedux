package com.binaris.wizardry.api.content.event;


import com.binaris.wizardry.api.content.event.abstr.WizardryEvent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Event fired when a player already pick up an item, after the advancement is awarded and all the other operations
 * are done. (This event already checks the pickup delay, other things are not checked)
 * <p>
 * If you want to cancel the pickup event check {@link EBItemPickupEvent}
 */
public class EBPlayerItemPickupEvent extends WizardryEvent {
    ItemEntity itemEntity;
    Player entity;

    public EBPlayerItemPickupEvent(ItemEntity itemEntity, Player entity) {
        this.itemEntity = itemEntity;
        this.entity = entity;
    }

    public Player getEntity() {
        return entity;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }
}
