package com.electroblob.wizardry.core;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

// TODO ALLY DESIGNATION SYSTEM
public class AllyDesignationSystem {
    public static boolean isAllied(LivingEntity caster, LivingEntity entity) {
        return false;
    }

    public static boolean isValidTarget(Entity caster, Entity entity) {
        if(caster instanceof LivingEntity casterEntity && entity instanceof LivingEntity livingEntity){
            return isValidTarget(casterEntity, livingEntity);
        }
        return false;
    }

    public static boolean isValidTarget(LivingEntity caster, LivingEntity entity) {
        return true;
    }
}
