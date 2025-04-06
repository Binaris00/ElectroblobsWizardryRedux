package com.electroblob.wizardry.api.content.spell.internal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class CastContext {
    protected Level world;
    protected int ticksInUse;
    protected SpellModifiers modifiers;

    public CastContext(Level world, int ticksInUse, SpellModifiers modifiers) {
        this.world = world;
        this.ticksInUse = ticksInUse;
        this.modifiers = modifiers;
    }

    public abstract LivingEntity caster();

    public Level world() {
        return world;
    }

    public int ticksInUse() {
        return ticksInUse;
    }

    public SpellModifiers modifiers() {
        return modifiers;
    }

    public void ticksInUse(int tick){
        this.ticksInUse = tick;
    }

    public void modifiers(SpellModifiers modifiers) {
        this.modifiers = modifiers;
    }
}
