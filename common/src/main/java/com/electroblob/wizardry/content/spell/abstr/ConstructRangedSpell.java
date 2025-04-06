package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.content.entity.construct.MagicConstructEntity;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.LocationCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.RayTracer;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class ConstructRangedSpell<T extends MagicConstructEntity> extends ConstructSpell<T>{
    protected boolean hitLiquids = false;
    protected boolean ignoreUncollidables = false;

    public ConstructRangedSpell(Function<Level, T> constructFactory, boolean permanent) {
        super(constructFactory, permanent);
    }

    public Spell hitLiquids(boolean hitLiquids) {
        this.hitLiquids = hitLiquids;
        return this;
    }

    public Spell ignoreUncollidables(boolean ignoreUncollidables) {
        this.ignoreUncollidables = ignoreUncollidables;
        return this;
    }

    @Override
    public boolean canCastByLocation() {
        return true;
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        double range = property(DefaultProperties.RANGE).doubleValue();
        HitResult rayTrace = RayTracer.standardBlockRayTrace(ctx.world(), ctx.caster(), range, hitLiquids, ignoreUncollidables, false);

        if (rayTrace instanceof BlockHitResult blockTrace) {
            Direction direction = blockTrace.getDirection();
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

        playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
        return true;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        double range = property(DefaultProperties.RANGE);
        if (ctx.target() == null) return false;
        if (ctx.caster().distanceTo(ctx.target()) >= range || !ctx.world().isClientSide) return false;

        Vec3 origin = ctx.caster().getEyePosition(1);
        HitResult hit = ctx.world().clip(new ClipContext(origin, ctx.target().position(),
                ClipContext.Block.COLLIDER, hitLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, null));

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
        playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
        return true;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        double range = property(DefaultProperties.RANGE);
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
                ctx.ticksInUse(), ctx.duration());
        return true;
    }


}
