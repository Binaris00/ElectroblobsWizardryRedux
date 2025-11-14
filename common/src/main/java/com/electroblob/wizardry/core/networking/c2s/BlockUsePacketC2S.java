package com.electroblob.wizardry.core.networking.c2s;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.core.networking.abst.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;

public class BlockUsePacketC2S implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("block_use_packet");
    private BlockPos pos;

    public BlockUsePacketC2S(BlockPos pos) {
        this.pos = pos;
    }

    public BlockUsePacketC2S(FriendlyByteBuf pBuf) {
        this.pos = pBuf.readBlockPos();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void encode(FriendlyByteBuf pBuf) {
        pBuf.writeBlockPos(pos);
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        Pig pig = EntityType.PIG.create(server.getLevel(Level.OVERWORLD));
        if (pig != null) {
            pig.setPos(pos.getX(), pos.getY(), pos.getZ());
            pig.setAge(0);
            server.getLevel(Level.OVERWORLD).addFreshEntity(pig);
        }
    }
}
