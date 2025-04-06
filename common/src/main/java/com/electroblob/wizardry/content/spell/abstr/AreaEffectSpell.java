package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.EntityCastContext;
import com.electroblob.wizardry.api.content.spell.internal.LocationCastContext;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public abstract class AreaEffectSpell extends Spell {
    protected boolean targetAllies = false;
    protected boolean alwaysSucceed = false;
    protected float particleDensity = 0.65f;

    public AreaEffectSpell targetAllies(boolean targetAllies) {
        this.targetAllies = targetAllies;
        return this;
    }

    public AreaEffectSpell alwaysSucceed(boolean alwaysSucceed) {
        this.alwaysSucceed = alwaysSucceed;
        return this;
    }

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
        boolean result = findAndAffectEntities(ctx.world(), ctx.caster().position(), ctx.caster(), ctx.ticksInUse());
        if(result) this.playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
        return result;
    }

    @Override
    public boolean cast(EntityCastContext ctx) {
        boolean result = findAndAffectEntities(ctx.world(), ctx.caster().position(), ctx.caster(), ctx.ticksInUse());
        if(result) this.playSound(ctx.world(), ctx.caster(), ctx.ticksInUse(), -1);
        return result;
    }

    @Override
    public boolean cast(LocationCastContext ctx) {
        boolean result = findAndAffectEntities(ctx.world(), ctx.vec3(), null, ctx.ticksInUse());
        if(result) this.playSound(ctx.world(), ctx.vec3(), ctx.ticksInUse(), -1);
        return result;
    }

    protected boolean findAndAffectEntities(Level world, Vec3 origin, @Nullable LivingEntity caster, int ticksInUse) {
        double radius = this.property(DefaultProperties.EFFECT_RADIUS);

        List<LivingEntity> targets = EntityUtil.getLivingWithinRadius(radius, origin.x, origin.y, origin.z, world);

        if (targetAllies) {
            // TODO Bin: Ally implementation !AllyDesignationSystem.isAllied(caster, target)
            targets.removeIf(target -> target != caster);
        } else {
            // TODO Bin: Ally implementation !AllyDesignationSystem.isAllied(caster, target)
            //targets.removeIf(target -> !AllyDesignationSystem.isValidTarget(caster, target));
        }

        targets.sort(Comparator.comparingDouble(e -> e.distanceToSqr(origin.x, origin.y, origin.z)));

        boolean result = alwaysSucceed;
        int i = 0;

        for (LivingEntity target : targets) {
            if (affectEntity(world, origin, caster, target, i++, ticksInUse)) result = true;
        }

        if (world.isClientSide) spawnParticleEffect(world, origin, radius, caster);
        return result;
    }

    protected abstract boolean affectEntity(Level world, Vec3 origin, @Nullable LivingEntity caster, LivingEntity target, int targetCount, int ticksInUse);

    protected void spawnParticleEffect(Level world, Vec3 origin, double radius, @Nullable LivingEntity caster) {
        int particleCount = (int) Math.round(particleDensity * Math.PI * radius * radius);

        for (int i = 0; i < particleCount; i++) {
            double r = (1 + world.random.nextDouble() * (radius - 1));
            float angle = world.random.nextFloat() * (float) Math.PI * 2f;

            spawnParticle(world, origin.x + r * Mth.cos(angle), origin.y, origin.z + r * Mth.sin(angle));
        }
    }

    protected void spawnParticle(Level world, double x, double y, double z) {
    }
}
