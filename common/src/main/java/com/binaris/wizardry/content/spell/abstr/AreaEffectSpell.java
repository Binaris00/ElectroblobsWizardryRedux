package com.binaris.wizardry.content.spell.abstr;

import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.EntityCastContext;
import com.binaris.wizardry.api.content.spell.internal.LocationCastContext;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
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

    protected abstract boolean affectEntity(CastContext ctx, Vec3 origin, LivingEntity target, int targetCount);

    protected void spawnParticleEffect(CastContext ctx, Vec3 origin, double radius) {
        int particleCount = (int) Math.round(particleDensity * Math.PI * radius * radius);

        for (int i = 0; i < particleCount; i++) {
            double r = (1 + ctx.world().random.nextDouble() * (radius - 1));
            float angle = ctx.world().random.nextFloat() * (float) Math.PI * 2f;

            spawnParticle(ctx.world(), origin.x + r * Mth.cos(angle), origin.y, origin.z + r * Mth.sin(angle));
        }
    }

    protected void spawnParticle(Level world, double x, double y, double z) {
    }
}
