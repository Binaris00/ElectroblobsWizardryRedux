package com.binaris.wizardry.api.content.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Utility methods for {@link Vec3} operations not covered by vanilla Minecraft.
 */
public final class VecUtils {

    /**
     * Projects the given vector onto the horizontal (XZ) plane and normalizes the result. The Y component is zeroed out
     * before normalizing, making this useful for calculating horizontal facing directions regardless of vertical look angle.
     *
     * @param vec the input vector
     * @return a normalized horizontal vector with Y = 0
     */
    public static Vec3 flattenToHorizontal(Vec3 vec) {
        return replaceComponent(vec, Direction.Axis.Y, 0).normalize();
    }

    /**
     * Returns a copy of the given vector with one axis component replaced by a new value. Since {@link Vec3} is immutable,
     * this constructs a new instance.
     *
     * @param vec      the source vector
     * @param axis     the axis whose component should be replaced
     * @param newValue the new value for that component
     * @return a new {@link Vec3} with the specified component replaced
     */
    public static Vec3 replaceComponent(Vec3 vec, Direction.Axis axis, double newValue) {
        double[] components = {vec.x, vec.y, vec.z};
        components[axis.ordinal()] = newValue;
        return new Vec3(components[0], components[1], components[2]);
    }

    /**
     * Returns the world-space center point of the given face of a block.
     * Equivalent to the block's center ({@link Vec3#atCenterOf}) offset by half a block
     * in the direction of the face.
     *
     * @param pos  the block position
     * @param face the face of the block
     * @return the center point of the specified face as a {@link Vec3}
     */
    public static Vec3 getFaceCentre(BlockPos pos, Direction face) {
        return Vec3.atCenterOf(pos).add(new Vec3(face.step()).scale(0.5));
    }

    /**
     * Returns all 8 corner vertices of the given {@link AABB}, ordered
     * bottom face first (Y = minY), then top face (Y = maxY), both in
     * counter-clockwise winding order when viewed from below.
     *
     * <pre>
     *   4 --- 5
     *   |     |   top (maxY)
     *   7 --- 6
     *
     *   0 --- 1
     *   |     |   bottom (minY)
     *   3 --- 2
     * </pre>
     *
     * @param box the axis-aligned bounding box
     * @return an array of 8 {@link Vec3} vertices
     */
    public static Vec3[] getVertices(AABB box) {
        return new Vec3[]{
                new Vec3(box.minX, box.minY, box.minZ),
                new Vec3(box.maxX, box.minY, box.minZ),
                new Vec3(box.maxX, box.minY, box.maxZ),
                new Vec3(box.minX, box.minY, box.maxZ),
                new Vec3(box.minX, box.maxY, box.minZ),
                new Vec3(box.maxX, box.maxY, box.minZ),
                new Vec3(box.maxX, box.maxY, box.maxZ),
                new Vec3(box.minX, box.maxY, box.maxZ)
        };
    }

    private VecUtils() {
    }
}