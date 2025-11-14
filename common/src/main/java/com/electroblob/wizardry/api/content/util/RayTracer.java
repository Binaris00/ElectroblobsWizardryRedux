package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.api.content.entity.ICustomHitbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RayTracer {

    @Nullable
    public static HitResult rayTrace(Level world, Entity caster, Vec3 origin, Vec3 endpoint, float aimAssist,
                                     boolean hitLiquids, Class<? extends Entity> entityType, Predicate<? super Entity> filter) {
        HitResult result = world.clip(new ClipContext(origin, endpoint, ClipContext.Block.COLLIDER, hitLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, caster));

        endpoint = result.getLocation();
        float borderSize = 1 + aimAssist;
        AABB searchVolume = new AABB(origin.x, origin.y, origin.z, endpoint.x, endpoint.y, endpoint.z).inflate(borderSize, borderSize, borderSize);
        List<? extends Entity> entities = world.getEntitiesOfClass(entityType, searchVolume);
        entities.removeIf(filter);

        Entity closestHitEntity = null;
        Vec3 closestHitPosition = endpoint;
        AABB entityBounds;
        Vec3 intercept = null;

        for (Entity entity : entities) {
            float fuzziness = EntityUtil.isLiving(entity) ? aimAssist : 0;

            if (entity instanceof ICustomHitbox) {
                intercept = ((ICustomHitbox) entity).calculateIntercept(origin, endpoint, fuzziness);
            } else {
                entityBounds = entity.getBoundingBox();

                if (entityBounds != null) {
                    float entityBorderSize = entity.getPickRadius();
                    if (entityBorderSize != 0)
                        entityBounds = entityBounds.inflate(entityBorderSize, entityBorderSize, entityBorderSize);

                    if (fuzziness != 0) entityBounds = entityBounds.inflate(fuzziness, fuzziness, fuzziness);

                    Optional<Vec3> hit = entityBounds.clip(origin, endpoint);
                    if (hit.isPresent()) {
                        intercept = hit.get();
                    }
                }
            }

            if (intercept != null) {
                float currentHitDistance = (float) intercept.distanceTo(origin);
                float closestHitDistance = (float) closestHitPosition.distanceTo(origin);
                if (currentHitDistance < closestHitDistance) {
                    closestHitEntity = entity;
                    closestHitPosition = intercept;
                }
            }
        }

        if (closestHitEntity != null) {
            result = new EntityHitResult(closestHitEntity, closestHitPosition);
        }

        return result;
    }

    @Nullable
    public static HitResult standardBlockRayTrace(Level world, LivingEntity entity, double range, boolean hitLiquids, boolean ignoreUncollidables, boolean returnLastUncollidable) {
        Vec3 origin = entity.getEyePosition(1);
        Vec3 endpoint = origin.add(entity.getLookAngle().scale(range));
        return world.clip(new ClipContext(origin, endpoint, ClipContext.Block.COLLIDER, hitLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, entity));
    }

    public static Predicate<Entity> ignoreEntityFilter(@Nullable Entity entity) {
        return e -> e == entity || (e instanceof LivingEntity && ((LivingEntity) e).deathTime > 0);
    }
}
