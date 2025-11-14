package com.electroblob.wizardry.api.content.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class BombEntity extends MagicProjectileEntity {
    /**
     * The entity blast multiplier. This is now synced and saved centrally from {@link BombEntity}.
     */
    public float blastMultiplier = 1.0f;

    public BombEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public BombEntity(EntityType<? extends ThrowableItemProjectile> entityType, LivingEntity livingEntity, Level level) {
        super(entityType, livingEntity, level);
    }


    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        blastMultiplier = tag.getFloat("blastMultiplier");
    }

    @Override
    public boolean save(CompoundTag tag) {
        tag.putFloat("blastMultiplier", blastMultiplier);
        return super.save(tag);
    }
}
