package com.electroblob.wizardry.content.menu.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class VirtualSlot extends Slot {
    private final BlockEntity tileEntity;
    private ItemStack prevStack;

    public VirtualSlot(Container inventory, int index) {
        super(inventory, index, -999, -999);
        if (!(inventory instanceof BlockEntity)) throw new IllegalArgumentException("Inventory must be a tile entity!");
        this.tileEntity = (BlockEntity) inventory;
        this.prevStack = getItem().copy();
    }

    @Override
    public boolean isActive() {
        return false;
    }

    public boolean isValid() {
        return !tileEntity.isRemoved();
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return isValid() && container.canPlaceItem(index, stack);
    }

    @Override
    public boolean mayPickup(@NotNull Player p_40228_) {
        return isValid() && super.mayPickup(p_40228_);
    }

    @Override
    public void onTake(@NotNull Player p_150645_, @NotNull ItemStack p_150646_) {
        if (isValid()) {
            super.onTake(p_150645_, p_150646_);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.hasItem()) this.prevStack = this.getItem().copy();
    }

    @Override
    public ItemStack getItem() {
        return isValid() ? super.getItem() : ItemStack.EMPTY;
    }

    public ItemStack getPrevStack() {
        return prevStack;
    }

    @Override
    public void set(ItemStack p_40240_) {
        if (isValid()) {
            //TODO
            //if(container instanceof TileEntityBookshelf) ((TileEntityBookshelf)container).sync();
            super.set(p_40240_);
        }
    }

    @Override
    public ItemStack remove(int amount) {
        //TODO
        //if(isValid() && container instanceof TileEntityBookshelf) ((TileEntityBookshelf)container).sync();
        return isValid() ? super.remove(amount) : ItemStack.EMPTY;
    }
}
