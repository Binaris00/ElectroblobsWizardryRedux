package com.electroblob.wizardry.common.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.common.spell.SpellProperties;
import com.electroblob.wizardry.api.common.util.BlockUtil;
import com.electroblob.wizardry.common.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Banish extends RaySpell {
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
        if (target instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) target;

            double minRadius = 8;
            double maxRadius = 16;
            double radius = (minRadius + world.random.nextDouble() * maxRadius - minRadius);

            teleport(entity, world, radius);
        }

        return true;
    }

    @Override
    protected void spawnParticle(Level world, double x, double y, double z, double vx, double vy, double vz) {
        world.addParticle(ParticleTypes.PORTAL, x, y - 0.5, z, 0, 0, 0);
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.2f, 0, 0.2f).spawn(world);
    }

    public void teleport(LivingEntity entity, Level world, double radius) {
        float angle = world.random.nextFloat() * (float) Math.PI * 2;

        int x = Mth.floor(entity.getX() + Mth.sin(angle) * radius);
        int z = Mth.floor(entity.getZ() - Mth.cos(angle) * radius);
        Integer y = BlockUtil.getNearestFloor(world, new BlockPos(x, (int) entity.getY(), z), (int) radius);

        if (world.isClientSide) {
            for (int i = 0; i < 10; i++) {
                double dx1 = entity.getX();
                double dy1 = entity.getY() + entity.getBbHeight() * world.random.nextFloat();
                double dz1 = entity.getZ();
                world.addParticle(ParticleTypes.PORTAL, dx1, dy1, dz1, world.random.nextDouble() - 0.5, world.random.nextDouble() - 0.5, world.random.nextDouble() - 0.5);
            }

            // TODO BIN BLINK EFFECT
            //if (entity instanceof Player) Wizardry.proxy.playBlinkEffect((Player) entity);
        }

        if (y != null) {
            if (!world.getBlockState(new BlockPos(x, y, z)).blocksMotion()) {
                y--;
            }

            if (world.getBlockState(new BlockPos(x, y + 1, z)).blocksMotion() || world.getBlockState(new BlockPos(x, y + 2, z)).blocksMotion()) {
                return;
            }

            if (!world.isClientSide) {
                entity.moveTo(x + 0.5, y + 1, z + 0.5);
            }

            this.playSound(world, entity, 0, -1);
        }

    }

    @Override
    protected SpellProperties properties() {
        return null;
    }
}
