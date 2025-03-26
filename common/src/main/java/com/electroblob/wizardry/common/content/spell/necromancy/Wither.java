package com.electroblob.wizardry.common.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.util.EBMagicDamageSource;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Wither extends RaySpell {
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
        if(target instanceof LivingEntity livingTarget && !EBMagicDamageSource.isEntityImmune(EBDamageSources.WITHER, target)){
            DamageSource source = caster != null ? EBMagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.WITHER)
                    : livingTarget.damageSources().wither();

            livingTarget.hurt(source, 5);
            livingTarget.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
        }

        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.1f, 0, 0).spawn(world);
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + world.random.nextInt(8)).color(0.1f, 0, 0.05f).spawn(world);
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
