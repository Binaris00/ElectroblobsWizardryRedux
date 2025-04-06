package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Reversal extends RaySpell {
    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if(ctx.caster() == null || !(entityHit.getEntity() instanceof LivingEntity target)) return true;

        List<MobEffectInstance> negativePotions = new ArrayList<>(ctx.caster().getActiveEffects());
        negativePotions.removeIf(p -> !p.getEffect().getCategory().equals(MobEffectCategory.HARMFUL));

        if(ctx.world().isClientSide) {
            ParticleBuilder.create(EBParticles.BUFF).entity(ctx.caster()).color(1, 1, 0.3f).spawn(ctx.world());
            return true;
        }

        if (negativePotions.isEmpty()) return false;

        // TODO BIN, no bonus effects :c
        int bonusEffects = 0;
        int n = property(DefaultProperties.EFFECT_STRENGTH) + bonusEffects;

        Collections.shuffle(negativePotions);
        negativePotions = negativePotions.subList(0, Math.min(negativePotions.size(), n));

        negativePotions.forEach(p -> ctx.caster().removeEffect(p.getEffect()));
        negativePotions.forEach(((LivingEntity) target)::addEffect);

        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    public boolean canCastByLocation() {
        return false; // :c
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(ctx.world());
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + ctx.world().random.nextInt(8)).color(0.1f, 0, 0.05f).spawn(ctx.world());
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 8F)
                .add(DefaultProperties.EFFECT_STRENGTH, 1)
                .build();
    }
}
