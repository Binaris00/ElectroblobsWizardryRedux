package com.binaris.wizardry.content.block;

import com.binaris.wizardry.content.blockentity.BookshelfBlockEntity;
import com.binaris.wizardry.setup.registries.EBBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
        super(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD).strength(2.0F, 5.0F)
                .sound(SoundType.WOOD).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult blockHitResult) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (!(blockentity instanceof BookshelfBlockEntity entity)) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            ItemStack heldStack = player.getItemInHand(hand);

            if (!heldStack.isEmpty() && !entity.canPlaceItem(0, heldStack)) return InteractionResult.PASS;
            if (level.isClientSide) return InteractionResult.SUCCESS;

            if (heldStack.isEmpty()) {
                for (int slot = 0; slot < entity.getContainerSize(); slot++) {
                    if (!entity.getItem(slot).isEmpty()) {
                        ItemStack removed = entity.removeItemNoUpdate(slot);
                        if (!player.getInventory().add(removed)) player.drop(removed, false);
                        entity.setChanged();
                        return InteractionResult.CONSUME;
                    }
                }
            } else {
                for (int slot = 0; slot < entity.getContainerSize(); slot++) {
                    if (entity.getItem(slot).isEmpty()) {
                        entity.setItem(slot, heldStack.split(1));
                        return InteractionResult.CONSUME;
                    }
                }
            }

            return InteractionResult.CONSUME;
        }

        if (level.isClientSide) return InteractionResult.SUCCESS;
        player.openMenu(entity);
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
        if (state.getBlock() != state1.getBlock() && level.getBlockEntity(pos) instanceof BookshelfBlockEntity e) {
            Containers.dropContents(level, pos, e);
        }
        super.onRemove(state, level, pos, state1, b);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide || type != EBBlockEntities.BOOKSHELF.get()) return null;
        return (lvl, pos, blockState, blockEntity) -> {
            if (blockEntity instanceof BookshelfBlockEntity bookshelf) BookshelfBlockEntity.serverTick(lvl, pos, blockState, bookshelf);
        };
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
