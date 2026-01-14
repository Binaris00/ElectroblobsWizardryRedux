package com.binaris.wizardry.api.content.entity.construct;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class ScaledConstructEntity extends MagicConstructEntity {
    protected float sizeMultiplier = 1;
    protected EntityDimensions size = EntityDimensions.scalable(this.getBbWidth(), this.getBbHeight());

    public ScaledConstructEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.refreshDimensions();
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return this.size;
    }

    public float getSizeMultiplier() {
        return sizeMultiplier;
    }

    public void setSizeMultiplier(float sizeMultiplier) {
        this.sizeMultiplier = sizeMultiplier;
        this.size = EntityDimensions.scalable(shouldScaleWidth() ? getBbWidth() * sizeMultiplier : getBbWidth(), shouldScaleHeight() ? getBbHeight() * sizeMultiplier : getBbHeight());
    }

    public void setSize(float width, float height) {
        this.size = EntityDimensions.scalable(width, height);
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
        setSizeMultiplier(tag.getFloat("sizeMultiplier"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("sizeMultiplier", sizeMultiplier);
    }
}
