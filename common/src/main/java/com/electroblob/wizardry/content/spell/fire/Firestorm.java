package com.electroblob.wizardry.content.spell.fire;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.content.util.EBMagicDamageSource;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.AreaEffectSpell;
import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.util.BlockUtil;
import com.electroblob.wizardry.api.content.util.DrawingUtils;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Firestorm extends AreaEffectSpell {
    public Firestorm(){
        this.soundValues(2f, 1.0f, 0);
        this.alwaysSucceed(true);
    }

    @Override
    protected void perform(Caster caster) {
        super.perform(caster);
        if(!(caster instanceof Player player)) return;
        burnNearbyBlocks(player.level(), player.position(), player);
    }

    @Override
    protected boolean affectEntity(Level world, Vec3 origin, @Nullable LivingEntity caster, LivingEntity target, int targetCount, int ticksInUse) {
        if(!EBMagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target))
            target.setSecondsOnFire(property(DefaultProperties.EFFECT_DURATION));
        return true;
    }

    private void burnNearbyBlocks(Level world, Vec3 origin, @Nullable LivingEntity caster) {
        if(world.isClientSide || !EntityUtil.canDamageBlocks(caster, world)) return;

        double radius = property(DefaultProperties.EFFECT_RADIUS);

        for (int i = -(int) radius; i <= (int) radius; i++) {
            for (int j = -(int) radius; j <= (int) radius; j++) {
                BlockPos pos = new BlockPos((int) origin.x(), (int) origin.y(), (int) origin.z()).offset(i, 0, j);
                Integer y = BlockUtil.getNearestSurface(world, new BlockPos(pos), Direction.UP, (int) radius, true, BlockUtil.SurfaceCriteria.NOT_AIR_TO_AIR);

                if (y != null) {
                    pos = new BlockPos(pos.getX(), y, pos.getZ());

                    double dist = origin.distanceTo(new Vec3(origin.x + i, y, origin.z + j));

                    if (y != -1 && world.random.nextInt((int) (dist * 2) + 1) < radius && dist < radius && dist > 1.5 && BlockUtil.canPlaceBlock(caster, world, pos)) {
                        world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                    }
                }
            }
        }
    }

    @Override
    protected void spawnParticleEffect(Level world, Vec3 origin, double radius, @Nullable LivingEntity caster) {
        for (int i = 0; i < 100; i++) {
            float r = world.random.nextFloat();
            double speed = 0.02 / r * (1 + world.random.nextDouble());
            ParticleBuilder.create(EBParticles.MAGIC_FIRE)
                    .pos(origin.x, origin.y + world.random.nextDouble() * 3, origin.z)
                    .velocity(0, 0, 0)
                    .scale(2)
                    .time(40 + world.random.nextInt(10))
                    .spin(world.random.nextDouble() * (radius - 0.5) + 0.5, speed)
                    .spawn(world);
        }

        for (int i = 0; i < 60; i++) {
            float r = world.random.nextFloat();
            double speed = 0.02 / r * (1 + world.random.nextDouble());
            ParticleBuilder.create(EBParticles.CLOUD)
                    .pos(origin.x, origin.y + world.random.nextDouble() * 2.5, origin.z)
                    .color(DrawingUtils.mix(DrawingUtils.mix(0xffbe00, 0xff3600, r / 0.6f), 0x222222, (r - 0.6f) / 0.4f))
                    .spin(r * (radius - 1) + 0.5, speed)
                    .spawn(world);
        }
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder().add(DefaultProperties.EFFECT_RADIUS, 6)
                .add(DefaultProperties.EFFECT_DURATION, 15)
                .build();
    }
}
