package com.electroblob.wizardry.content;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class EBLivingTick {
    LivingEntity entity;
    Level level;

    private EBLivingTick(LivingEntity entity, Level level) {
        this.entity = entity;
        this.level = level;
    }

    public static EBLivingTick create(LivingEntity entity, Level level) {
        return new EBLivingTick(entity, level);
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Level getLevel() {
        return level;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
