package com.electroblob.wizardry.common.content.spell.ice;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.BlockUtil;
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

public class Freeze extends RaySpell {

    public Freeze() {
        // Todo: freeze sound
        //this.soundValues(1, 1.4f, 0.4f);
        this.hitLiquids(true);
        this.ignoreUncollidables(false);
    }

    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return false;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if (!world.isClientSide && BlockUtil.canPlaceBlock(caster, world, pos)) {
            BlockUtil.freeze(world, pos, true);
        }

        return true;
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if (EntityUtil.isLiving(target)) {
            if (target instanceof Blaze || target instanceof MagmaCube) {
                target.hurt(target.damageSources().indirectMagic(caster, target), 3);
            }

            if(target instanceof LivingEntity livingEntity){
                //livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), 200, 1));
            }

            if (target.isOnFire()) target.clearFire();

            return true;
        }

        return false;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        float brightness = 0.5f + (world.random.nextFloat() / 2);
//        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).time(12 + world.random.nextInt(8))
//                .color(brightness, brightness + 0.1f, 1).spawn(world);
//        ParticleBuilder.create(EBParticles.SNOW).pos(x, y, z).spawn(world);
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
