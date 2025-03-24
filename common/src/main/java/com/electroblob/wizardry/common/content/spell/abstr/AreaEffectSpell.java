package com.electroblob.wizardry.common.content.spell.abstr;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.common.content.spell.earth.ForestsCurse;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

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
    protected void perform(Caster caster) {
        if(!(caster instanceof Player player)) return;

        // TODO Bin: tickuse 0
        findAndAffectEntities(player.level(), player.position(), player, 0);
        // ticks in use 0
        this.playSound(player.level(), player, 0, -1);
    }

    protected boolean findAndAffectEntities(Level world, Vec3 origin, @Nullable LivingEntity caster, int ticksInUse) {
        // TODO Bin: Effect radius
        double radius = 10;

        // TODO TEMP
        if(this instanceof ForestsCurse) radius = 5;

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

    protected abstract boolean affectEntity(Level world, Vec3 origin, @Nullable LivingEntity caster,
                                            LivingEntity target, int targetCount, int ticksInUse);

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

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
