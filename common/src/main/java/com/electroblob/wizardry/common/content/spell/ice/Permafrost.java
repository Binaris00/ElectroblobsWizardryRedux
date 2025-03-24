package com.electroblob.wizardry.common.content.spell.ice;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.EBBlocks;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Permafrost extends RaySpell {

    public Permafrost(){
        this.particleVelocity(1);
        this.particleSpacing(0.5);
        //soundValues(0.5f, 1, 0);
        this.ignoreLivingEntities(true);
    }

    // TODO Bin: Loop sound

//    @Override
//    protected SoundEvent[] createSounds() {
//        return this.createContinuousSpellSounds();
//    }
//
//    @Override
//    protected void playSound(Level world, LivingEntity entity, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
//        this.playSoundLoop(world, entity, ticksInUse);
//    }
//
//    @Override
//    protected void playSound(Level world, double x, double y, double z, int ticksInUse, int duration, SpellModifiers modifiers, String... sounds) {
//        this.playSoundLoop(world, x, y, z, ticksInUse, duration);
//    }

    @Override
    protected boolean onMiss(Level world, @Nullable LivingEntity caster, Vec3 origin, Vec3 direction, int ticksInUse) {
        return true;
    }

    @Override
    protected boolean onBlockHit(Level world, BlockPos pos, Direction side, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        boolean flag = false;

        if (!world.isClientSide) {
            float radius = 0.5f + 0.73f;

            int duration = 600;

            List<BlockPos> sphere = BlockUtil.getBlockSphere(pos.above(), radius);

            for (BlockPos pos1 : sphere) {
                flag |= tryToPlaceIce(world, pos1, caster, duration);
            }

            return flag;
        }

        return true;
    }

    private boolean tryToPlaceIce(Level world, BlockPos pos, LivingEntity caster, int duration) {
        if (world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP) &&
                BlockUtil.canBlockBeReplaced(world, pos)) {
            if (BlockUtil.canPlaceBlock(caster, world, pos)) {
                world.setBlockAndUpdate(pos, EBBlocks.PERMAFROST.get().defaultBlockState());
                world.scheduleTick(pos.immutable(), EBBlocks.PERMAFROST.get(), duration);
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean onEntityHit(Level world, Entity target, Vec3 hit, @Nullable LivingEntity caster, Vec3 origin, int ticksInUse) {
        return false;
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        float brightness = world.random.nextFloat();
        ParticleBuilder.create(EBParticles.DUST).pos(x, y, z).velocity(vx, vy, vz).time(8 + world.random.nextInt(12))
                .color(0.4f + 0.6f * brightness, 0.6f + 0.4f * brightness, 1).spawn(world);
        ParticleBuilder.create(EBParticles.SNOW).pos(x, y, z).velocity(vx, vy, vz).time(8 + world.random.nextInt(12)).spawn(world);
    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
