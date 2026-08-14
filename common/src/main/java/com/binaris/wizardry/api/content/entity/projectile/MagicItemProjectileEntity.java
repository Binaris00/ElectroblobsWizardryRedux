package com.binaris.wizardry.api.content.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/// Based on [net.minecraft.world.entity.projectile.ThrowableItemProjectile] but using [MagicProjectileEntity] as a base.
/// You use this for projectiles that are items.
public abstract class MagicItemProjectileEntity extends MagicProjectileEntity implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(MagicItemProjectileEntity.class, EntityDataSerializers.ITEM_STACK);

    public MagicItemProjectileEntity(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public MagicItemProjectileEntity(EntityType<? extends ThrowableProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level);
    }

    public MagicItemProjectileEntity(EntityType<? extends ThrowableProjectile> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level);
    }

    public void setItem(ItemStack stack) {
        if (!stack.is(this.getDefaultItem()) || stack.hasTag()) {
            this.getEntityData().set(DATA_ITEM_STACK, stack.copyWithCount(1));
        }

    }

    /// Returns the default item for this projectile.
    protected abstract Item getDefaultItem();

    protected ItemStack getItemRaw() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        ItemStack itemstack = this.getItemRaw();
        if (!itemstack.isEmpty()) {
            compound.put("Item", itemstack.save(new CompoundTag()));
        }

    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        ItemStack itemstack = ItemStack.of(compound.getCompound("Item"));
        this.setItem(itemstack);
    }
}
