package com.binaris.wizardry.core.networking.abst;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/// Common networking abstraction for all mod packets sent between server and client.
///
/// Each implementation defines its own data fields and provides a constructor taking a
/// {@code FriendlyByteBuf} for deserialization.
public interface Message {
    /// Returns the unique channel identifier used to register this packet type.
    ///
    /// Used by Fabric's networking API ({@code ServerPlayNetworking.send}, etc.) to
    /// route packets to the correct global receiver. On Forge this identifier is not
    /// used externally since packets are registered by class type.
    ///
    /// @return the resource location identifying this message on the network channel.
    ResourceLocation getId();

    /// Serializes this message's data into the network buffer.
    ///
    /// Must write fields in the exact same order that the deserialization constructor
    /// reads them. Called by the networking layer before each send operation.
    ///
    /// @param pBuf the packet byte buffer to write to.
    void encode(FriendlyByteBuf pBuf);

    /// Called on the client thread when a  server-to-client packet is received.
    ///
    /// Default implementation is a no-op. Override in S2C messages to execute
    /// client-side logic (e.g. spawning particles, syncing data, rendering effects).
    default void handleClient() {
    }

    /// Called on the server thread when a client-to-server packet is received.
    ///
    /// Default implementation is a no-op. Override in C2S messages to execute
    /// server-side logic (e.g. handling key bindings, processing block interactions).
    /// The sending player is provided for validation and context.
    ///
    /// @param server the current Minecraft server instance.
    /// @param player the player who sent this packet.
    default void handleServer(MinecraftServer server, ServerPlayer player) {
    }
}
