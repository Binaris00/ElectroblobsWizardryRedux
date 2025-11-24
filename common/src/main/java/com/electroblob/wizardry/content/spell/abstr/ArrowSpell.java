package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.content.entity.projectile.MagicArrowEntity;
import com.electroblob.wizardry.api.content.entity.projectile.MagicProjectileEntity;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.*;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBItems;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Represents a spell that launches a {@link MagicArrowEntity}-based projectile.
 * <p>
 * This class abstracts the logic of casting spells that behave like arrows, handling all the core steps
 * such as entity creation, aiming, velocity calculation, and launch.
 * <p>
 * Check {@link com.electroblob.wizardry.setup.registries.Spells#DART Spells#Dart}
 * - {@link com.electroblob.wizardry.setup.registries.Spells#MAGIC_MISSILE Spells#MagicMissile} for some examples
 *
 * @param <T> The type of {@link MagicArrowEntity} this spell launches.
 */
public class ArrowSpell<T extends MagicArrowEntity> extends Spell {
    /**
     * A factory function to create instances of the projectile entity.
     */
    protected final Function<Level, T> arrowFactory;

    public ArrowSpell(Function<Level, T> arrowFactory) {
        this.arrowFactory = arrowFactory;
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
            T arrow = arrowFactory.apply(ctx.world());
            arrow.aim(ctx.caster(), calculateVelocity(ctx, arrow, ctx.caster().getEyeHeight()) - (float) MagicArrowEntity.LAUNCH_Y_OFFSET);
            arrow.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
            addArrowExtras(arrow, ctx.caster());
            ctx.world().addFreshEntity(arrow);
        }

        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (ctx.target() == null) return false;

        if (!ctx.world().isClientSide) {
            T arrow = arrowFactory.apply(ctx.world());
            int aimingError = EntityUtil.getDefaultAimingError(ctx.world().getDifficulty());
            arrow.aim(ctx.caster(), ctx.target(), calculateVelocity(ctx, arrow, ctx.caster().getEyeHeight() - (float) MagicProjectileEntity.LAUNCH_Y_OFFSET), aimingError);
            arrow.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
            addArrowExtras(arrow, ctx.caster());
            ctx.world().addFreshEntity(arrow);
        }

        ctx.caster().swing(ctx.hand());
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }


    @Override
    public boolean cast(LocationCastContext ctx) {
        if (!ctx.world().isClientSide) {
            T arrow = arrowFactory.apply(ctx.world());
            arrow.setPos(ctx.vec3());
            Vec3 vec = Vec3.atLowerCornerOf(ctx.direction().getNormal());
            arrow.shoot(vec.x(), vec.y(), vec.z(), calculateVelocity(ctx, arrow, 0.375f), 1);
            arrow.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
            addArrowExtras(arrow, null);
            ctx.world().addFreshEntity(arrow);
        }

        this.playSound(ctx.world(), ctx.x() - ctx.direction().getStepX(),
                ctx.y() - ctx.direction().getStepY(), ctx.z() - ctx.direction().getStepZ(),
                ctx.castingTicks(), ctx.duration());
        return true;
    }

    /**
     * Calculates the velocity of the projectile based on gravity and range.
     *
     * @param ctx          Cast Context about how the spell is cast
     * @param projectile   The projectile entity.
     * @param launchHeight The vertical height from which the projectile is launched.
     * @return The velocity value to be used when launching the projectile.
     */
    public float calculateVelocity(CastContext ctx, MagicArrowEntity projectile, float launchHeight) {
        float range = this.property(DefaultProperties.RANGE) * ctx.modifiers().get(EBItems.RANGE_UPGRADE.get());

        if (projectile.isNoGravity()) {
            if (projectile.getLifetime() <= 0) return 2;
            return range / projectile.getLifetime();
        } else {
            float g = 0.05f;
            return range / Mth.sqrt(2 * launchHeight / g);
        }
    }

    /**
     * Makes changes to arrows before it's spawned.
     * Override this is subclasses to apply special effects
     *
     * @param arrow  The arrow instance to modify.
     * @param caster The entity who cast the spell, may be null in location-based casts.
     */
    protected void addArrowExtras(T arrow, @Nullable LivingEntity caster) {
        // Meant to be overridden by subclasses or anonymous spells.
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