package com.binaris.wizardry.core.networking.c2s;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.block.LecternBookshelfUtils;
import com.binaris.wizardry.content.item.SpellBookItem;
import com.binaris.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.binaris.wizardry.core.networking.abst.Message;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.phys.Vec3;

/// Takes one spell book from a nearby bookshelf. A normal click puts it on the player's
/// cursor, while shift-click sends it to the arcane workbench's spell slots or the player inventory.
public class WorkbenchTakeBookPacketC2S implements Message {
    public static final ResourceLocation ID = WizardryMainMod.location("workbench_take_book");
    private static final double MAX_DISTANCE_SQR = 64.0;

    private final BlockPos shelfPos;
    private final int slot;
    private final boolean toInventory;

    public WorkbenchTakeBookPacketC2S(BlockPos shelfPos, int slot, boolean toInventory) {
        this.shelfPos = shelfPos;
        this.slot = slot;
        this.toInventory = toInventory;
    }

    public WorkbenchTakeBookPacketC2S(FriendlyByteBuf buf) {
        this.shelfPos = buf.readBlockPos();
        this.slot = buf.readVarInt();
        this.toInventory = buf.readBoolean();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.shelfPos);
        buf.writeVarInt(this.slot);
        buf.writeBoolean(this.toInventory);
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player) {
        if (!(player.containerMenu instanceof ArcaneWorkbenchMenu menu)) return;

        BlockPos workbenchPos = menu.getWorkbenchPos();
        if (workbenchPos == null) return;
        if (player.distanceToSqr(Vec3.atCenterOf(workbenchPos)) > MAX_DISTANCE_SQR) return;
        if (!LecternBookshelfUtils.findBookshelfPositions(player.level(), workbenchPos).contains(this.shelfPos)) return;

        Container shelf = LecternBookshelfUtils.getBookshelf(player.level(), this.shelfPos);
        if (shelf == null || this.slot < 0 || this.slot >= shelf.getContainerSize()) return;

        if (!(shelf.getItem(this.slot).getItem() instanceof SpellBookItem)) return;

        // A normal click picks the book up onto the cursor; shift-click sends it to the spell slots or inventory.
        if (this.toInventory) {
            menu.storeBook(shelf, this.slot);
        } else {
            menu.takeBookToCursor(shelf, this.slot);
        }

        menu.broadcastChanges();
    }
}
