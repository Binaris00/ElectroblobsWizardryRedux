package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.LocationCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

/**
 * Extension of {@link ConstructSpell} that spawns constructs at a distance from the caster using ray casting.
 * <p>
 * This spell traces a ray from the caster's position (or from a block face when cast by location) to find the
 * target spawn position. The range of the ray is determined by the {@link DefaultProperties#RANGE} property and
 * can be modified by range upgrades.
 * <p>
 * When cast by a player, the construct is spawned at the point where the player's look direction intersects with
 * a block surface (or at maximum range if no block is hit and {@link #requiresFloor} is false). When cast by an entity,
 * the construct is spawned at the target entity's position if it is within range and not obstructed by blocks. When cast
 * by location, the construct is spawned along the direction specified by the block face, at the point where the ray
 * intersects with a block surface (or at maximum range if no block is hit).
 * <p>
 * You can configure whether the ray casting should detect liquid blocks by calling {@link #hitLiquids(boolean)},
 * and whether it should ignore uncollidable blocks by calling {@link #ignoreUncollidables(boolean)}.
 * <p>
 * You must override the {@link #properties()} to return an actual instance of {@link SpellProperties}
 * for this spell or use {@link Spell#assignProperties(SpellProperties)},
 * otherwise the spell will have no properties and may not function as intended.
 *
 * @param <T> The type of {@link MagicConstructEntity} this spell creates.
 */
public class ConstructRangedSpell<T extends MagicConstructEntity> extends ConstructSpell<T> {
    /** Whether ray casting should detect liquid blocks as solid surfaces. Defaults to false. */
    protected boolean hitLiquids = false;
    /** Whether ray casting should ignore uncollidable blocks (e.g., tall grass, flowers). Defaults to false. */
    protected boolean ignoreUncollidables = false;

    /**
     * Creates a new ranged construct spell with the specified factory and permanence setting.
     *
     * @param constructFactory A function that creates the construct entity given a world
     * @param permanent        Whether the construct should be permanent (true) or temporary (false)
     */
    public ConstructRangedSpell(Function<Level, T> constructFactory, boolean permanent) {
        super(constructFactory, permanent);
    }

    /**
     * Sets whether ray casting should detect liquid blocks as solid surfaces.
     * <p>
     * When set to true, the ray cast will stop when it hits water, lava, or other liquid blocks, and the
     * construct will be spawned at that position.
     * <p>
     * When set to false (default), liquids are treated as air and the ray cast will pass through them.
     *
     * @param hitLiquids true to detect liquids, false to ignore them
     * @return this spell instance for method chaining
     */
    public Spell hitLiquids(boolean hitLiquids) {
        this.hitLiquids = hitLiquids;
        return this;
    }

    /**
     * Sets whether ray casting should ignore uncollidable blocks (e.g., tall grass, flowers, vines).
     * <p>
     * When set to true, the ray cast will pass through blocks that don't have collision (like plants and
     * decorative blocks), only stopping when it hits a solid block.
     * <p>
     * When set to false (default), the ray cast will stop at any block, including decorative ones.
     *
     * @param ignoreUncollidables true to ignore uncollidable blocks, false to detect them
     * @return this spell instance for method chaining
     */
    public Spell ignoreUncollidables(boolean ignoreUncollidables) {
        this.ignoreUncollidables = ignoreUncollidables;
        return this;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        double range = property(DefaultProperties.RANGE) * ctx.modifiers().get(SpellModifiers.RANGE);
        HitResult rayTrace = RayTracer.standardBlockRayTrace(ctx.world(), ctx.caster(), range, hitLiquids, ignoreUncollidables, false);

        if (rayTrace instanceof BlockHitResult blockTrace) {
            Direction direction = blockTrace.getDirection();
            if (requiresFloor && !ctx.caster().onGround()) return false;

            if (!ctx.world().isClientSide && (direction == Direction.UP || !requiresFloor)) {
                if (!spawnConstruct(ctx, blockTrace.getLocation(), direction)) return false;
            } else {
                return false;
            }
        } else if (!requiresFloor && !ctx.world().isClientSide) {
            Vec3 look = ctx.caster().getLookAngle();
            Vec3 origin = ctx.caster().position().add(0, ctx.caster().getEyeHeight(), 0);
            Vec3 target = origin.add(look.scale(range));

            if (!spawnConstruct(ctx, target, null)) return false;
        } else {
            return false;
        }

        playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        double range = property(DefaultProperties.RANGE) * ctx.modifiers().get(SpellModifiers.RANGE);
        if (ctx.target() == null) return false;
        if (ctx.caster().distanceTo(ctx.target()) >= range || ctx.world().isClientSide) return false;

        Vec3 origin = ctx.caster().getEyePosition(1);
        HitResult hit = ctx.world().clip(new ClipContext(origin, ctx.target().position(),
                ClipContext.Block.COLLIDER, hitLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, ctx.target()));

        if (hit instanceof BlockHitResult blockHit && !blockHit.getBlockPos().equals(ctx.caster().blockPosition())) {
            return false;
        }

        Direction side = null;
        int y = (int) ctx.target().getY();

        if (!ctx.target().onGround() && requiresFloor) {
            Integer floor = BlockUtil.getNearestFloor(ctx.world(), ctx.target().blockPosition(), 3);
            if (floor == null) return false;
            side = Direction.UP;
            y = floor;
        }

        if (!spawnConstruct(ctx, new Vec3(ctx.target().getX(), y, ctx.target().getZ()), side)) return false;

        ctx.caster().swing(ctx.hand());
        playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        double range = property(DefaultProperties.RANGE) * ctx.modifiers().get(SpellModifiers.RANGE);
        Vec3 endpoint = ctx.vec3().add(Vec3.atLowerCornerOf(ctx.direction().getNormal()).scale(range));
        HitResult rayTrace = ctx.world().clip(new ClipContext(ctx.vec3(), endpoint,
                ClipContext.Block.COLLIDER, hitLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, null));

        if (rayTrace instanceof BlockHitResult blockHit) {
            Direction direction = blockHit.getDirection();
            if (direction == Direction.UP || !requiresFloor) {
                if (!ctx.world().isClientSide) {
                    if (!spawnConstruct(ctx, blockHit.getLocation(), direction)) return false;
                }
            } else {
                return false;
            }
        } else if (!requiresFloor && !ctx.world().isClientSide) {
            if (!spawnConstruct(ctx, endpoint, null)) return false;
        } else {
            return false;
        }

        this.playSound(ctx.world(), ctx.x() - ctx.direction().getStepX(),
                ctx.y() - ctx.direction().getStepY(), ctx.z() - ctx.direction().getStepZ(),
                ctx.castingTicks(), ctx.duration());
        return true;
    }
}
