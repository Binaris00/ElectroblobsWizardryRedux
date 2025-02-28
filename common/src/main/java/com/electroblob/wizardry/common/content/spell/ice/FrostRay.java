package com.electroblob.wizardry.common.content.spell.ice;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FrostRay extends RaySpell {

    public FrostRay() {
        this.particleVelocity(1);
        this.particleSpacing(0.5);
    }

    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return true;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        return false;
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if(target instanceof LivingEntity livingTarget){
            if(livingTarget.isOnFire()) livingTarget.clearFire();

            //livingTarget.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 200 , 1));
            if(ticksInUse % livingTarget.invulnerableDuration == 1){
                float damage = 10;
                if(target instanceof Blaze || target instanceof MagmaCube) damage *= 2;

                EntityUtil.attackEntityWithoutKnockback(livingTarget, livingTarget.damageSources().indirectMagic(caster, livingTarget), damage);
            }
        }
        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        float brightness = world.random.nextFloat();
        //ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(vx, vy, vz).time(8 + world.random.nextInt(12))
         //       .color(0.4f + 0.6f * brightness, 0.6f + 0.4f * brightness, 1).collide(true).spawn(world);
        //ParticleBuilder.create(EBParticles.SNOW).pos(x, y, z).velocity(vx, vy, vz).time(8 + world.random.nextInt(12)).collide(true).spawn(world);
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
