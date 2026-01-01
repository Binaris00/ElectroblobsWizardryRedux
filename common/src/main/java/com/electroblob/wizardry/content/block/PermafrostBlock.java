package com.electroblob.wizardry.content.block;

import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PermafrostBlock extends DryFrostedIceBlock {
    protected static final VoxelShape BOUNDING_BOX = Shapes.create(0, 0, 0, 1, 0.05, 1);

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return BOUNDING_BOX;
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        entity.setIsInPowderSnow(true);

        if (level.isClientSide) {
            RandomSource randomSource = level.getRandom();
            boolean $$5 = entity.xOld != entity.getX() || entity.zOld != entity.getZ();
            if ($$5 && randomSource.nextBoolean()) {
                level.addParticle(ParticleTypes.SNOWFLAKE, entity.getX(), entity.getY() + 1, entity.getZ(),
                        Mth.randomBetween(randomSource, -1.0F, 1.0F) * 0.083333336F, 0.05000000074505806,
                        Mth.randomBetween(randomSource, -1.0F, 1.0F) * 0.083333336F);
            }
        }

        if (entity instanceof LivingEntity livingEntity && entity.tickCount % 30 == 0) {
            entity.hurt(entity.damageSources().magic(), Spells.PERMAFROST.property(DefaultProperties.DAMAGE));
            entity.makeStuckInBlock(state, new Vec3(0.8999999761581421, 1.5, 0.8999999761581421));

            int duration = Spells.PERMAFROST.property(DefaultProperties.EFFECT_DURATION);
            int amplifier = Spells.PERMAFROST.property(DefaultProperties.EFFECT_STRENGTH);
            if (!level.isClientSide) livingEntity.addEffect(new MobEffectInstance(EBMobEffects.FROST.get(), duration, amplifier));
        }
    }
}
