package com.electroblob.wizardry.content.block;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.util.GeometryUtil;
import com.electroblob.wizardry.content.blockentity.ReceptacleBlockEntity;
import com.electroblob.wizardry.api.content.item.IElementValue;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBSounds;
import com.electroblob.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ReceptacleBlock extends Block implements EntityBlock {
    protected static final VoxelShape AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);

    public ReceptacleBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.COBBLESTONE).strength(0.5F).randomTicks().lightLevel((b) -> 1).sound(SoundType.STONE));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return AABB;
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        return direction == Direction.DOWN && !this.canSurvive(state, level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof ReceptacleBlockEntity blockEntity)) return InteractionResult.PASS;

        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack stack = blockEntity.getStack();

        // If wanting to add an item to an empty receptacle
        if (stack.isEmpty() && !heldItem.isEmpty() && heldItem.getItem() instanceof IElementValue value && value.validForReceptacle()) {
            ItemStack receptacleItem = player.getAbilities().instabuild ? heldItem.copy() : heldItem;
            blockEntity.setStack(receptacleItem.split(1));
        }
        // If wanting to take an item out of a filled receptacle
        if (!stack.isEmpty() && !player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
        level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), EBSounds.BLOCK_RECEPTACLE_IGNITE.get(), SoundSource.BLOCKS, 0.7f, 0.7f, false);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean movedByPiston) {
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

        Vec3 centre = GeometryUtil.getCentre(pos);
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