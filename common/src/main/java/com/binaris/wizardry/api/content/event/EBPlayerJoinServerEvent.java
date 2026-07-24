package com.binaris.wizardry.api.content.event;

import com.binaris.wizardry.api.content.event.abstr.WizardryEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

/// This applies to:
///
/// Fabric: `ServerPlayConnectionEvents#JOIN`
///
/// Forge: `EntityJoinLevelEvent`
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
