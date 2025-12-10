package com.electroblob.wizardry.core.networking.s2c;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.core.networking.ClientMessageHandler;
import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ParticleBuilderS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("particle_builder");
    private final ParticleBuilder.ParticleData data;

    public ParticleBuilderS2C(ParticleBuilder.ParticleData data) {
        this.data = data;
    }

    public ParticleBuilderS2C(FriendlyByteBuf buf) {
        this.data = ParticleBuilder.ParticleData.read(buf);
    }


    public ParticleBuilder.ParticleData getData() {
        return data;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void encode(FriendlyByteBuf pBuf) {
        data.write(pBuf);
    }

    @Override
    public void handleClient() {
        ClientMessageHandler.particleBuilder(this);
    }
}