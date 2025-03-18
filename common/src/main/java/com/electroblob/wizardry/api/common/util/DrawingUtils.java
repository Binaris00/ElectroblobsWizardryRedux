package com.electroblob.wizardry.api.common.util;

import net.minecraft.util.Mth;

public class DrawingUtils {

    public static float smoothScaleFactor(int lifetime, int ticksExisted, float partialTicks, int startLength, int endLength) {
        float age = ticksExisted + partialTicks;
        float s = Mth.clamp(age < startLength || lifetime < 0 ? age / startLength : (lifetime - age) / endLength, 0, 1);
        s = (float) Math.pow(s, 0.4);
        return s;
    }

    public static int mix(int colour1, int colour2, float proportion) {
        proportion = Mth.clamp(proportion, 0, 1);

        int r1 = colour1 >> 16 & 255;
        int g1 = colour1 >> 8 & 255;
        int b1 = colour1 & 255;
        int r2 = colour2 >> 16 & 255;
        int g2 = colour2 >> 8 & 255;
        int b2 = colour2 & 255;

        int r = (int) (r1 + (r2 - r1) * proportion);
        int g = (int) (g1 + (g2 - g1) * proportion);
        int b = (int) (b1 + (b2 - b1) * proportion);

        return (r << 16) + (g << 8) + b;
    }
}
