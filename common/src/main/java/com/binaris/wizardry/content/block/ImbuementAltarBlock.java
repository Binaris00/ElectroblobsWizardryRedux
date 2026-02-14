package com.binaris.wizardry.content.block;

import com.binaris.wizardry.api.content.util.BlockUtil;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.content.blockentity.ImbuementAltarBlockEntity;
import com.binaris.wizardry.content.item.RandomSpellBookItem;
import com.binaris.wizardry.setup.registries.EBBlockEntities;
import com.binaris.wizardry.setup.registries.EBBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@SuppressWarnings("deprecation")
public class ImbuementAltarBlock extends BaseEntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    private static final VoxelShape AABB = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);

    public ImbuementAltarBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).strength(-1.0F, 6000000.0F).lightLevel((state) -> 1));
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @javax.annotation.Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> type, BlockEntityType<ImbuementAltarBlockEntity> entityType) {
        return createTickerHelper(type, type, ImbuementAltarBlockEntity::update);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof ImbuementAltarBlockEntity entity) || player.isShiftKeyDown()) {
            return InteractionResult.FAIL;
        }

        ItemStack currentStack = entity.getStack();
        ItemStack toInsert = player.getItemInHand(hand);

        if (currentStack.isEmpty()) {
            ItemStack stack = toInsert.copy();
            stack.setCount(1);
            entity.setStack(stack, true);
            entity.setLastUser(player);
            if (!player.isCreative()) toInsert.shrink(1);
            return InteractionResult.SUCCESS;
        }

        if (currentStack.getItem() instanceof RandomSpellBookItem) {
            RandomSpellBookItem.create(level, player, currentStack);
            return InteractionResult.SUCCESS;
        }

        if (!(player.isCreative() && InventoryUtil.doesPlayerHaveItem(player, currentStack.getItem()))) {
            if (!player.addItem(currentStack)) {
                player.drop(currentStack, false);
            }
        }

        entity.setStack(ItemStack.EMPTY, false);
        entity.setLastUser(null);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean movedByPiston) {
        boolean shouldBeActive = Arrays.stream(BlockUtil.getHorizontals())
                .allMatch(s -> level.getBlockState(pos.relative(s)).getBlock() == EBBlocks.WALL_RECEPTACLE.get()
                        && level.getBlockState(pos.relative(s)).getValue(WallReceptacleBlock.FACING) == s);

        if (level.getBlockState(pos).getValue(ACTIVE) != shouldBeActive) {
            BlockEntity te = level.getBlockEntity(pos);
            ItemStack stack = ItemStack.EMPTY;
            if (te instanceof ImbuementAltarBlockEntity e) stack = e.getStack();

            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(ACTIVE, shouldBeActive));

            te = level.getBlockEntity(pos);
            if (te instanceof ImbuementAltarBlockEntity e) e.setStack(stack, true);

            level.getChunkSource().getLightEngine().checkBlock(pos);
        }

        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof ImbuementAltarBlockEntity e) e.checkRecipe();

        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return AABB;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public int getLightBlock(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.getValue(ACTIVE) ? super.getLightBlock(state, level, pos) : 0;
    }

    @javax.annotation.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTicker(level, type, EBBlockEntities.IMBUEMENT_ALTAR.get());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ImbuementAltarBlockEntity(pos, state);
    }
}
