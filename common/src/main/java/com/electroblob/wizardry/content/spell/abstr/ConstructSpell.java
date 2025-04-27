package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.electroblob.wizardry.api.content.entity.construct.ScaledConstructEntity;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.LocationCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ConstructSpell<T extends MagicConstructEntity> extends Spell {
    protected final Function<Level, T> constructFactory;
    protected final boolean permanent;
    protected boolean requiresFloor = false;
    protected boolean allowOverlap = false;

    public ConstructSpell(Function<Level, T> constructFactory, boolean permanent){
        this.constructFactory = constructFactory;
        this.permanent = permanent;
    }

    public ConstructSpell<T> floor(boolean requiresFloor) {
        this.requiresFloor = requiresFloor;
        return this;
    }

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
            if (!spawnConstruct(ctx, ctx.caster().position(), ctx.caster().onGround() ? Direction.UP : null)) return false;
            this.playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
            return true;
        }
        return false;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        if (ctx.target() != null && (ctx.caster().onGround() || !requiresFloor)) {
            if (!spawnConstruct(ctx, ctx.caster().position(), ctx.caster().onGround() ? Direction.UP : null)) return false;
            this.playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
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
                    ctx.ticksInUse(), ctx.duration());
            return true;
        }

        return false;
    }

    protected boolean spawnConstruct(CastContext ctx, Vec3 vec3, @Nullable Direction side) {
        if (!ctx.world().isClientSide) {
            T construct = constructFactory.apply(ctx.world());

            construct.setPos(vec3);
            if(ctx.caster() != null) construct.setCaster(ctx.caster());

            construct.lifetime = permanent ? -1 : property(DefaultProperties.DURATION);
            construct.damageMultiplier = 1;
            if (construct instanceof ScaledConstructEntity)
                ((ScaledConstructEntity) construct).setSizeMultiplier(1);
            addConstructExtras(construct, side, ctx.caster());

            if (!allowOverlap && !ctx.world().getEntitiesOfClass(construct.getClass(), construct.getBoundingBox()).isEmpty())
                return false;

            ctx.world().addFreshEntity(construct);
        }

        return true;
    }

    protected void addConstructExtras(T construct, Direction side, @Nullable LivingEntity caster) {
    }


    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
