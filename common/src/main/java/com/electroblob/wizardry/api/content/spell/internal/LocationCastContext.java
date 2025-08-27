package com.electroblob.wizardry.api.content.spell.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LocationCastContext extends CastContext{
    protected Vec3 vec3;
    protected Direction direction;
    protected int duration;

    public LocationCastContext(Level world, double x, double y, double z, Direction direction, int castingTicks, int duration, SpellModifiers modifiers) {
        super(world, castingTicks, modifiers);
        this.vec3 = new Vec3(x, y, z);
        this.direction = direction;
        this.duration = duration;
    }

    @Override
    public LivingEntity caster() {
        return null;
    }

    public Vec3 vec3(){
        return vec3;
    }

    public BlockPos pos(){
        return BlockPos.containing(vec3);
    }

    public double x() {
        return vec3.x;
    }

    public double y() {
        return vec3.y;
    }

    public double z() {
        return vec3.z;
    }

    public Direction direction() {
        return direction;
    }

    public int duration() {
        return duration;
    }

    public void vec3(Vec3 vec3) {
        this.vec3 = vec3;
    }

    public void direction(Direction direction) {
        this.direction = direction;
    }

    public void duration(int duration) {
        this.duration = duration;
    }
}
