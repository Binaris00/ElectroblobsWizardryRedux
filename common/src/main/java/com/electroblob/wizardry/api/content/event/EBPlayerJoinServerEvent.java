package com.electroblob.wizardry.api.content.event;

import com.electroblob.wizardry.api.content.event.abstr.WizardryEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

/**
 * This applies to: <br><br>
 * Fabric: {@code ServerPlayConnectionEvents#JOIN} <br>
 * Forge: {@code EntityJoinLevelEvent}
 *
 */
public class EBPlayerJoinServerEvent extends WizardryEvent {
    Player player;
    MinecraftServer server;

    public EBPlayerJoinServerEvent(Player player, MinecraftServer server) {
        this.player = player;
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public Player getPlayer() {
        return player;
    }
}
