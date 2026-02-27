package com.binaris.wizardry.api.content.util;

import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@SuppressWarnings("deprecation") // Most block methods are marked deprecated?!?!
public final class BlockUtil {

    /**
     * Checks whether a block at the given position in the given level can be replaced.
     *
     * @param world The level
     * @param pos   The position to check
     * @return True if the block can be replaced, false otherwise
     */
    public static boolean canBlockBeReplaced(Level world, BlockPos pos) {
        return canBlockBeReplaced(world, pos, false);
    }

    /**
     * Finds the nearest floor level (a surface that can be stood on) from a given position within a given range.
     *
     * @param world The level
     * @param pos   The position
     * @param range The maximum range to search
     * @return The Y coordinate of the nearest floor level, or null if none was found
     */
    public static Integer getNearestFloor(Level world, BlockPos pos, int range) {
        return getNearestSurface(world, pos, Direction.UP, range, true, SurfaceCriteria.COLLIDABLE);
    }

    /**
     * Checks whether a block at the given position in the given level can be replaced.
     *
     * @param world          The level
     * @param pos            The position to check
     * @param excludeLiquids Whether to exclude liquids from being considered replaceable
     * @return True if the block can be replaced, false otherwise
     */
    public static boolean canBlockBeReplaced(Level world, BlockPos pos, boolean excludeLiquids) {
        return (world.isEmptyBlock(new BlockPos(pos)) || world.getBlockState(pos).canBeReplaced()) && (!excludeLiquids || !world.getBlockState(pos).liquid());
    }

    /**
     * Checks whether the given player can break the block at the given position in the given level following these rules:
     * <ul>
     *     <li>If the block break event is cancelled (called on each loader implementation), the block cannot be broken.</li>
     *     <li>If the player is in creative mode, they can break the block.</li>
     *     <li>If the player does not have build permissions, they cannot break the block.</li>
     *     <li>If the block is outside the build height, it cannot be broken.</li>
     *     <li>If the block is unbreakable (destroy speed is less than 0 like bedrock), it cannot be broken.</li>
     *     <li>If the player is in spectator mode, they cannot break the block.</li>
     *     <li>If the block's destroy speed is greater than 3.0f and the 'powerful' flag is false, it cannot be broken.</li>
     *     <li>Finally, if none of the above prevent it, the player's ability to interact with the block is checked.</li>
     * </ul>
     *
     * @param player   The player attempting to break the block
     * @param level    The level the block is in
     * @param pos      The position of the block to be broken
     * @param powerful Whether to ignore block hardness (e.g. for powerful spells)
     * @return True if the player can break the block, false otherwise
     */
    public static boolean canBreak(Player player, Level level, BlockPos pos, boolean powerful) {
        if (level.isClientSide) {
            EBLogger.error("BlockUtils#canBreak called from the client side! Blocks should be modified server-side only");
            return true;
        }

        if (Services.PLATFORM.firePlayerBlockBreakEvent(level, pos, player)) return false;
        if (player.isCreative()) return true;
        if (!player.mayBuild()) return false;
        if (level.isOutsideBuildHeight(pos)) return false;

        BlockState state = level.getBlockState(pos);
        if (state.getDestroySpeed(level, pos) < 0) return false; // unbreakable block
        if (player.isSpectator()) return false;

        // strength:
        // 3 are normally small and easy to break
        // 1.5 or less are very easy to break (sand, dirt, wood, etc)
        // mayor of 3 are hard to break (deepslate, stone, ores, etc)
        if (state.getDestroySpeed(level, pos) > 3.0f && !powerful) {
            return false;
        } else return level.mayInteract(player, pos);
    }

    /**
     * Checks whether the given mob can break the block at the given position in the given level following these rules:
     * <ul>
     *     <li>If the block break event is cancelled (called on each loader implementation), the block cannot be broken.</li>
     *     <li>If the block is outside the build height, it cannot be broken.</li>
     *     <li>If the block is unbreakable (destroy speed is less than 0 like bedrock), it cannot be broken.</li>
     *     <li>If the block's destroy speed is greater than 3.0f, it cannot be broken.</li>
     * </ul>
     *
     * @param mob   The mob attempting to break the block
     * @param level The level the block is in
     * @param pos   The position of the block to be broken
     * @return True if the mob can break the block, false otherwise
     */
    public static boolean canBreak(Mob mob, Level level, BlockPos pos) {
        if (level.isClientSide) {
            EBLogger.error("BlockUtils#canBreak called from the client side! Blocks should be modified server-side only");
            return true;
        }

        if (Services.PLATFORM.fireMobBlockBreakEvent(level, pos, mob)) return false;
        if (level.isOutsideBuildHeight(pos)) return false;

        BlockState state = level.getBlockState(pos);
        if (state.getDestroySpeed(level, pos) < 0) return false; // unbreakable block

        return !(state.getDestroySpeed(level, pos) > 3.0f);
    }

    /**
     * Used because {@code Direction#BY_2D_DATA} is private access only.
     */
    public static Direction[] getHorizontals() {
        return Arrays.stream(Direction.values()).filter((direction) -> direction.getAxis().isHorizontal()).sorted(Comparator.comparingInt(Direction::get2DDataValue)).toArray(Direction[]::new);
    }

    /**
     * Checks whether the given entity can place a block at the given position in the given level following these rules:
     * <ul>
     *     <li>If the entity cannot damage blocks, it cannot place the block.</li>
     *     <li>If the block is outside the build height, it cannot be placed.</li>
     *     <li>Finally, if the entity is a player, their ability to interact with the block is checked.</li>
     * </ul>
     *
     * @param placer The entity attempting to place the block
     * @param world  The level the block is in
     * @param pos    The position of the block to be placed
     * @return True if the entity can place the block, false otherwise
     */
    public static boolean canPlaceBlock(LivingEntity placer, Level world, BlockPos pos) {
        if (world.isClientSide) {
            EBLogger.error("BlockUtils#canPlaceBlock called from the client side! Blocks should be modified server-side only");
            return true;
        }

        if (!EntityUtil.canDamageBlocks(placer, world)) return false;

        if (world.isOutsideBuildHeight(pos)) return false;

        return !(placer instanceof Player) || world.mayInteract((Player) placer, pos);
    }

    /**
     * Finds a random nearby floor space within the given horizontal and vertical range of the given entity's position.
     * A floor space is defined as a position where there is a solid block below and two air blocks above.
     *
     * @param entity          The entity
     * @param horizontalRange The horizontal range to search
     * @param verticalRange   The vertical range to search
     * @return A random nearby floor space, or null if none was found
     */
    @Nullable
    public static BlockPos findNearbyFloorSpace(Entity entity, int horizontalRange, int verticalRange) {
        Level world = entity.level();
        BlockPos origin = entity.blockPosition();
        return findNearbyFloorSpace(world, origin, horizontalRange, verticalRange, true, entity);
    }

    /**
     * Finds a random nearby floor space within the given horizontal and vertical range of the given origin position.
     * A floor space is defined as a position where there is a solid block below and two air blocks above.
     *
     * @param world           The level
     * @param origin          The origin position
     * @param horizontalRange The horizontal range to search
     * @param verticalRange   The vertical range to search
     * @param lineOfSight     Whether to require line of sight from the origin to the found position
     * @param entity          The entity for line of sight calculations
     * @return A random nearby floor space, or null if none was found
     */
    @Nullable
    public static BlockPos findNearbyFloorSpace(Level world, BlockPos origin, int horizontalRange, int verticalRange, boolean lineOfSight, Entity entity) {
        List<BlockPos> possibleLocations = new ArrayList<>();
        final Vec3 centre = GeometryUtil.getCentre(origin);

        for (int x = -horizontalRange; x <= horizontalRange; x++) {
            for (int z = -horizontalRange; z <= horizontalRange; z++) {
                Integer y = getNearestFloor(world, origin.offset(x, 0, z), verticalRange);
                if (y != null) {
                    BlockPos location = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
                    if (lineOfSight) {
                        HitResult rayTrace = world.clip(new ClipContext(centre, GeometryUtil.getCentre(location), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, entity));
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

    /**
     * Finds a random nearby floor space within the given horizontal and vertical range of the given origin position.
     * A floor space is defined as a position where there is a solid block below and two air blocks above.
     *
     * @param world           The level
     * @param origin          The origin position
     * @param horizontalRange The horizontal range to search
     * @param verticalRange   The vertical range to search
     * @param lineOfSight     Whether to require line of sight from the origin to the found position
     * @return A random nearby floor space, or null if none was found
     */
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

    /**
     * Checks whether the block at the given position in the given level is unbreakable (i.e. bedrock or air).
     *
     * @param world The level
     * @param pos   The position to check
     * @return True if the block is unbreakable, false otherwise
     */
    public static boolean isBlockUnbreakable(Level world, BlockPos pos) {
        return !world.isEmptyBlock(new BlockPos(pos)) && world.getBlockState(pos).isSolid();
    }

    /**
     * Returns a list of BlockPos in a sphere around a centre position.
     *
     * @param centre The centre position
     * @param radius The radius of the sphere
     * @return A list of BlockPos in a sphere around the centre position
     */
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
     * Finds the nearest surface from a given position in a given direction within a given range that meets the given
     * criteria.
     *
     * @param world       The world
     * @param pos         The starting position
     * @param direction   The direction to search in
     * @param range       The maximum range to search
     * @param doubleSided Whether to search in both directions along the given axis
     * @param criteria    The criteria that defines a surface
     * @return The coordinate of the nearest surface along the given axis, or null if none was found
     */
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

    /**
     * Freezes water and lava source blocks, converts flowing lava to cobblestone, and adds a layer of snow on top of
     * blocks that can support it.
     *
     * @param world      The world
     * @param pos        The position to freeze
     * @param freezeLava Whether to freeze lava as well as water
     * @return True if a block was changed, false otherwise
     */
    public static boolean freeze(Level world, BlockPos pos, boolean freezeLava) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // is water
        if (state.getBlock() == Blocks.WATER && state.getValue(LiquidBlock.LEVEL) == 0) {
            world.setBlockAndUpdate(pos, Blocks.ICE.defaultBlockState());
        }
        // is lava
        else if (freezeLava && state.getBlock() == Blocks.LAVA && state.getValue(LiquidBlock.LEVEL) == 0) {
            world.setBlockAndUpdate(pos, Blocks.OBSIDIAN.defaultBlockState());
        }
        // is flowing lava (we don't want to convert all lava levels to obsidian, just the source blocks)
        else if (freezeLava && (block == Blocks.LAVA)) {
            world.setBlockAndUpdate(pos, Blocks.COBBLESTONE.defaultBlockState());
        }
        // is snow and can grow a layer
        else if (canBlockBeReplaced(world, pos.above()) && Blocks.SNOW.defaultBlockState().canSurvive(world, pos.above())) {
            world.setBlockAndUpdate(pos.above(), Blocks.SNOW.defaultBlockState());
        } else {
            return false;
        }

        return true;
    }

    /**
     * Checks whether a block at the given position in the given level is passable (i.e. can be moved through).
     *
     * @param world    The level
     * @param checkPos The position to check
     * @return True if the block is passable, false otherwise
     */
    public static boolean isBlockPassable(Level world, BlockPos checkPos) {
        return world.isEmptyBlock(checkPos) || world.getBlockState(checkPos).canBeReplaced();
    }


    /**
     * A {@code SurfaceCriteria} object is used to define a 'surface', a boundary between two blocks which differ in
     * some way, for use in {@link BlockUtil#getNearestSurface(Level, BlockPos, Direction, int, boolean, SurfaceCriteria)}.
     * This provides a more flexible replacement for the old {@code getNearestFloorLevel} methods.<br>
     * <p>
     * <i>In the context of this class, 'outside' refers to the side of the surface that is in the supplied direction,
     * and 'inside' refers to the side which is in the opposite direction. For example, if the direction is {@code UP},
     * the inside of the surface is defined as below it, and the outside is defined as above it.</i>
     */
    @FunctionalInterface
    public interface SurfaceCriteria {
        /**
         * Surface criterion which defines a surface as the boundary between a block that cannot be moved through and
         * a block that can be moved through. This means the surface can be stood on.
         */
        SurfaceCriteria COLLIDABLE = basedOn(BlockBehaviour.BlockStateBase::blocksMotion);
        /**
         * Surface criterion which defines a surface as the boundary between a block that is solid on the required side and
         * a block that is replaceable. This means the surface can be built on.
         */
        SurfaceCriteria BUILDABLE = (world, pos, side) -> world.getBlockState(pos).isFaceSturdy(world, pos, side) && world.getBlockState(pos.relative(side)).canBeReplaced();

        /**
         * Surface criterion which defines a surface as the boundary between a block that is solid on the required side
         * or a liquid, and an air block. Used for freezing water and placing snow.
         */
        SurfaceCriteria SOLID_LIQUID_TO_AIR = (world, pos, side) -> (world.getBlockState(pos).liquid() || world.getBlockState(pos).isFaceSturdy(world, pos, side) && world.isEmptyBlock(pos.relative(side)));

        /**
         * Surface criterion which defines a surface as the boundary between any non-air block and an air block.
         * Used for particles, and is also good for placing fire.
         */
        SurfaceCriteria NOT_AIR_TO_AIR = basedOn(Level::isEmptyBlock).flip();

        /**
         * Returns a {@code SurfaceCriteria} based on the given condition, where the inside of the surface satisfies
         * the condition and the outside does not.
         */
        static SurfaceCriteria basedOn(BiPredicate<Level, BlockPos> condition) {
            return (world, pos, side) -> condition.test(world, pos) && !condition.test(world, pos.relative(side));
        }

        /**
         * Returns a {@code SurfaceCriteria} based on the given condition, where the inside of the surface satisfies
         * the condition and the outside does not.
         */
        static SurfaceCriteria basedOn(Predicate<BlockState> condition) {
            return (world, pos, side) -> condition.test(world.getBlockState(pos)) && !condition.test(world.getBlockState(pos.relative(side)));
        }

        /**
         * Tests whether the inputs define a valid surface according to this set of criteria.
         *
         * @param world The world in which the surface is to be tested.
         * @param pos   The block coordinates of the inside ('solid' part) of the surface.
         * @param side  The direction in which the surface must face.
         * @return True if the side {@code side} of the block at {@code pos} in {@code world} is a valid surface
         * according to this set of criteria, false otherwise.
         */
        boolean test(Level world, BlockPos pos, Direction side);

        /** Returns a {@code SurfaceCriteria} with the opposite arrangement to this one. */
        default SurfaceCriteria flip() {
            return (world, pos, side) -> this.test(world, pos.relative(side), side.getOpposite());
        }
    }
}
