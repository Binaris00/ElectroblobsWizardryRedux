package com.binaris.wizardry.content.blockentity;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.content.block.BookShelfBlock;
import com.binaris.wizardry.content.menu.BookshelfMenu;
import com.binaris.wizardry.setup.registries.EBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BookshelfBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> inventory;

    public BookshelfBlockEntity(BlockPos pos, BlockState state) {
        super(EBBlockEntities.BOOKSHELF.get(), pos, state);
        inventory = NonNullList.withSize(BookShelfBlock.SLOT_COUNT, ItemStack.EMPTY);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container." + WizardryMainMod.MOD_ID + ".bookshelf");
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> stacks) {
        this.inventory = stacks;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory) {
        return new BookshelfMenu(i, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        super.setItem(slot, stack);
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64;
    }

    @Override
    public boolean canPlaceItem(int slotNumber, ItemStack stack) {
        return stack.isEmpty() || BookshelfMenu.isBook(stack);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (inventory == null) this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.inventory);
        }
        if (tag.contains("CustomName", Tag.TAG_STRING))
            this.setCustomName(Component.literal(tag.getString("CustomName")));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.inventory);
        }
        if (this.hasCustomName()) tag.putString("CustomName", this.getCustomName().getString());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
