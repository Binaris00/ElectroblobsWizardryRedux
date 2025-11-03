package com.electroblob.wizardry.content.block;

import com.electroblob.wizardry.content.blockentity.BookshelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class BookShelfBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final int SLOT_COUNT = 12;

    public BookShelfBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD).strength(2.0F, 5.0F).sound(SoundType.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult blockHitResult) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity == null || player.isShiftKeyDown()) return InteractionResult.PASS;

        if (blockentity instanceof BookshelfBlockEntity entity) player.openMenu(entity);
        return InteractionResult.CONSUME;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new BookshelfBlockEntity(blockPos, blockState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState state1, boolean b) {
        if (level.getBlockEntity(pos) instanceof BookshelfBlockEntity e) Containers.dropContents(level, pos, e);
        super.onRemove(state, level, pos, state1, b);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, Level level, @NotNull BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof BookshelfBlockEntity e) {
            int slotsOccupied = 0;

            for (int i = 0; i < e.getContainerSize(); i++) {
                if (!e.getItem(i).isEmpty()) slotsOccupied++;
            }

            return (int) Math.floor((slotsOccupied / (float) e.getContainerSize()) * 15);
        }
        return super.getAnalogOutputSignal(state, level, pos);
    }

    @Override
    public boolean triggerEvent(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, int i, int i1) {
        super.triggerEvent(state, level, pos, i, i1);
        BlockEntity blockentity = level.getBlockEntity(pos);
        return blockentity != null && blockentity.triggerEvent(i, i1);
    }
}
