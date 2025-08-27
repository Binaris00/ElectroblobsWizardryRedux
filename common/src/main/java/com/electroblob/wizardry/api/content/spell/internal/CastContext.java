package com.electroblob.wizardry.api.content.spell.internal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class CastContext {
    protected Level world;
    protected int castingTicks;
    protected SpellModifiers modifiers;

    public CastContext(Level world, int castingTicks, SpellModifiers modifiers) {
        this.world = world;
        this.castingTicks = castingTicks;
        this.modifiers = modifiers;
    }

    public abstract LivingEntity caster();

    public Level world() {
        return world;
    }

    public int castingTicks() {
        return castingTicks;
    }

    public SpellModifiers modifiers() {
        return modifiers;
    }

    public void castingTicks(int tick) {
        this.castingTicks = tick;
    }

    public void modifiers(SpellModifiers modifiers) {
        this.modifiers = modifiers;
    }
}
