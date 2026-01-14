package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryCancelableEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class EBPlayerInteractEntityEvent extends WizardryCancelableEvent {
    private final Player player;
    private final Entity target;

    public EBPlayerInteractEntityEvent(Player player, Entity target) {
        this.player = player;
        this.target = target;
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getTarget() {
        return target;
    }
}
