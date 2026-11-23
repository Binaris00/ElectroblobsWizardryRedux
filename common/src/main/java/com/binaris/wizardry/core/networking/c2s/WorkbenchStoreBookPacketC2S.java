package com.binaris.wizardry.core.networking.c2s;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.core.networking.abst.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

///Inserts the spell book carried by the player into a nearby bookshelf from the arcane workbench side panel.
public class WorkbenchStoreBookPacketC2S implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("workbench_store_book");
    private static final double MAX_DISTANCE_SQR = 64.0;
    private final @Nullable BlockPos targetPos;
    private final int targetSlot;

    public WorkbenchStoreBookPacketC2S() {
        this.targetPos = null;
        this.targetSlot = -1;
    }

    public WorkbenchStoreBookPacketC2S(BlockPos targetPos, int targetSlot) {
        this.targetPos = targetPos.immutable();
        this.targetSlot = targetSlot;
    }

    public WorkbenchStoreBookPacketC2S(FriendlyByteBuf buf) {
        this.targetPos = buf.readBoolean() ? buf.readBlockPos() : null;
        this.targetSlot = buf.readVarInt();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.targetPos != null);
        if (this.targetPos != null) buf.writeBlockPos(this.targetPos);
        buf.writeVarInt(this.targetSlot);
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        if (!(player.containerMenu instanceof ArcaneWorkbenchMenu menu)) return;

        if (menu.getWorkbenchPos() == null
                || player.distanceToSqr(Vec3.atCenterOf(menu.getWorkbenchPos())) > MAX_DISTANCE_SQR) {
            return;
        }

        if (menu.storeCarriedBookInNearbyBookshelf(this.targetPos, this.targetSlot)) menu.broadcastChanges();
    }
}
