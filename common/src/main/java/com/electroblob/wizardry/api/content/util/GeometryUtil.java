package com.electroblob.wizardry.api.content.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public final class GeometryUtil {
    public static final double ANTI_Z_FIGHTING_OFFSET = 0.005;

    private GeometryUtil() {
    }

    public static double component(Vec3 vec, Direction.Axis axis) {
        return new double[]{vec.x, vec.y, vec.z}[axis.ordinal()];
    }

    public static Vec3 getCentre(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0.5, 0.5);
    }

    public static Vec3 horizontalise(Vec3 vec) {
        return replaceComponent(vec, Direction.Axis.Y, 0).normalize();
    }

    public static Vec3 replaceComponent(Vec3 vec, Direction.Axis axis, double newValue) {
        double[] components = {vec.x, vec.y, vec.z};
        components[axis.ordinal()] = newValue;
        return new Vec3(components[0], components[1], components[2]);
    }

    public static Vec3 getFaceCentre(BlockPos pos, Direction face) {
        return getCentre(pos).add(new Vec3(face.step()).scale(0.5));
    }

    public static float getPitch(Direction facing) {
        return facing == Direction.UP ? 90 : facing == Direction.DOWN ? -90 : 0;
    }
}
