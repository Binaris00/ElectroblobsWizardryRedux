package com.electroblob.wizardry.core.networking.s2c;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.core.networking.abst.Message;
import com.electroblob.wizardry.core.networking.ClientMessageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Used just for testing the network api
 */
public class TestParticlePacketS2C implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("test_particle_packet");
    private final BlockPos pos;
    private final int color;

    public TestParticlePacketS2C(BlockPos pos, int color) {
        this.pos = pos;
        this.color = color;
    }

    public TestParticlePacketS2C(FriendlyByteBuf pBuf) {
        this.pos = pBuf.readBlockPos();
        this.color = pBuf.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf pBuf) {
        pBuf.writeBlockPos(pos);
        pBuf.writeInt(color);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void handleClient() {
        ClientMessageHandler.testParticle(this);
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getColor() {
        return color;
    }
}
