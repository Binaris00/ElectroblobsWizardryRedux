package com.binaris.wizardry.core.platform.services;

import com.binaris.wizardry.core.networking.abst.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/// Platform abstraction for sending network messages (packets) between server and client.
///
/// Covers four transport targets: single player ([#sendTo]), client-to-server ([#sendToServer]),
/// chunk-tracking players ([#sendToTracking]), and all players in a dimension ([#sendToDimension]).
/// All messages must extend {@link com.binaris.wizardry.core.networking.abst.Message}. The Fabric implementation
/// uses Fabric API networking ({@code ServerPlayNetworking}, {@code ClientPlayNetworking}); the Forge implementation
/// delegates to {@code EBForgeNetwork.INSTANCE} with {@code PacketDistributor}. Obtained via {@code Services.NETWORK_HELPER}.
public interface INetworkHelper {
    /// Sends a message to a specific player. Callers should verify the caller is on the server side
    /// before invoking — this method does not perform its own side check.
    ///
    /// @param pPlayer the target player (must be non-null)
    /// @param pMessage the packet to send, extending {@link com.binaris.wizardry.core.networking.abst.Message}
    <T extends Message> void sendTo(ServerPlayer pPlayer, T pMessage);

    /// Sends a message from the client to the server. Called exclusively from client-side code
    /// (keybinding handlers, GUI button presses). Fire-and-forget — no response or acknowledgment.
    ///
    /// @param pMessage the packet to send to the server
    <T extends Message> void sendToServer(T pMessage);

    /// Sends a message to all players that have the chunk loaded at the given block position.
    /// Finds the chunk containing {@code pPos} and broadcasts to every player tracking that chunk.
    ///
    /// @param pLevel the server level (must be server-side)
    /// @param pPos the position within the chunk to broadcast from
    /// @param pMessage the packet to broadcast
    <T extends Message> void sendToTracking(ServerLevel pLevel, BlockPos pPos, T pMessage);

    /// Sends a message to all players that are currently tracking the given entity.
    /// Typically used to broadcast visual effects (spell casts, particles) to nearby observers.
    ///
    /// @param pEntity the entity whose tracking players will receive the message
    /// @param pMessage the packet to broadcast
    <T extends Message> void sendToTracking(Entity pEntity, T pMessage);

    /// Sends a message to every player currently in the specified dimension. Both implementations
    /// guard against a null server — if {@code server} is null, the method returns silently.
    ///
    /// @param server the server instance (maybe null, in which case the call is a no-op)
    /// @param packet the packet to broadcast
    /// @param dimension the dimension key whose players will receive the packet
    <T extends Message> void sendToDimension(MinecraftServer server, T packet, ResourceKey<Level> dimension);
}
