package com.electroblob.wizardry.network;

import com.electroblob.wizardry.capabilities.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Handles messages received on the client side, we may only call client-side methods from here because we don't want to
 * accidentally reference client-only code on the server side.
 */
public final class ClientMessageHandlerForge {
    private ClientMessageHandlerForge() {
    }

    public static void arcaneLock(ArcaneLockSyncPacketS2C m) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        BlockEntity blockEntity = level.getBlockEntity(m.getPos());
        if (blockEntity == null) return;

        blockEntity.getCapability(ArcaneLockDataHolder.INSTANCE).ifPresent(arcaneLockData ->
                arcaneLockData.deserializeNBT(m.getData()));
    }

    public static void minionSync(MinionSyncPacketS2C m) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        var entity = level.getEntity(m.getEntityId());
        if(entity == null) return;

        entity.getCapability(MinionDataHolder.INSTANCE).ifPresent(minionData ->
                minionData.deserializeNBT(m.getData()));
    }

    public static void playerCapabilitySync(PlayerCapabilitySyncPacketS2C m) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        switch (m.getType()) {
            case CAST_COMMAND -> player.getCapability(CastCommandDataHolder.INSTANCE)
                    .ifPresent(d -> d.deserializeNBT(m.getData()));
            case SPELL_MANAGER -> player.getCapability(SpellManagerDataHolder.INSTANCE)
                    .ifPresent(d -> d.deserializeNBT(m.getData()));
            case WIZARD_DATA -> player.getCapability(WizardDataHolder.INSTANCE)
                    .ifPresent(d -> d.deserializeNBT(m.getData()));
        }
    }
}
