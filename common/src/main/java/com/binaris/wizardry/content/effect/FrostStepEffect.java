package com.binaris.wizardry.content.effect;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.effect.MagicMobEffect;
import com.binaris.wizardry.setup.registries.EBBlocks;
import com.binaris.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class FrostStepEffect extends MagicMobEffect {
    public FrostStepEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public void spawnCustomParticle(Level world, double x, double y, double z) {
        ParticleBuilder.create(EBParticles.SNOW).pos(x, y, z).time(15 + world.random.nextInt(5)).spawn(world);
    }

    public static void onEntityMoved(LivingEntity living, Level level, BlockPos pos, int amplifier) {
        if (!living.onGround()) return;

        int i = Math.min(16, 2 + amplifier);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-i, -1, -i), pos.offset(i, -1, i))) {
            if (blockpos.closerToCenterThan(living.position(), (double)i)) {
                blockpos$mutableblockpos.set(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ());
                BlockState blockstate1 = level.getBlockState(blockpos$mutableblockpos);
                if (blockstate1.isAir()) {
                    BlockState blockstate2 = level.getBlockState(blockpos);
                    if (blockstate2 == FrostedIceBlock.meltsInto() && Blocks.FROSTED_ICE.defaultBlockState().canSurvive(level, blockpos) && level.isUnobstructed(Blocks.FROSTED_ICE.defaultBlockState(), blockpos, CollisionContext.empty())) {
                        level.setBlockAndUpdate(blockpos, Blocks.FROSTED_ICE.defaultBlockState());
                        level.scheduleTick(blockpos, Blocks.FROSTED_ICE, Mth.nextInt(living.getRandom(), 60, 120));
                    }

                    if (blockstate2 == Blocks.LAVA.defaultBlockState() && EBBlocks.OBSIDIAN_CRUST.get().defaultBlockState().canSurvive(level, blockpos) && level.isUnobstructed(EBBlocks.OBSIDIAN_CRUST.get().defaultBlockState(), blockpos, CollisionContext.empty())) {
                        level.setBlockAndUpdate(blockpos, EBBlocks.OBSIDIAN_CRUST.get().defaultBlockState());
                        level.scheduleTick(blockpos, EBBlocks.OBSIDIAN_CRUST.get(), Mth.nextInt(living.getRandom(), 60, 120));
                    }
                }
            }
        }

    }
}
