package com.electroblob.wizardry.content.spell.fire;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class FlameRay extends RaySpell {
    public FlameRay(){
        this.particleVelocity(1);
        this.particleSpacing(0.5);
        this.soundValues(2.5f, 1, 0);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity target && ctx.ticksInUse() % target.invulnerableDuration == 1
                && !EBMagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target)) {
            target.setSecondsOnFire(property(DefaultProperties.EFFECT_DURATION));
            DamageSource source = ctx.caster() != null ? EBMagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.FIRE)
                    : target.damageSources().magic();
            EntityUtil.attackEntityWithoutKnockback(target, source, property(DefaultProperties.DAMAGE));
        }

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

    // TODO TEMP
    @Override
    public SpellAction getAction() {
        return SpellAction.THRUST;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
//        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).collide(true).spawn(ctx.world());
//        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).collide(true).spawn(ctx.world());
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 10F)
                .add(DefaultProperties.DAMAGE, 3F)
                .add(DefaultProperties.EFFECT_DURATION, 10)
                .build();
    }
}
