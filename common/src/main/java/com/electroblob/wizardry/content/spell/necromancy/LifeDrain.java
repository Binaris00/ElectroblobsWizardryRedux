package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class LifeDrain extends RaySpell {
    public LifeDrain() {
        this.particleVelocity(-0.5);
        this.particleSpacing(0.4);
        this.soundValues(0.6f, 1, 0);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if(!(entityHit.getEntity() instanceof LivingEntity target) || ctx.ticksInUse() % 12 != 0) return false;
        float damage = property(DefaultProperties.DAMAGE);
        DamageSource source = ctx.caster() != null ? EBMagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.SORCERY)
                : target.damageSources().magic();

        EntityUtil.attackEntityWithoutKnockback(target, source, damage);
        if (ctx.caster() != null) ctx.caster().heal(damage * property(DefaultProperties.HEALTH));
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
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        if (ctx.world().random.nextInt(5) == 0) {
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(ctx.world());
        }
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(vx, vy, vz).time(8 + ctx.world().random.nextInt(6))
                .color(0.5f, 0, 0).spawn(ctx.world());
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.DAMAGE, 2F)
                .add(DefaultProperties.HEALTH, 0.35F)
                .build();
    }
}
