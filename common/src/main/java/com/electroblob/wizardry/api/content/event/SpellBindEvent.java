package com.electroblob.wizardry.api.content.event;

import com.electroblob.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import com.electroblob.wizardry.content.menu.ArcaneWorkbenchMenu;
import net.minecraft.world.entity.player.Player;

/**
 * Event fired when a player presses the apply button in the Arcane Workbench GUI (not confuse, this is only called on
 * the server side). This event is fired before anything is actually done, so you can use it to prevent or modify any
 * changes. This event is cancelable.
 * */
public class SpellBindEvent extends WizardryCancelableEvent {
    Player player;
    ArcaneWorkbenchMenu menu;

    public SpellBindEvent(Player player, ArcaneWorkbenchMenu menu) {
        this.player = player;
        this.menu = menu;
    }

    public Player getPlayer() {
        return player;
    }

    public ArcaneWorkbenchMenu getMenu() {
        return menu;
    }
}
