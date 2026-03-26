package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.content.entity.projectile.BombEntity;
import com.binaris.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.*;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Base class for spells that shoot magical projectile entities ({@link MagicProjectileEntity}). Handles the creation,
 * positioning, and launching of projectiles with support for damage modifiers, velocity scaling, and spread patterns.
 * <p>
 * This spell can be cast by players (shooting in their look direction), entities (shooting towards a target), and
 * by location (shooting in the direction of a block face).
 * <p>
 * Check {@link Spells#POISON_BOMB} and {@link Spells#ICE_CHARGE} for examples of projectile spells.
 * <p>
 * You must override the {@link #properties()} to return an actual instance of {@link SpellProperties} for this spell or
 * use {@link Spell#assignProperties(SpellProperties)}, otherwise the spell will have no properties and may not function
 * as intended.
 *
 * @param <T> The type of {@link MagicProjectileEntity} this spell shoots.
 * @see MagicProjectileEntity
 */

public class ProjectileSpell<T extends MagicProjectileEntity> extends Spell {
    private static final float FALLBACK_VELOCITY = 1.5f;
    protected final Function<Level, T> projectileFactory;

    public ProjectileSpell(Function<Level, T> projectileFactory) {
        this.projectileFactory = projectileFactory;
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
        if (!ctx.world().isClientSide) {
            T projectile = projectileFactory.apply(ctx.world());
            projectile.aim(ctx.caster(), calculateVelocity(ctx, projectile, ctx.caster().getEyeHeight() - (float) MagicProjectileEntity.LAUNCH_Y_OFFSET));
            projectile.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
            if (projectile instanceof BombEntity bomb)
                bomb.blastMultiplier = ctx.modifiers().get(SpellModifiers.BLAST);
            addProjectileExtras(ctx, projectile);
            ctx.world().addFreshEntity(projectile);
        }

        ctx.caster().swing(ctx.hand());

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);

        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (ctx.target() == null) return false;

        if (!ctx.world().isClientSide) {
            T projectile = projectileFactory.apply(ctx.world());
            int aimingError = EntityUtil.getDefaultAimingError(ctx.world().getDifficulty());
            projectile.aim(ctx.caster(), ctx.target(), calculateVelocity(ctx, projectile, ctx.caster().getEyeHeight() - (float) MagicProjectileEntity.LAUNCH_Y_OFFSET), aimingError);
            projectile.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
            if (projectile instanceof BombEntity bomb)
                bomb.blastMultiplier = ctx.modifiers().get(SpellModifiers.BLAST);
            addProjectileExtras(ctx, projectile);
            ctx.world().addFreshEntity(projectile);
        }

        ctx.caster().swing(ctx.hand());
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        if (!ctx.world().isClientSide) {
            T projectile = projectileFactory.apply(ctx.world());
            projectile.setPos(ctx.vec3());
            Vec3i vec = ctx.direction().getNormal();
            projectile.shoot(vec.getX(), vec.getY(), vec.getZ(), calculateVelocity(ctx, projectile, 0.375f), 1);
            projectile.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
            if (projectile instanceof BombEntity bomb)
                bomb.blastMultiplier = ctx.modifiers().get(SpellModifiers.BLAST);
            addProjectileExtras(ctx, projectile);
            ctx.world().addFreshEntity(projectile);
        }

        this.playSound(ctx.world(), ctx.x() - ctx.direction().getStepX(),
                ctx.y() - ctx.direction().getStepY(), ctx.z() - ctx.direction().getStepZ(),
                ctx.castingTicks(), ctx.duration());
        return true;
    }

    /**
     * Calculates the velocity at which the projectile should be launched based on the spell's range property, the
     * caster's modifiers, and whether the projectile is affected by gravity.
     *
     * @param ctx          the cast context containing spell information and modifiers
     * @param projectile   the projectile entity that will be launched
     * @param launchHeight the height from which the projectile is launched (used for gravity-affected projectiles)
     * @return the calculated velocity for launching the projectile
     */
    protected float calculateVelocity(CastContext ctx, MagicProjectileEntity projectile, float launchHeight) {
        float range = property(DefaultProperties.RANGE) * ctx.modifiers().get(SpellModifiers.RANGE);

        if (projectile.isNoGravity()) {
            if (projectile.getLifeTime() <= 0) return FALLBACK_VELOCITY;
            return range / projectile.getLifeTime();
        } else {
            float g = 0.05f;
            return range / Mth.sqrt(2 * launchHeight / g);
        }
    }

    /**
     * Allows subclasses to apply additional modifications to the projectile before it is launched.
     *
     * @param ctx        the cast context containing spell information and modifiers
     * @param projectile the projectile entity that will be launched
     */
    protected void addProjectileExtras(CastContext ctx, T projectile) {
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
