package com.electroblob.wizardry.common.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.EntityUtil;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LifeDrain extends RaySpell {
    public LifeDrain() {
        this.particleVelocity(-0.5);
        this.particleSpacing(0.4);
        this.soundValues(0.6f, 1, 0);
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
            if (ticksInUse % 12 == 0) {
                float damage = 2;

                DamageSource source = caster != null ? EBMagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.SORCERY)
                        : livingTarget.damageSources().magic();

                EntityUtil.attackEntityWithoutKnockback(livingTarget, source, damage);
                if (caster != null) caster.heal(damage * 0.35F);
            }
        }
        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        if (world.random.nextInt(5) == 0) {
            ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(world);
        }
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).velocity(vx, vy, vz).time(8 + world.random.nextInt(6))
                .color(0.5f, 0, 0).spawn(world);
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
