package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Event fired when a player tosses an item from their inventory. fired from a
 * {@link net.minecraft.world.entity.player.Player#drop(ItemStack, boolean)}.
 */
public class EBItemTossEvent extends WizardryCancelableEvent {
    Player player;
    ItemStack stack;

    public EBItemTossEvent(Player player, ItemStack stack) {
        this.player = player;
        this.stack = stack;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getStack() {
        return stack;
    }
}
