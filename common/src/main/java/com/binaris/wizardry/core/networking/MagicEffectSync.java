package com.binaris.wizardry.core.networking;

import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.core.networking.s2c.MagicEffectSyncS2C;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

/// Handles the checks and information about the sync process related to the custom magic effects particles
public final class MagicEffectSync {
    public static final int CLIENT_DURATION_SLACK = 40;

    private MagicEffectSync() {
    }

    /// Sends an add/remove message for the given effect to every client tracking the entity. Doesn't do anything on client
    ///
    /// @param entity   the entity holding the effect
    /// @param instance the effect that was added, updated or removed
    /// @param action   what happened to the effect
    public static void sendToTracking(LivingEntity entity, MobEffectInstance instance, MagicEffectSyncS2C.Action action) {
        if (entity.level().isClientSide) return;
        if (!(instance.getEffect() instanceof MagicMobEffect)) return;
        Services.NETWORK_HELPER.sendToTracking(entity, new MagicEffectSyncS2C(entity.getId(), instance, action));
    }

    /// Sends every wizardry effect the entity currently has to a single client. Used when a client starts tracking
    /// an entity, where no add/remove event is fired for the effects that are already active.
    ///
    /// @param player the client to send to
    /// @param entity the entity being tracked
    public static void sendAllTo(ServerPlayer player, LivingEntity entity) {
        if (entity.level().isClientSide) return;
        for (MobEffectInstance instance : entity.getActiveEffects()) {
            if (instance.getEffect() instanceof MagicMobEffect) {
                Services.NETWORK_HELPER.sendTo(player, new MagicEffectSyncS2C(entity.getId(), instance,
                        MagicEffectSyncS2C.Action.ADD));
            }
        }
    }
}