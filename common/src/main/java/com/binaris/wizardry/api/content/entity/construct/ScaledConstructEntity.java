package com.binaris.wizardry.api.content.entity.construct;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class ScaledConstructEntity extends MagicConstructEntity {
    private static final EntityDataAccessor<Float> SIZE_MULTIPLIER = SynchedEntityData.defineId(ScaledConstructEntity.class, EntityDataSerializers.FLOAT);
    protected float sizeMultiplier = 1;
    protected float baseWidth;
    protected float baseHeight;

    public ScaledConstructEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.baseWidth = type.getDimensions().width;
        this.baseHeight = type.getDimensions().height;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIZE_MULTIPLIER, 1.0f);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.scalable(shouldScaleWidth() ? baseWidth * sizeMultiplier : baseWidth, shouldScaleHeight() ? baseHeight * sizeMultiplier : baseHeight);
    }

    public float getSizeMultiplier() {
        return sizeMultiplier;
    }

    public void setSizeMultiplier(float sizeMultiplier) {
        this.sizeMultiplier = sizeMultiplier;
        this.entityData.set(SIZE_MULTIPLIER, sizeMultiplier);
        this.refreshDimensions();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (SIZE_MULTIPLIER.equals(key)) {
            this.sizeMultiplier = this.entityData.get(SIZE_MULTIPLIER);
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    public void setBaseSize(float width, float height) {
        this.baseWidth = width;
        this.baseHeight = height;
        this.refreshDimensions();
    }

    protected boolean shouldScaleWidth() {
        return true;
    }

    protected boolean shouldScaleHeight() {
        return true;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("sizeMultiplier")) {
            this.sizeMultiplier = tag.getFloat("sizeMultiplier");
            this.entityData.set(SIZE_MULTIPLIER, this.sizeMultiplier);
        }
        if (tag.contains("baseWidth")) this.baseWidth = tag.getFloat("baseWidth");
        if (tag.contains("baseHeight")) this.baseHeight = tag.getFloat("baseHeight");
        this.refreshDimensions();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("sizeMultiplier", sizeMultiplier);
        tag.putFloat("baseWidth", baseWidth);
        tag.putFloat("baseHeight", baseHeight);
    }
}
