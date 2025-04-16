package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.api.EBLogger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class BlockUtil {
    public static boolean canBlockBeReplaced(Level world, BlockPos pos) {
        return canBlockBeReplaced(world, pos, false);
    }

    public static Integer getNearestFloor(Level world, BlockPos pos, int range) {
        return getNearestSurface(world, pos, Direction.UP, range, true, SurfaceCriteria.COLLIDABLE);
    }

    public static boolean canBlockBeReplaced(Level world, BlockPos pos, boolean excludeLiquids) {
        return (world.isEmptyBlock(new BlockPos(pos)) || world.getBlockState(pos).canBeReplaced()) && (!excludeLiquids || !world.getBlockState(pos).liquid());
    }

    public static boolean isWaterSource(BlockState state) {
        return state.getBlock() == Blocks.WATER && (state.getBlock() == Blocks.WATER) && state.getValue(LiquidBlock.LEVEL) == 0;
    }

    public static boolean isLavaSource(BlockState state) {
        return state.getBlock() == Blocks.LAVA && (state.getBlock() == Blocks.LAVA) && state.getValue(LiquidBlock.LEVEL) == 0;
    }

    public static boolean canPlaceBlock(@Nullable Entity placer, Level world, BlockPos pos) {
        if (world.isClientSide) {
            EBLogger.error("BlockUtils#canPlaceBlock called from the client side! Blocks should be modified server-side only");
            return true;
        }

        if (!EntityUtil.canDamageBlocks(placer, world)) return false;

        if (world.isOutsideBuildHeight(pos)) return false;

        return !(placer instanceof Player) || world.mayInteract((Player) placer, pos);
    }

    @Nullable
    public static BlockPos findNearbyFloorSpace(Level world, BlockPos origin, int horizontalRange, int verticalRange, boolean lineOfSight) {
        List<BlockPos> possibleLocations = new ArrayList<>();
        final Vec3 centre = GeometryUtil.getCentre(origin);

        for (int x = -horizontalRange; x <= horizontalRange; x++) {
            for (int z = -horizontalRange; z <= horizontalRange; z++) {
                Integer y = getNearestFloor(world, origin.offset(x, 0, z), verticalRange);
                if (y != null) {
                    BlockPos location = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
                    if (lineOfSight) {
                        HitResult rayTrace = world.clip(new ClipContext(centre, GeometryUtil.getCentre(location), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, null));
                        if (rayTrace.getType() == HitResult.Type.BLOCK) continue;
                    }
                    possibleLocations.add(location);
                }
            }
        }

        if (possibleLocations.isEmpty()) {
            return null;
        } else {
            return possibleLocations.get(world.random.nextInt(possibleLocations.size()));
        }
    }

    public static boolean isBlockUnbreakable(Level world, BlockPos pos){
        return !world.isEmptyBlock(new BlockPos(pos)) && world.getBlockState(pos).isSolid();
    }

    public static List<BlockPos> getBlockSphere(BlockPos centre, double radius) {
        List<BlockPos> sphere = new ArrayList<>((int) Math.pow(radius, 3));

        for (int i = -(int) radius; i <= radius; i++) {
            float r1 = Mth.sqrt((float) (radius * radius - i * i));

            for (int j = -(int) r1; j <= r1; j++) {
                float r2 = Mth.sqrt((float) (radius * radius - i * i - j * j));

                for (int k = -(int) r2; k <= r2; k++) {
                    sphere.add(centre.offset(i, j, k));
                }
            }
        }

        return sphere;
    }

    /**
     * Returns true if the block at the given position is a tree block (or other 'solid' vegetation, such as cacti).
     * Used for structure generation.
     * @param world The world the block is in
     * @param pos The position of the block to be tested
     * @return True if the given block is a tree block, false if not.
     */
    public static boolean isTreeBlock(Level world, BlockPos pos) {
        BlockState block = world.getBlockState(pos);
        return block.is(BlockTags.LOGS) || block.getBlock() instanceof CactusBlock;
    }

    @Nullable
    public static Integer getNearestSurface(Level world, BlockPos pos, Direction direction, int range, boolean doubleSided, SurfaceCriteria criteria) {
        Integer surface = null;
        int currentBest = Integer.MAX_VALUE;

        for (int i = doubleSided ? -range : 0; i <= range && i < currentBest; i++) {
            BlockPos testPos = pos.relative(direction, i);

            if (criteria.test(world, testPos, direction)) {
                surface = (int) GeometryUtil.component(GeometryUtil.getFaceCentre(testPos, direction), direction.getAxis());
                currentBest = Math.abs(i);
            }
        }

        return surface;
    }

    public static boolean freeze(Level world, BlockPos pos, boolean freezeLava) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (isWaterSource(state)) {
            world.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
        } else if (freezeLava && isLavaSource(state)) {
            world.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
        } else if (freezeLava && (block == Blocks.LAVA)) {
            world.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState());
        } else if (canBlockBeReplaced(world, pos.above()) && Blocks.SNOW.defaultBlockState().canSurvive(world, pos.above())) {
            world.setBlockAndUpdate(pos.above(), Blocks.SNOW.defaultBlockState());
        } else {
            return false;
        }

        return true;
    }


    @FunctionalInterface
    public interface SurfaceCriteria {

        boolean test(Level world, BlockPos pos, Direction side);

        default SurfaceCriteria flip() {
            return (world, pos, side) -> this.test(world, pos.relative(side), side.getOpposite());
        }

        static SurfaceCriteria basedOn(BiPredicate<Level, BlockPos> condition) {
            return (world, pos, side) -> condition.test(world, pos) && !condition.test(world, pos.relative(side));
        }

        static SurfaceCriteria basedOn(Predicate<BlockState> condition) {
            return (world, pos, side) -> condition.test(world.getBlockState(pos)) && !condition.test(world.getBlockState(pos.relative(side)));
        }

        SurfaceCriteria COLLIDABLE = basedOn(BlockBehaviour.BlockStateBase::blocksMotion);

        /**
         * Surface criterion which defines a surface as the boundary between a block that is solid on the required side and
         * a block that is replaceable. This means the surface can be built on.
         */
        SurfaceCriteria BUILDABLE = (world, pos, side) -> world.getBlockState(pos).isFaceSturdy(world, pos, side) && world.getBlockState(pos.relative(side)).canBeReplaced();


        SurfaceCriteria SOLID_LIQUID_TO_AIR = (world, pos, side) -> (world.getBlockState(pos).liquid() || world.getBlockState(pos).isFaceSturdy(world, pos, side) && world.isEmptyBlock(pos.relative(side)));

        SurfaceCriteria NOT_AIR_TO_AIR = basedOn(Level::isEmptyBlock).flip();

        SurfaceCriteria COLLIDABLE_IGNORING_TREES = basedOn((world, pos) -> world.getBlockState(pos).blocksMotion() && !isTreeBlock(world, pos));
    }
}
