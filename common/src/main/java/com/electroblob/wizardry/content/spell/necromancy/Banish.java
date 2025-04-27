package com.electroblob.wizardry.content.spell.necromancy;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.RaySpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Tiers;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Banish extends RaySpell {
    public static final SpellProperty<Integer> MINIMUM_TELEPORT = SpellProperty.intProperty("minimum_teleport_distance", 8);
    public static final SpellProperty<Integer> MAX_TELEPORT = SpellProperty.intProperty("maximum_teleport_distance", 16);

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (entityHit.getEntity() instanceof LivingEntity target) {
            double minRadius = this.property(MINIMUM_TELEPORT);
            double maxRadius = this.property(MAX_TELEPORT);
            double radius = (minRadius + ctx.world().random.nextDouble() * maxRadius - minRadius);

            teleport(target, ctx.world(), radius);
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

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ctx.world().addParticle(ParticleTypes.PORTAL, x, y - 0.5, z, 0, 0, 0);
        ParticleBuilder.create(EBParticles.DARK_MAGIC).pos(x, y, z).color(0.2f, 0, 0.2f).spawn(ctx.world());
    }

    protected void teleport(LivingEntity entity, Level world, double radius) {
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
            if (!world.getBlockState(new BlockPos(x, y, z)).blocksMotion()) y--;
            if (world.getBlockState(new BlockPos(x, y + 1, z)).blocksMotion()
                    || world.getBlockState(new BlockPos(x, y + 2, z)).blocksMotion()) return;

            if (!world.isClientSide) entity.moveTo(x + 0.5, y + 1, z + 0.5);
            this.playSound(world, entity, 0, -1);
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(Tiers.ADVANCED, Elements.NECROMANCY, SpellType.ATTACK, SpellAction.POINT, 75, 0, 40)
                .add(DefaultProperties.RANGE, 10F)
                .add(MINIMUM_TELEPORT)
                .add(MAX_TELEPORT)
                .build();
    }
}
