package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.*;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.fire.FlameRay;
import com.binaris.wizardry.content.spell.ice.FrostRay;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Base class for spells that cast a ray from the caster's position or a location, affecting entities and blocks along
 * the ray's path. The ray is traced using ray casting, and can be configured to detect liquids, ignore uncollidable blocks,
 * and pass through entities.
 * <p>
 * The ray is traced from the caster's eye position (or from a block face when cast by location) in the direction the
 * caster is looking (or in the direction of the block face).
 * <p>
 * Check {@link FrostRay FrostRay} and {@link FlameRay FlameRay} for examples of ray spells.
 * <p>
 * You must override the {@link #properties()} to return an actual instance of {@link SpellProperties} for this spell or
 * use {@link Spell#assignProperties(SpellProperties)}, otherwise the spell will have no properties and may not function
 * as intended.
 */

@SuppressWarnings("UnusedReturnValue")
public abstract class RaySpell extends Spell {
    /** The vertical offset from the caster's eye position to the actual origin of the ray. */
    protected static final double Y_OFFSET = 0.25;
    /** The spacing between particles spawned along the ray.*/
    protected double particleSpacing = 0.85;
    /** The amount of random jitter applied to the position of each particle spawned along the ray, creating a more natural effect. */
    protected double particleJitter = 0.1;
    /** The velocity of the particles spawned along the ray*/
    protected double particleVelocity = 0;
    /** Whether the ray should ignore living entities when ray tracing, allowing it to pass through them. */
    protected boolean ignoreLivingEntities = false;
    /** Whether the ray should treat liquid blocks (like water and lava) as solid and stop when it hits them. */
    protected boolean hitLiquids = false;
    /** Whether the ray should ignore uncollidable blocks (like tall grass, flowers, etc.) when ray tracing. */
    protected boolean ignoreUncollidables = true;
    /** Whether the ray should have aim assist to slightly adjust towards nearby targets. */
    protected float aimAssist = 0;

    /**
     * Sets the spacing between particles spawned along the ray. The particles will be spawned at regular intervals along
     * the ray, with the distance between each particle determined by this value. A smaller value will result in more
     * particles being spawned and a denser particle effect, while a larger value will result in fewer particles and a
     * sparser effect. The default value is 0.85, which means that particles will be spawned approximately every 0.85
     * blocks along the ray.
     *
     * @param particleSpacing The distance between particles along the ray.
     * @return This spell instance for method chaining.
     */
    public Spell particleSpacing(double particleSpacing) {
        this.particleSpacing = particleSpacing;
        return this;
    }

    /**
     * Sets the amount of random jitter applied to the position of each particle spawned along the ray. This creates a
     * more natural and less uniform appearance for the particle effects. The jitter is applied in all directions, so a
     * value of 0.1 means that each particle's position can be randomly offset by up to 0.1 blocks in any direction from
     * its ideal position along the ray.
     *
     * @param particleJitter The maximum random offset for particle positions, where 0 means no jitter and higher values
     *                       increase the randomness.
     * @return This spell instance for method chaining.
     */
    public Spell particleJitter(double particleJitter) {
        this.particleJitter = particleJitter;
        return this;
    }

    /**
     * Sets the velocity of the particles spawned along the ray. The velocity is applied in the direction of the ray, so
     * positive values will make the particles move forward along the ray, while negative values will make them move
     * backward towards the caster. A value of 0 means the particles will be stationary.
     *
     * @param particleVelocity The velocity of the particles along the ray direction.
     * @return This spell instance for method chaining.
     */
    public Spell particleVelocity(double particleVelocity) {
        this.particleVelocity = particleVelocity;
        return this;
    }

    /**
     * When set to true, the ray will ignore living entities when performing ray tracing. This means that the ray will
     * pass through living entities without stopping, allowing it to hit blocks or entities behind them. When set to false,
     * the ray will treat living entities as solid and stop when it hits them.
     *
     * @param ignoreLivingEntities true to ignore living entities, false to treat them as solid
     * @return this spell instance for method chaining
     */
    public Spell ignoreLivingEntities(boolean ignoreLivingEntities) {
        this.ignoreLivingEntities = ignoreLivingEntities;
        return this;
    }

    /**
     * When set to true, the ray will treat liquid blocks (like water and lava) as solid and stop when it hits them.
     * This means that the ray will not pass through liquids and will hit the liquid block instead. When set to false,
     * liquids are treated as air and the ray will pass through them without stopping.
     *
     * @param hitLiquids true to treat liquids as solid, false to ignore them
     * @return this spell instance for method chaining
     */
    public Spell hitLiquids(boolean hitLiquids) {
        this.hitLiquids = hitLiquids;
        return this;
    }

    /**
     * When set to true, the ray will ignore uncollidable blocks such as tall grass, flowers, and other non-solid blocks.
     * This means that the ray will pass through these blocks without stopping, allowing it to hit entities or blocks
     * behind them. When set to false, the ray will treat uncollidable blocks as solid and stop when it hits them.
     *
     * @param ignoreUncollidables true to ignore uncollidable blocks, false to treat them as solid
     * @return this spell instance for method chaining
     */
    public Spell ignoreUncollidables(boolean ignoreUncollidables) {
        this.ignoreUncollidables = ignoreUncollidables;
        return this;
    }

    /**
     * Sets the aim assist for this ray spell. Aim assist helps the ray to slightly adjust its direction towards nearby
     * valid targets, making it easier to hit them.
     *
     * @param aimAssist The strength of the aim assist, where 0 means no assist and higher values increase the assist. A
     *                  value of 1 would mean the ray can adjust its direction by up to 1 block towards a target.
     * @return This spell instance for method chaining.
     */
    public Spell aimAssist(float aimAssist) {
        this.aimAssist = aimAssist;
        return this;
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    @Override
    public boolean canCastByLocation() {
        return true;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Vec3 look = ctx.caster().getLookAngle();
        Vec3 origin = new Vec3(ctx.caster().getX(), ctx.caster().getY() + ctx.caster().getEyeHeight() - Y_OFFSET, ctx.caster().getZ());

        if (this.isInstantCast() && ctx.world().isClientSide && ClientUtils.isFirstPerson(ctx.caster()))
            origin = origin.add(look.scale(1.2));
        if (!shootSpell(ctx, origin, look)) return false;

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        Vec3 origin = new Vec3(ctx.caster().getX(), ctx.caster().getY() + ctx.caster().getEyeHeight() - Y_OFFSET, ctx.caster().getZ());
        Vec3 targetPos = null;

        if (ctx.target() != null) {
            if (!ignoreLivingEntities || !(ctx.target() instanceof LivingEntity)) {
                targetPos = new Vec3(ctx.target().getX(), ctx.target().getY() + ctx.target().getBbHeight() / 2, ctx.target().getZ());

            } else {
                int x = Mth.floor(ctx.target().getX());
                int y = (int) ctx.target().getY() - 1;
                int z = Mth.floor(ctx.target().getZ());
                BlockPos pos = new BlockPos(x, y, z);

                if (!ctx.world().isEmptyBlock(pos) && (!ctx.world().getBlockState(pos).liquid() || hitLiquids)) {
                    targetPos = new Vec3(x + 0.5, y + 1, z + 0.5);
                }
            }
        }

        if (targetPos == null) return false;

        if (!shootSpell(ctx, origin, targetPos.subtract(origin).normalize())) return false;
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        Vec3 vec = new Vec3(ctx.direction().step());

        if (shootSpell(ctx, ctx.vec3(), vec)) return false;
        this.playSound(ctx.world(), ctx.x() - ctx.direction().getStepX(),
                ctx.y() - ctx.direction().getStepY(), ctx.z() - ctx.direction().getStepZ(),
                ctx.castingTicks(), ctx.duration());
        return true;
    }

    /**
     * Called when the ray hits an entity. Override this method to perform an action when the ray hits an entity, such as
     * dealing damage or applying a status effect. Return true if the spell should be considered successfully cast
     * when it hits an entity, or false if the spell should not be cast when it hits an entity (e.g. if you want to prevent
     * casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
     *
     * @param ctx       The cast context of the spell.
     * @param entityHit The result of the entity hit, containing information about the hit entity and hit position.
     * @param origin    The starting point of the ray.
     * @return true if the spell should be considered successfully cast when it hits an entity, false if the spell should
     * not be cast when it hits an entity.
     */
    protected abstract boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin);

    /**
     * Called when the ray hits a block. Override this method to perform an action when the ray hits a block, such as
     * creating an explosion or spawning particles. Return true if the spell should be considered successfully cast
     * when it hits a block, or false if the spell should not be cast when it hits a block (e.g. if you want to prevent
     * casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
     *
     * @param ctx      The cast context of the spell.
     * @param blockHit The result of the block hit, containing information about the hit position and block.
     * @param origin   The starting point of the ray.
     * @return true if the spell should be considered successfully cast when it hits a block, false if the spell should
     * not be cast when it hits a block.
     */
    protected abstract boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin);

    /**
     * Called when the ray does not hit any entities or blocks. Override this method to perform an action when the ray
     * misses, such as spawning particles at the endpoint. Return true if the spell should be considered successfully
     * cast even when it misses, or false if the spell should not be cast when it misses (e.g. if you want to prevent
     * casting when the ray is blocked by an uncollidable block and ignoreUncollidables is true).
     *
     * @param ctx       The cast context of the spell.
     * @param origin    The starting point of the ray.
     * @param direction The normalized direction vector of the ray.
     * @return true if the spell should be considered successfully cast even when it misses, false if the spell should
     * not be cast when it misses.
     */
    protected abstract boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction);

    /**
     * Performs the ray casting logic for the spell, tracing a ray from the origin in the given direction and checking for
     * hits against entities and blocks. This method handles the ray casting and hit detection, while delegating the
     * actual effects of hitting an entity or block to the {@link #onEntityHit} and {@link #onBlockHit} methods.
     *
     * @param ctx       The cast context of the spell.
     * @param origin    The starting point of the ray.
     * @param direction The normalized direction vector of the ray.
     * @return true if the spell was successfully cast (hit an entity, hit a block, or missed), false if the spell
     * should not be cast (e.g. if it was blocked by an uncollidable block and ignoreUncollidables is true).
     */
    protected boolean shootSpell(CastContext ctx, Vec3 origin, Vec3 direction) {
        double range = this.property(DefaultProperties.RANGE) * ctx.modifiers().get(SpellModifiers.RANGE);
        Vec3 endpoint = origin.add(direction.scale(range));

        HitResult rayTrace = RayTracer.rayTrace(ctx.world(), ctx.caster(), origin, endpoint, aimAssist, hitLiquids, Entity.class, ignoreLivingEntities ? EntityUtil::isLiving : RayTracer.ignoreEntityFilter(ctx.caster()));

        boolean flag = false;

        if (rayTrace instanceof EntityHitResult entityHit) {
            flag = onEntityHit(ctx, entityHit, origin);
            if (flag) range = origin.distanceTo(rayTrace.getLocation());
        } else if (rayTrace instanceof BlockHitResult blockHit) {
            flag = onBlockHit(ctx, blockHit, origin);
            range = origin.distanceTo(rayTrace.getLocation());
        }

        if (!flag && !onMiss(ctx, origin, direction)) return false;


        if (ctx.world().isClientSide) {
            spawnParticleRay(ctx, origin, direction, range);
        }

        return true;
    }

    /**
     * Spawns particles along the ray from the origin in the given direction up to the given distance. The particles are
     * spaced according to {@link #particleSpacing} and have a random jitter based on {@link #particleJitter}. The
     * velocity of the particles is determined by {@link #particleVelocity}.
     *
     * @param ctx       The cast context of the spell.
     * @param origin    The starting point of the ray.
     * @param direction The normalized direction vector of the ray.
     * @param distance  The distance to spawn particles along the ray.
     */
    protected void spawnParticleRay(CastContext ctx, Vec3 origin, Vec3 direction, double distance) {
        Vec3 velocity = direction.scale(particleVelocity);

        for (double d = particleSpacing; d <= distance; d += particleSpacing) {
            double x = origin.x + d * direction.x + particleJitter * (ctx.world().random.nextDouble() * 2 - 1);
            double y = origin.y + d * direction.y + particleJitter * (ctx.world().random.nextDouble() * 2 - 1);
            double z = origin.z + d * direction.z + particleJitter * (ctx.world().random.nextDouble() * 2 - 1);
            spawnParticle(ctx, x, y, z, velocity.x, velocity.y, velocity.z);
        }
    }

    /**
     * Spawns a particle at the given position with the given velocity. Override this method to spawn custom particles
     * for the ray.
     *
     * @param ctx The cast context of the spell.
     * @param x   The x-coordinate of the particle.
     * @param y   The y-coordinate of the particle.
     * @param z   The z-coordinate of the particle.
     * @param vx  The x-component of the particle's velocity.
     * @param vy  The y-component of the particle's velocity.
     * @param vz  The z-component of the particle's velocity.
     */
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
    }
}
