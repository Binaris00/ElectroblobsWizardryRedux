package com.binaris.wizardry.content.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.content.blockentity.SpectralBlockEntity;
import com.binaris.wizardry.setup.registries.EBBlockEntities;
import com.binaris.wizardry.setup.registries.client.EBParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpectralBlock extends BaseEntityBlock {

    public SpectralBlock() {
        // I'm a literal copy of the glass, hi!
         super(BlockBehaviour.Properties.of()
                 .instrument(NoteBlockInstrument.HAT).strength(0.3F).sound(SoundType.GLASS)
                 .noOcclusion()
                 .isValidSpawn((state, getter, pos, entityType) -> false)
                 .isRedstoneConductor((state, getter, pos) -> false)
                 .isSuffocating((state, getter, pos) -> false)
                 .isViewBlocking((state, getter, pos) -> false)
                 .lightLevel(state -> 0));
     }

     
     @Nullable
     protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type,
                                                                               BlockEntityType<SpectralBlockEntity> entityType) {
         return createTickerHelper(type, entityType, SpectralBlockEntity::update);
     }
 
     @Override
     public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
         return RenderShape.MODEL;
     }
 
     @Override
     public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
         return new SpectralBlockEntity(pos, state);
     }
 
     @Nullable
     @Override
     public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                   @NotNull BlockEntityType<T> type) {
         return createTicker(level, type, EBBlockEntities.SPECTRAL_BLOCK.get());
     }

     @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) ? true : super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }
 
     @Override
     public void animateTick(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull RandomSource random) {
         if (world.isClientSide) {
             for (int i = 0; i < 2; i++) {
                 ParticleBuilder.create(EBParticles.DUST)
                         .pos(pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble())
                         .time((int) (16.0D / (random.nextDouble() * 0.8D + 0.2D)))
                         .color(0.4f + random.nextFloat() * 0.2f, 0.6f + random.nextFloat() * 0.4f, 0.6f + random.nextFloat() * 0.4f)
                         .shaded(true).spawn(world);
             }
         }
     }
}