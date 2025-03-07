package com.electroblob.wizardry.api.common.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public final class EntityUtil {
    private EntityUtil() {}

    @Nullable
    public static Entity getEntityByUUID(Level world, @Nullable UUID id){
        if(id == null) return null; // It would return null eventually, but there's no point even looking

        if(world instanceof ServerLevel serverWorld){
            for (Entity entity : serverWorld.getAllEntities()) {
                if(entity.getUUID().equals(id)) return entity;
            }
        }
        return null;
    }

    public static List<LivingEntity> getLivingEntitiesInRange(Level world, double x, double y, double z, double range) {
        return getEntitiesInRange(world, x, y, z, range, LivingEntity.class);
    }

    public static List<LivingEntity> getLivingWithinRadius(double radius, double x, double y, double z, Level world) {
        return getEntitiesWithinRadius(radius, x, y, z, world, LivingEntity.class);
    }

    public static <T extends Entity> List<T> getEntitiesInRange(Level world, double x, double y, double z, double range, Class<T> entityClass){
        AABB boundingBox = new AABB(x - range, y - range, z - range, x + range, y + range, z + range);
        Predicate<T> alwaysTrue = entity -> true;

        List<T> entities = world.getEntitiesOfClass(entityClass, boundingBox, alwaysTrue);
        entities.removeIf(livingEntity -> livingEntity.distanceToSqr(x, y, z) > range);
        return entities;
    }

    public static <T extends Entity> List<T> getEntitiesWithinRadius(double radius, double x, double y, double z, Level world, Class<T> entityType) {
        AABB box = new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        List<T> entityList = world.getEntitiesOfClass(entityType, box);
        for (int i = 0; i < entityList.size(); i++) {
            if (entityList.get(i).distanceToSqr(x, y, z) > radius) {
                entityList.remove(i);
                break;
            }
        }
        return entityList;
    }

    public static boolean isLiving(Entity entity){
        return entity instanceof LivingEntity && !(entity instanceof ArmorStand);
    }

    public static boolean attackEntityWithoutKnockback(Entity entity, DamageSource source, float amount) {
        Vec3 originalVec = entity.getDeltaMovement();
        boolean succeeded = entity.hurt(source, amount);
        entity.setDeltaMovement(originalVec);
        return succeeded;
    }

    public static boolean canDamageBlocks(@Nullable Entity entity, Level world) {
//        if (entity == null) return WizardryConfig.dispenserBlockDamage;
//        else if (entity instanceof PlayerEntity) return ((PlayerEntity) entity).canModifyBlocks() && WizardryConfig.playerBlockDamage;
//        // TODO: Forge Event Factory
//        //return ForgeEventFactory.getMobGriefingEvent(world, entity);
        return true;
    }
}
