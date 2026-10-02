package com.binaris.wizardry.api.network;

import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;

/// Implemented by [ClientboundUpdateMobEffectPacket] (via mixin) to expose whether the synced effect
/// was a magic effect instance.
public interface MagicEffectPacket {
    boolean ebwizardry$isMagic();
}