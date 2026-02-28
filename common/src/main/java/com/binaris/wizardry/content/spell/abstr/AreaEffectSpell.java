package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.LocationCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

/**
 * Base class for spells that affect entities within a specified area of effect (AoE). Handles finding targets within
 * range, calculating the effective radius based on modifiers, and applying effects to each target.
 * <p>
 * This spell can be cast by entities (targeting around themselves or a specific target) and by location (targeting
 * around a block position). The actual effect applied to each entity is defined by overriding the
 * {@link #affectEntity(CastContext, Vec3, LivingEntity, int)} method.
 * <p>
 * The area of effect radius is determined by the {@link DefaultProperties#EFFECT_RADIUS} property and can be modified
 * by blast upgrades. Targets are automatically sorted by distance from the origin point, with closer entities being
 * affected first.
 * <p>
 * The spell automatically spawns particle effects in a circular pattern around the origin point. You can customize the
 * particle density by calling {@link #particleDensity(float)} and override {@link #spawnParticle(Level, double, double, double)}
 * to define which particles to spawn.
 * <p>
 * You must override the {@link #properties()} to return an actual instance of {@link SpellProperties} for this spell or
 * use {@link Spell#assignProperties(SpellProperties)}, otherwise the spell will have no properties and may not function
 * as intended.
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class AreaEffectSpell extends Spell {
    /** Whether this spell targets allies instead of enemies. Defaults to false. */
    protected boolean targetAllies = false;
    /** Whether this spell should always succeed, even if no targets are affected. Defaults to false. */
    protected boolean alwaysSucceed = false;
    /** Density multiplier for particle spawning. Higher values spawn more particles. Defaults to 0.65. */
    protected float particleDensity = 0.65f;

    /**
     * Sets whether this spell targets allies instead of enemies.
     * <p>
     * When set to true, the spell will only affect entities that are allied with the caster.
     * When set to false (default), the spell will only affect valid enemy targets.
     *
     * @param targetAllies true to target allies, false to target enemies
     * @return this spell instance for method chaining
     */
    public AreaEffectSpell targetAllies(boolean targetAllies) {
        this.targetAllies = targetAllies;
        return this;
    }

    /**
     * Sets whether this spell should always succeed, regardless of whether any targets are affected.
     * <p>
     * When set to true, the spell will return true (success) even if no entities are found or affected.
     * When set to false (default), the spell will only succeed if at least one entity is affected.
     *
     * @param alwaysSucceed true to always succeed, false to require at least one affected entity
     * @return this spell instance for method chaining
     */
    public AreaEffectSpell alwaysSucceed(boolean alwaysSucceed) {
        this.alwaysSucceed = alwaysSucceed;
        return this;
    }

    /**
     * Sets the particle density multiplier for this spell's visual effect.
     * <p>
     * The actual particle count is calculated as {@code particleDensity * π * radius²}, so higher values
     * result in more particles being spawned. The default is 0.65.
     *
     * @param particleDensity the particle density multiplier
     * @return this spell instance for method chaining
     */
    public AreaEffectSpell particleDensity(float particleDensity) {
        this.particleDensity = particleDensity;
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
        boolean result = findAndAffectEntities(ctx, ctx.caster().position());
        if (result) this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return result;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        boolean result = findAndAffectEntities(ctx, ctx.caster().position());
        if (result) this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return result;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        boolean result = findAndAffectEntities(ctx, ctx.vec3());
        if (result) this.playSound(ctx.world(), ctx.vec3(), ctx.castingTicks(), -1);
        return result;
    }

    /**
     * Finds all entities within the effect radius and applies the spell's effect to them.
     * <p>
     * This method calculates the effective radius, searches for all living entities within that radius, filters them
     * based on the {@link #targetAllies} setting, sorts them by distance from the origin, and then
     * calls {@link #affectEntity(CastContext, Vec3, LivingEntity, int)} for each target.
     * <p>
     * On the client side, this method also spawns particle effects via {@link #spawnParticleEffect(CastContext, Vec3, double)}.
     *
     * @param ctx the cast context containing spell information and modifiers
     * @param origin the center point of the area effect
     * @return true if at least one entity was affected, or if {@link #alwaysSucceed} is true
     */
    protected boolean findAndAffectEntities(CastContext ctx, Vec3 origin) {
        double radius = this.property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(EBItems.BLAST_UPGRADE.get());

        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(radius, origin.x, origin.y, origin.z, ctx.world());

        if (targetAllies)
            targets.removeIf(target -> target != ctx.caster() && !AllyDesignation.isAllied(ctx.caster(), target));
        else targets.removeIf(target -> !AllyDesignation.isValidTarget(ctx.caster(), target));

        targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(origin.x, origin.y, origin.z)));

        boolean result = alwaysSucceed;
        int i = 0;

        for (LivingEntity target : targets) {
            if (affectEntity(ctx, origin, target, i++)) result = true;
        }

        if (ctx.world().isClientSide) spawnParticleEffect(ctx, origin, radius);
        return result;
    }

    /**
     * Applies the spell's effect to a single target entity.
     * <p>
     * This method must be implemented by subclasses to define what happens when the spell affects an entity.
     * It is called for each valid target found within the area of effect, in order of distance from the origin
     * (closest first).
     *
     * @param ctx the cast context containing spell information and modifiers
     * @param origin the center point of the area effect
     * @param target the entity to affect
     * @param targetCount the index of this target in the sorted list (0 for the closest entity, 1 for the second closest, etc.)
     * @return true if the entity was successfully affected, false otherwise
     */
    protected abstract boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount);

    /**
     * Spawns particle effects in a circular pattern around the origin point.
     * <p>
     * The number of particles is determined by {@code particleDensity * π * radius²}. Particles are spawned
     * at random positions within the radius, distributed in a circle around the origin.
     * <p>
     * This method calls {@link #spawnParticle(Level, double, double, double)} for each particle position.
     * Override {@code spawnParticle} to define which particles to spawn.
     *
     * @param ctx the cast context containing spell information
     * @param origin the center point of the area effect
     * @param radius the radius of the area effect
     */
    protected void spawnParticleEffect(CastContext ctx, Vec3 origin, double radius) {
        int particleCount = (int) Math.round(particleDensity * Math.PI * radius * radius);

        for (int i = 0; i < particleCount; i++) {
            double r = (1 + ctx.world().random.nextDouble() * (radius - 1));
            float angle = ctx.world().random.nextFloat() * (float) Math.PI * 2f;

            spawnParticle(ctx.world(), origin.x + r * Mth.cos(angle), origin.y, origin.z + r * Mth.sin(angle));
        }
    }

    /**
     * Spawns a single particle at the specified position.
     * <p>
     * Override this method in subclasses to define which particle type to spawn and with what parameters. This method
     * is called multiple times by {@link #spawnParticleEffect(CastContext, Vec3, double)} to create the full particle
     * effect.
     * <p>
     *
     * @param world the world to spawn the particle in
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    protected void spawnParticle(Level world, double x, double y, double z) {
    }
}
