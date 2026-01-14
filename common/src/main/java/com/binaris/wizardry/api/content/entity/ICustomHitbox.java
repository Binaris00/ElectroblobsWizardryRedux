package com.binaris.wizardry.api.content.entity;

import net.minecraft.world.phys.Vec3;

@Deprecated
public interface ICustomHitbox {
    Vec3 calculateIntercept(Vec3 origin, Vec3 endpoint, float fuzziness);

    boolean contains(Vec3 point);
}

