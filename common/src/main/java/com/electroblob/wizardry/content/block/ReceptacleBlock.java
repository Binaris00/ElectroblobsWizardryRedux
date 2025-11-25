package com.electroblob.wizardry.content.block;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.util.GeometryUtil;
import com.electroblob.wizardry.content.blockentity.ReceptacleBlockEntity;
import com.electroblob.wizardry.content.item.ReceptacleItemValue;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class ReceptacleBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ON_WALL = BooleanProperty.create("on_wall");
    protected static final VoxelShape FLOOR_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    private static final double WALL_PARTICLE_OFFSET = 3 / 16d;
    private static final Map<Direction, VoxelShape> WALL_AABBS = Maps.newEnumMap(ImmutableMap.of(
            Direction.NORTH, Block.box(5.5D, 3.0D, 11.0D, 10.5D, 13.0D, 16.0D),
            Direction.SOUTH, Block.box(5.5D, 3.0D, 0.0D, 10.5D, 13.0D, 5.0D),
            Direction.WEST, Block.box(11.0D, 3.0D, 5.5D, 16.0D, 13.0D, 10.5D),
            Direction.EAST, Block.box(0.0D, 3.0D, 5.5D, 5.0D, 13.0D, 10.5D)));

    public ReceptacleBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.STONE).randomTicks().strength(0).lightLevel((b) -> 1).sound(SoundType.STONE));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ON_WALL, false));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return state.getValue(ON_WALL) ? WALL_AABBS.get(state.getValue(FACING)) : FLOOR_AABB;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState();
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction[] directions = context.getNearestLookingDirections();

        // Try to place on a wall first
        for (Direction direction : directions) {
            if (direction.getAxis().isHorizontal()) {
                Direction opposite = direction.getOpposite();
                blockstate = blockstate.setValue(FACING, opposite).setValue(ON_WALL, true);
                if (blockstate.canSurvive(level, pos)) {
                    return blockstate;
                }
            }
        }

        // If that fails, try to place on the floor
        blockstate = blockstate.setValue(ON_WALL, false);
        if (blockstate.canSurvive(level, pos)) {
            return blockstate;
        }

        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        if (!state.getValue(ON_WALL)) return canSupportCenter(level, pos.below(), Direction.UP);

        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.getBlock() instanceof ImbuementAltarBlock) return direction.getAxis().isHorizontal();
        return direction.getAxis().isHorizontal() && blockState.isFaceSturdy(level, blockPos, direction);
    }

    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (state.getValue(ON_WALL)) {
            return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
        }
        return direction == Direction.DOWN && !this.canSurvive(state, level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof ReceptacleBlockEntity blockEntity)) return InteractionResult.PASS;

        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack stack = blockEntity.getStack();

        // If wanting to add an item to an empty receptacle
        if (stack.isEmpty() && !heldItem.isEmpty() && heldItem.getItem() instanceof ReceptacleItemValue) {
            if (!level.isClientSide) {
                ItemStack receptacleItem = player.getAbilities().instabuild ? heldItem.copy() : heldItem;
                blockEntity.setStack(receptacleItem.split(1));
                level.playSound(null, pos, EBSounds.BLOCK_RECEPTACLE_IGNITE.get(), SoundSource.BLOCKS, 0.7f, 0.7f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        // If wanting to take an item out of a filled receptacle
        if (!stack.isEmpty()) {
            if (!level.isClientSide) {
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                blockEntity.setStack(ItemStack.EMPTY);
                level.playSound(null, pos, EBSounds.BLOCK_RECEPTACLE_IGNITE.get(), SoundSource.BLOCKS, 0.7f, 0.7f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof ReceptacleBlockEntity entity) {
            ItemStack stack = entity.getStack();
            if (!stack.isEmpty()) Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void animateTick(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof ReceptacleBlockEntity entity)) return;
        Element element = entity.getElement();
        if (element == null) return;

        Direction facing = state.getValue(FACING).getOpposite();
        Vec3 centre = GeometryUtil.getCentre(pos);
        if (facing.getAxis().isHorizontal() && state.getValue(ON_WALL))
            centre = centre.add(new Vec3(facing.step()).scale(WALL_PARTICLE_OFFSET)).add(0, 0.125, 0);
        int[] colors = element.getColors();
        ParticleBuilder.create(EBParticles.FLASH).pos(centre).scale(0.35f).time(48).color(colors[0]).spawn(level);

        double r = 0.12;

        for (int i = 0; i < 3; i++) {
            double x = r * (random.nextDouble() * 2 - 1);
            double y = r * (random.nextDouble() * 2 - 1);
            double z = r * (random.nextDouble() * 2 - 1);
            ParticleBuilder.create(EBParticles.DUST).pos(centre.x + x, centre.y + y, centre.z + z).velocity(x * -0.03, 0.02, z * -0.03).time(24 + random.nextInt(8)).color(colors[1]).fade(colors[2]).spawn(level);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ON_WALL);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ReceptacleBlockEntity(pos, state);
    }

    @Override
    public int getLightBlock(@NotNull BlockState state, BlockGetter level, @NotNull BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof ReceptacleBlockEntity blockEntity) {
            Element element = blockEntity.getElement();
            return element == null ? 0 : super.getLightBlock(state, level, pos);
        }
        return 0;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof ReceptacleBlockEntity blockEntity) {
            Element element = blockEntity.getElement();
            return element == null ? 0 : Services.REGISTRY_UTIL.getElements().stream().toList().indexOf(element) + 1;
        }
        return super.getAnalogOutputSignal(state, level, pos);
    }
}