package com.binaris.wizardry.client;

import com.binaris.wizardry.api.content.event.EBClientTickEvent;
import com.binaris.wizardry.core.config.EBClientConfig;
import com.binaris.wizardry.core.networking.s2c.ScreenShakeS2C;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/// A counter-based pitch oscillation: the remaining duration determines both the
/// time left and the magnitude, which naturally decays as the counter decreases.
public final class ScreenShakeHandler {
    private static int screenShakeCounter = 0;
    private static final float SHAKINESS = 0.5f;

    public static void shakeScreen(float intensity) {
        if (EBClientConfig.SCREEN_SHAKE.get()) {
            screenShakeCounter = (int) (intensity / SHAKINESS);
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                // Start halfway down
                player.setXRot(player.getXRot() - intensity * 0.5f);
            }
        }
    }

    public static void shakeScreen(float intensity, int duration) {
        if (EBClientConfig.SCREEN_SHAKE.get()) {
            screenShakeCounter = Math.max(duration, (int) (intensity / SHAKINESS));
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.setXRot(player.getXRot() - intensity * 0.5f);
            }
        }
    }

    public static void sendScreenShake(ServerPlayer player, float intensity, int duration) {
        Services.NETWORK_HELPER.sendTo(player, new ScreenShakeS2C(intensity, duration));
    }

    public static void onClientTick(EBClientTickEvent event) {
        Player player = event.getMinecraft().player;
        if (player == null) {
            return;
        }

        if (EBClientConfig.SCREEN_SHAKE.get()) {
            if (screenShakeCounter > 0) {
                float magnitude = screenShakeCounter * SHAKINESS;
                player.setXRot(player.getXRot() + (screenShakeCounter % 2 == 0 ? magnitude : -magnitude));
                screenShakeCounter--;
            }
        } else {
            screenShakeCounter = 0;
        }
    }

    private ScreenShakeHandler() {
    }
}