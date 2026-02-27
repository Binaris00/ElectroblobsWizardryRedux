package com.binaris.wizardry.core.networking.s2c;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.client.ScreenShakeHandler;
import com.binaris.wizardry.core.networking.abst.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ScreenShakeS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("screen_shake");
    float intensity;
    int duration;

    public ScreenShakeS2C(float intensity, int duration) {
        this.intensity = intensity;
        this.duration = duration;
    }

    public ScreenShakeS2C(FriendlyByteBuf pBuf) {
        this.intensity = pBuf.readFloat();
        this.duration = pBuf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf pBuf) {
        pBuf.writeFloat(intensity);
        pBuf.writeInt(duration);
    }

    @Override
    public void handleClient() {
        ScreenShakeHandler.triggerScreenShake(intensity, duration);
    }

    public float getIntensity() {
        return intensity;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
