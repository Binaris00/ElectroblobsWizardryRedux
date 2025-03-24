package com.electroblob.wizardry.common.content.spell.fire;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FlameRay extends RaySpell {

    public FlameRay(){
        this.particleVelocity(1);
        this.particleSpacing(0.5);
        this.soundValues(2.5f, 1, 0);
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
        if (target instanceof LivingEntity livingTarget) {
            if(ticksInUse % livingTarget.invulnerableDuration == 1){
                livingTarget.setSecondsOnFire(20);
                EntityUtil.attackEntityWithoutKnockback(livingTarget, livingTarget.damageSources().indirectMagic(caster, livingTarget), 5);
            }
        }

        return true;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).collide(true).spawn(world);
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).velocity(vx, vy, vz).collide(true).spawn(world);
    }


    @Override
    protected SpellProperties properties() {
        return null;
    }
}
