package com.binaris.wizardry.client;

import com.binaris.wizardry.api.content.event.EBClientTickEvent;
import net.minecraft.world.entity.Entity;

public final class ScreenShakeHandler {
    private static float shakeIntensity = 0f;
    private static int shakeDuration = 0;

    public static void onClientTick(EBClientTickEvent event) {
        if (shakeDuration > 0) {
            shakeDuration--;

            Entity camera = event.getMinecraft().cameraEntity;
            if (camera != null) {
                // Apply a simple shake effect by modifying the camera's position
                float yawOffset = (float) (Math.sin(System.currentTimeMillis() * 0.1) * shakeIntensity);
                float pitchOffset = (float) (Math.cos(System.currentTimeMillis() * 0.1) * shakeIntensity);
                camera.setYRot(camera.getYRot() + yawOffset);
                camera.setXRot(camera.getXRot() + pitchOffset);
            }
        }
    }

    public static void triggerScreenShake(float intensity, int duration) {
        shakeIntensity = intensity;
        shakeDuration = duration;
    }

    private ScreenShakeHandler() {
    }


}
