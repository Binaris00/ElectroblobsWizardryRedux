package com.electroblob.wizardry.api.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public final class GeometryUtil {
    private GeometryUtil() {
    }

    public static double component(Vec3 vec, Direction.Axis axis) {
        return new double[]{vec.x, vec.y, vec.z}[axis.ordinal()];
    }

    public static Vec3 getCentre(BlockPos pos) {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0.5, 0.5);
    }

    public static Vec3 getFaceCentre(BlockPos pos, Direction face) {
        return getCentre(pos).add(new Vec3(face.step()).scale(0.5));
    }
}
