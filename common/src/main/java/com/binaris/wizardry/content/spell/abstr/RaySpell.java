package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.client.util.ClientUtils;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.LocationCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.api.content.util.RayTracer;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("UnusedReturnValue")
public abstract class RaySpell extends Spell {
    protected static final double Y_OFFSET = 0.25;
    protected double particleSpacing = 0.85;
    protected double particleJitter = 0.1;
    protected double particleVelocity = 0;
    protected boolean ignoreLivingEntities = false;
    protected boolean hitLiquids = false;
    protected boolean ignoreUncollidables = true;
    protected float aimAssist = 0;

    public Spell particleSpacing(double particleSpacing) {
        this.particleSpacing = particleSpacing;
        return this;
    }

    public Spell particleJitter(double particleJitter) {
        this.particleJitter = particleJitter;
        return this;
    }

    public Spell particleVelocity(double particleVelocity) {
        this.particleVelocity = particleVelocity;
        return this;
    }

    public Spell ignoreLivingEntities(boolean ignoreLivingEntities) {
        this.ignoreLivingEntities = ignoreLivingEntities;
        return this;
    }

    public Spell hitLiquids(boolean hitLiquids) {
        this.hitLiquids = hitLiquids;
        return this;
    }


    public Spell ignoreUncollidables(boolean ignoreUncollidables) {
        this.ignoreUncollidables = ignoreUncollidables;
        return this;
    }


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

    protected abstract boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin);

    protected abstract boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin);

    protected abstract boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction);

    protected boolean shootSpell(CastContext ctx, Vec3 origin, Vec3 direction) {
        double range = this.property(DefaultProperties.RANGE) * ctx.modifiers().get(EBItems.RANGE_UPGRADE.get());
        Vec3 endpoint = origin.add(direction.scale(range));

        HitResult rayTrace = RayTracer.rayTrace(ctx.world(), ctx.caster(), origin, endpoint, aimAssist, hitLiquids, Entity.class, ignoreLivingEntities ? EntityUtil::isLiving : RayTracer.ignoreEntityFilter(ctx.caster()));

        boolean flag = false;

        if (rayTrace != null) {
            if (rayTrace instanceof EntityHitResult entityHit) {
                flag = onEntityHit(ctx, entityHit, origin);
                if (flag) range = origin.distanceTo(rayTrace.getLocation());
            } else if (rayTrace instanceof BlockHitResult blockHit) {
                flag = onBlockHit(ctx, blockHit, origin);

                range = origin.distanceTo(rayTrace.getLocation());
            }
        }

        if (!flag && !onMiss(ctx, origin, direction)) return false;


        if (ctx.world().isClientSide) {
            spawnParticleRay(ctx, origin, direction, range);
        }

        return true;
    }

    protected void spawnParticleRay(CastContext ctx, Vec3 origin, Vec3 direction, double distance) {
        Vec3 velocity = direction.scale(particleVelocity);

        for (double d = particleSpacing; d <= distance; d += particleSpacing) {
            double x = origin.x + d * direction.x + particleJitter * (ctx.world().random.nextDouble() * 2 - 1);
            double y = origin.y + d * direction.y + particleJitter * (ctx.world().random.nextDouble() * 2 - 1);
            double z = origin.z + d * direction.z + particleJitter * (ctx.world().random.nextDouble() * 2 - 1);
            spawnParticle(ctx, x, y, z, velocity.x, velocity.y, velocity.z);
        }
    }

    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
    }
}
