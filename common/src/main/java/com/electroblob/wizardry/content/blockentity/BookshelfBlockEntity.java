package com.electroblob.wizardry.content.blockentity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.content.block.BookShelfBlock;
import com.electroblob.wizardry.content.menu.BookshelfMenu;
import com.electroblob.wizardry.setup.registries.EBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BookshelfBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> inventory;

    public BookshelfBlockEntity(BlockPos pos, BlockState state) {
        super(EBBlockEntities.BOOKSHELF.get(), pos, state);
        inventory = NonNullList.withSize(BookShelfBlock.SLOT_COUNT, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = getItem(slot);
        if (stack.isEmpty()) return stack;

        if (stack.getCount() <= amount) {
            setItem(slot, ItemStack.EMPTY);
        } else {
            stack = stack.split(amount);
            if (stack.getCount() == 0) setItem(slot, ItemStack.EMPTY);
        }
        this.setChanged();

        return stack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (!stack.isEmpty()) setItem(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        super.setItem(slot, stack);
        this.setChanged();
    }

    @Override
    public @NotNull Component getDefaultName() {
        return Component.translatable("container." + WizardryMainMod.MOD_ID + ".bookshelf");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return this.getName();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64;
    }

    @Override
    public void startOpen(@NotNull Player player) {
    }

    @Override
    public void stopOpen(@NotNull Player player) {
    }

    @Override
    public boolean canPlaceItem(int slotNumber, ItemStack stack) {
        return stack.isEmpty() || BookshelfMenu.isBook(stack);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (inventory == null) this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.inventory);
        if (tag.contains("CustomName", Tag.TAG_STRING))
            this.setCustomName(Component.literal(tag.getString("CustomName")));
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inventory);
        if (this.hasCustomName()) tag.putString("CustomName", this.getCustomName().getString());
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory) {
        return new BookshelfMenu(i, inventory, this);
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> stacks) {
        this.inventory = stacks;
    }
}
