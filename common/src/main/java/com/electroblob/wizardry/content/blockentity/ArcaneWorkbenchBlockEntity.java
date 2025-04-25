package com.electroblob.wizardry.content.blockentity;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.item.IWorkbenchItem;
import com.electroblob.wizardry.api.content.util.WandHelper;
import com.electroblob.wizardry.content.item.CrystalItem;
import com.electroblob.wizardry.content.item.SpellBookItem;
import com.electroblob.wizardry.content.menu.ArcaneWorkbenchMenu;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.setup.registries.EBBlockEntities;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ArcaneWorkbenchBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> inventory;
    public float timer = 0;
    private boolean doNotSync;

    public ArcaneWorkbenchBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EBBlockEntities.ARCANE_WORKBENCH.get(), blockPos, blockState);
        inventory = NonNullList.withSize(ArcaneWorkbenchMenu.UPGRADE_SLOT + 1, ItemStack.EMPTY);
        this.doNotSync = true;
    }

    // ===============================
    // Sync and tickers
    // ===============================

    public void sync() {
        this.level.setBlocksDirty(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition));
        //if (!doNotSync)
            //this.level.setBlocksDirty(this.worldPosition, this.level.getChunkAt(worldPosition), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3, 512);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ArcaneWorkbenchBlockEntity entity) {
        entity.doNotSync = false;

        ItemStack stack = entity.getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);

        setChanged(level, pos, state);
        if (stack.getItem() instanceof IManaStoringItem manaItem && !level.isClientSide && !manaItem.isManaFull(stack) && level.getGameTime() % EBConfig.CONDENSER_TICK_INTERVAL == 0) {
            manaItem.rechargeMana(stack, WandHelper.getUpgradeLevel(stack, EBItems.CONDENSER_UPGRADE));
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, ArcaneWorkbenchBlockEntity entity) {
        if (level.isClientSide) {
            entity.timer++;
        }
    }

    // ===============================
    // Inventory stuff
    // ===============================

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return inventory.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(this.inventory, slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.inventory, slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        ItemStack previous = inventory.set(slot, stack);

        if (slot == ArcaneWorkbenchMenu.CENTRE_SLOT && previous.isEmpty() != stack.isEmpty()) this.sync();

        if (!stack.isEmpty() && stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) < 64;
    }

    @Override
    public boolean canPlaceItem(int slotNumber, @NotNull ItemStack itemstack) {
        if (itemstack == ItemStack.EMPTY) return true;

        this.setChanged();
        if (slotNumber >= 0 && slotNumber < ArcaneWorkbenchMenu.CRYSTAL_SLOT) {
            if (!(itemstack.getItem() instanceof SpellBookItem)) return false;

            ItemStack centreStack = getItem(ArcaneWorkbenchMenu.CENTRE_SLOT);

            if (centreStack.getItem() instanceof IWorkbenchItem workbenchItem) {
                int spellSlots = workbenchItem.getSpellSlotCount(centreStack);
                return slotNumber < spellSlots;
            }

            return false;

        } else if (slotNumber == ArcaneWorkbenchMenu.CRYSTAL_SLOT) {
            return itemstack.getItem() instanceof CrystalItem;
        } else if (slotNumber == ArcaneWorkbenchMenu.CENTRE_SLOT) {
            return itemstack.getItem() instanceof IWorkbenchItem;
        } else if (slotNumber == ArcaneWorkbenchMenu.UPGRADE_SLOT) {
            Set<DeferredObject<Item>> upgrades = new HashSet<>(WandHelper.getSpecialUpgrades());
            upgrades.add(EBItems.ARCANE_TOME);
            upgrades.add(EBItems.RESPLENDENT_THREAD);
            upgrades.add(EBItems.CRYSTAL_SILVER_PLATING);
            upgrades.add(EBItems.ETHEREAL_CRYSTAL_WEAVE);
            return upgrades.contains(new DeferredObject<>(itemstack::getItem));
        }

        return true;
    }

    // ===============================
    // NBT
    // ===============================
    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.inventory);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inventory);
    }

    // ===============================

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return new ArcaneWorkbenchMenu(containerId, inventory, this);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
        this.setChanged();
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
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.%s.arcane_workbench".formatted(WizardryMainMod.MOD_ID));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }
}
