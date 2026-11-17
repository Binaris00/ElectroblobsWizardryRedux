package com.binaris.wizardry.core.networking;

import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.core.networking.s2c.ScreenShakeS2C;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ScreenShakeHelper {
    public static void sendScreenShake(ServerPlayer player, float intensity, int duration) {
        Services.NETWORK_HELPER.sendTo(player, new ScreenShakeS2C(intensity, duration));
    }

    public static void sendScreenShakeNear(Level level, Vec3 pos, double radius, float intensity, int duration) {
        if (level.isClientSide) return;

        for (ServerPlayer player : EntityUtil.getEntitiesWithinRadius(radius, pos.x, pos.y, pos.z, level, ServerPlayer.class)) {
            sendScreenShake(player, intensity, duration);
        }
    }

    private ScreenShakeHelper() {
    }
}
