package com.electroblob.wizardry.core.networking.s2c;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/** Used just for testing the network api */
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
    public void handleClient(Minecraft minecraft, Player player) {
        minecraft.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0);
        player.sendSystemMessage(Component.literal("Test particle at " + pos + " with color " + color));
    }

    public BlockPos getPos() {
        return pos;
    }

    public int getColor() {
        return color;
    }
}
