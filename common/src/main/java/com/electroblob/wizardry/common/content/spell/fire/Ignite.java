package com.electroblob.wizardry.common.content.spell.fire;

import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Ignite extends RaySpell {
    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return false;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        BlockPos blockPos = pos.relative(side);
        if(world.isEmptyBlock(blockPos)){
            if(!world.isClientSide && BlockUtil.canPlaceBlock(caster, world, blockPos)){
                world.setBlockAndUpdate(blockPos, Blocks.FIRE.defaultBlockState());
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        if(target instanceof LivingEntity) {
            target.setSecondsOnFire(70);
            return true;
        }

        return false;
    }

    @Override
    public boolean isInstantCast() {
        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        world.addParticle(ParticleTypes.FLAME, x, y, z, 0,0,0);
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
