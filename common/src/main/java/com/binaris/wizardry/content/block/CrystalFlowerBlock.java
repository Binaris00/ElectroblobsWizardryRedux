package com.binaris.wizardry.content.block;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CrystalFlowerBlock extends FlowerBlock implements BonemealableBlock {

    public CrystalFlowerBlock(Properties properties) {
        super(MobEffects.REGENERATION, 40, properties);
    }

    @Override
    public void animateTick(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (world.isClientSide && random.nextBoolean()) {
            ParticleBuilder.create(EBParticles.SPARKLE)
                    .pos(pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble() / 2 + 0.5, pos.getZ() + random.nextDouble()).velocity(0, 0.01, 0)
                    .time(20 + random.nextInt(10)).color(0.5f + (random.nextFloat() / 2), 0.5f + (random.nextFloat() / 2),
                            0.5f + (random.nextFloat() / 2)).spawn(world);
        }
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockPos position = pos.offset(level.random.nextInt(8) - level.random.nextInt(8),
                level.random.nextInt(4) - level.random.nextInt(4), level.random.nextInt(8) -
                        level.random.nextInt(8));

        if (level.isEmptyBlock(new BlockPos(pos)) && (!level.dimension().equals(Level.NETHER) || pos.getY() < 127)
                && EBBlocks.CRYSTAL_FLOWER.get().defaultBlockState().canSurvive(level, pos)) {
            level.setBlock(position, EBBlocks.CRYSTAL_FLOWER.get().defaultBlockState(), 2);
        }
    }
}
