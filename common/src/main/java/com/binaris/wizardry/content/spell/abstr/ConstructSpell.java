package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.binaris.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.*;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Base class for spells that spawn magical construct entities ({@link MagicConstructEntity}). Handles the creation,
 * positioning, and spawning of constructs with support for lifetime management, scaling, and damage modifiers.
 * <p>
 * This spell can be cast by entities (spawning the construct at the caster's position) and by location (spawning the
 * construct at a specific block position). Constructs can be either permanent (lifetime = -1) or temporary with a
 * duration determined by the {@link DefaultProperties#DURATION} property.
 * <p>
 * The construct's position can be constrained to require a floor surface by calling {@link #floor(boolean)}, and
 * multiple constructs can be prevented from overlapping by calling {@link #overlap(boolean)}.
 * <p>
 * If the construct implements {@link ScaledConstructEntity}, its size will automatically scale based on blast upgrade
 * modifiers. The construct's damage is also automatically modified by the potency modifier.
 * <p>
 * You must override the {@link #properties()} to return an actual instance of {@link SpellProperties} for this spell or
 * use {@link Spell#assignProperties(SpellProperties)}, otherwise the spell will have no properties and may not function
 * as intended.
 *
 * @param <T> The type of {@link MagicConstructEntity} this spell creates.
 */
public class ConstructSpell<T extends MagicConstructEntity> extends Spell {
    /** Factory function to create instances of the construct entity. */
    protected final Function<Level, T> constructFactory;
    /** Whether this construct is permanent (true) or has a limited lifetime (false). */
    protected final boolean permanent;
    /** Whether the construct requires a floor surface to spawn on. Defaults to false. */
    protected boolean requiresFloor = false;
    /** Whether multiple constructs of the same type can overlap at the same position. Defaults to false. */
    protected boolean allowOverlap = false;

    /**
     * Creates a new construct spell with the specified factory and permanence setting.
     *
     * @param constructFactory A function that creates the construct entity given a world
     * @param permanent        Whether the construct should be permanent (true) or temporary (false)
     */
    public ConstructSpell(Function<Level, T> constructFactory, boolean permanent) {
        this.constructFactory = constructFactory;
        this.permanent = permanent;
    }

    /**
     * Sets whether this spell requires a floor surface to spawn the construct on.
     * <p>
     * When set to true, the spell will search for the nearest floor surface below the target position
     * (up to 1 block away for entities, configured distance for locations). If no floor is found, the spell
     * will fail to cast.
     * <p>
     * When set to false (default), the construct can be spawned in midair.
     *
     * @param requiresFloor true to require a floor surface, false to allow midair spawning
     * @return this spell instance for method chaining
     */
    public ConstructSpell<T> floor(boolean requiresFloor) {
        this.requiresFloor = requiresFloor;
        return this;
    }

    /**
     * Sets whether multiple constructs of the same type can overlap at the same position.
     * <p>
     * When set to false (default), the spell will check if another construct of the same type already
     * exists at the target position before spawning. If one exists, the spell will fail to cast.
     * <p>
     * When set to true, multiple constructs can be spawned at the same location.
     *
     * @param allowOverlap true to allow overlapping constructs, false to prevent them
     * @return this spell instance for method chaining
     */
    public ConstructSpell<T> overlap(boolean allowOverlap) {
        this.allowOverlap = allowOverlap;
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
        if (ctx.caster().onGround() || !requiresFloor) {
            if (!spawnConstruct(ctx, ctx.caster().position(), ctx.caster().onGround() ? Direction.UP : null))
                return false;
            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return true;
        }
        return false;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (ctx.target() != null && (ctx.caster().onGround() || !requiresFloor)) {
            if (!spawnConstruct(ctx, ctx.caster().position(), ctx.caster().onGround() ? Direction.UP : null))
                return false;
            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
            return true;
        }

        return false;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        Integer floor = ctx.pos().getY();

        if (requiresFloor) {
            floor = BlockUtil.getNearestFloor(ctx.world(), ctx.pos(), 1);
            ctx.direction(Direction.UP);
        }

        if (floor != null) {
            if (!spawnConstruct(ctx, new Vec3(ctx.x(), floor, ctx.z()), ctx.direction())) return false;
            this.playSound(ctx.world(), ctx.x() - ctx.direction().getStepX(),
                    ctx.y() - ctx.direction().getStepY(), ctx.z() - ctx.direction().getStepZ(),
                    ctx.castingTicks(), ctx.duration());
            return true;
        }

        return false;
    }

    /**
     * Spawns the construct entity in the world at the specified position.
     * <p>
     * This method creates the construct entity using the factory, sets its position and caster, configures its
     * lifetime, applies damage and size multipliers, and adds it to the world.
     * <p>
     * If {@link #allowOverlap} is false, this method checks for existing constructs of the same type at the
     * target position and prevents spawning if one exists.
     * <p>
     * This method also calls {@link #addConstructExtras(CastContext, MagicConstructEntity, Direction)} to allow
     * subclasses to apply additional modifications to the construct before spawning.
     *
     * @param ctx  the cast context containing spell information and modifiers
     * @param vec3 the position where the construct should be spawned
     * @param side the direction the construct is facing, or null if not applicable
     * @return true if the construct was successfully spawned, false if spawning was prevented (e.g., due to overlap)
     */
    protected boolean spawnConstruct(CastContext ctx, Vec3 vec3, @Nullable Direction side) {
        if (!ctx.world().isClientSide) {
            T construct = constructFactory.apply(ctx.world());

            construct.setPos(vec3);
            if (ctx.caster() != null) construct.setCaster(ctx.caster());

            construct.lifetime = permanent ? -1 : (int) (property(DefaultProperties.DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            construct.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
            if (construct instanceof ScaledConstructEntity scaledConstruct)
                scaledConstruct.setSizeMultiplier(ctx.modifiers().get(SpellModifiers.BLAST));
            addConstructExtras(ctx, construct, side);

            if (!allowOverlap && !ctx.world().getEntitiesOfClass(construct.getClass(), construct.getBoundingBox()).isEmpty())
                return false;

            ctx.world().addFreshEntity(construct);
        }

        return true;
    }

    /**
     * Applies additional modifications to the construct after it has been created and configured, but before it is
     * added to the world.
     * <p>
     * Override this method in subclasses to apply spell-specific behavior, additional attributes, or visual effects
     * to the construct.
     *
     * @param ctx       the cast context containing spell information and modifiers
     * @param construct construct entity that has been created and configured
     * @param side      direction the construct is facing, or null if not applicable
     */
    protected void addConstructExtras(CastContext ctx, T construct, Direction side) {
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
