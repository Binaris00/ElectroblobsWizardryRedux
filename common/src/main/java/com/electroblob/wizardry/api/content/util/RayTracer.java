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
import java.util.function.Predicate;

/**
 * Utility class for performing ray trace operations in the world, including both block and entity detection.
 * Provides methods for tracing rays with aim assist and custom entity filtering.
 */
public final class RayTracer {

    /**
     * Performs a ray trace from origin to endpoint, detecting both blocks and entities.
     *
     * @param world      The world to perform the ray trace in.
     * @param caster     The entity performing the ray trace.
     * @param origin     The starting position of the ray.
     * @param endpoint   The ending position of the ray.
     * @param aimAssist  Additional size to inflate entity hitboxes for easier targeting.
     * @param hitLiquids Whether the ray should collide with liquid blocks.
     * @param entityType The class of entities to detect.
     * @param filter     A predicate to filter out entities from the results.
     * @return The hit result, or null if nothing was hit.
     */
    @Nullable
    public static HitResult rayTrace(Level world, Entity caster, Vec3 origin, Vec3 endpoint, float aimAssist, boolean hitLiquids, Class<? extends Entity> entityType, Predicate<? super Entity> filter) {
        ClipContext.Fluid fluidMode = hitLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
        HitResult blockHit = world.clip(new ClipContext(origin, endpoint, ClipContext.Block.COLLIDER, fluidMode, caster));

        Vec3 traceEnd = blockHit.getLocation();
        float searchRadius = 1 + aimAssist;
        AABB searchVolume = new AABB(origin.x, origin.y, origin.z, traceEnd.x, traceEnd.y, traceEnd.z).inflate(searchRadius, searchRadius, searchRadius);

        List<? extends Entity> entities = world.getEntitiesOfClass(entityType, searchVolume);
        entities.removeIf(filter);

        Entity closestEntity = null;
        double closestDistance = origin.distanceTo(traceEnd);

        for (Entity entity : entities) {
            Vec3 intercept = calculateIntercept(entity, origin, traceEnd, aimAssist);
            if (intercept == null) continue;

            double distance = origin.distanceTo(intercept);
            if (distance < closestDistance) {
                closestEntity = entity;
                closestDistance = distance;
            }
        }

        return closestEntity != null ? new EntityHitResult(closestEntity) : blockHit;
    }

    /**
     * Calculates the intercept point between a ray and an entity's hitbox.
     *
     * @param entity    The entity to check collision with.
     * @param origin    The ray's starting position.
     * @param endpoint  The ray's ending position.
     * @param aimAssist Additional inflation for living entities' hitboxes.
     * @return The intercept point, or null if no collision occurs.
     */
    @Nullable
    private static Vec3 calculateIntercept(Entity entity, Vec3 origin, Vec3 endpoint, float aimAssist) {
        if (entity instanceof ICustomHitbox customHitbox) {
            float fuzziness = EntityUtil.isLiving(entity) ? aimAssist : 0;
            return customHitbox.calculateIntercept(origin, endpoint, fuzziness);
        }

        AABB bounds = entity.getBoundingBox();
        float pickRadius = entity.getPickRadius();
        if (pickRadius != 0) bounds = bounds.inflate(pickRadius);

        if (EntityUtil.isLiving(entity) && aimAssist != 0) {
            bounds = bounds.inflate(aimAssist);
        }

        return bounds.clip(origin, endpoint).orElse(null);
    }

    /**
     * Performs a standard block ray trace from the entity's eye position in the direction they are looking.
     *
     * @param world                  The world to perform the ray trace in.
     * @param entity                 The entity performing the ray trace.
     * @param range                  The maximum distance to trace.
     * @param hitLiquids             Whether the ray should collide with liquid blocks.
     * @param ignoreUncollidables    Unused parameter (kept for API compatibility).
     * @param returnLastUncollidable Unused parameter (kept for API compatibility).
     * @return The hit result, or null if nothing was hit.
     */
    @Nullable
    public static HitResult standardBlockRayTrace(Level world, LivingEntity entity, double range, boolean hitLiquids, boolean ignoreUncollidables, boolean returnLastUncollidable) {
        Vec3 origin = entity.getEyePosition(1);
        Vec3 endpoint = origin.add(entity.getLookAngle().scale(range));
        ClipContext.Fluid fluidMode = hitLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
        return world.clip(new ClipContext(origin, endpoint, ClipContext.Block.COLLIDER, fluidMode, entity));
    }

    /**
     * Creates a predicate that filters out the specified entity and dead living entities.
     *
     * @param entity The entity to ignore, or null to only filter dead entities.
     * @return A predicate for filtering entities.
     */
    public static Predicate<Entity> ignoreEntityFilter(@Nullable Entity entity) {
        return e -> e == entity || (e instanceof LivingEntity living && living.deathTime > 0);
    }
}
