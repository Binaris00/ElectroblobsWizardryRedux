package com.electroblob.wizardry.api.content.spell.internal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class EntityCastContext extends CastContext {
    protected LivingEntity caster;
    protected InteractionHand hand;
    // What if I'm a wizard that just want to chill out casting funny spells without damaging someone?
    protected @Nullable LivingEntity target;

    public EntityCastContext(Level world, LivingEntity caster, InteractionHand hand, int castingTicks, @Nullable LivingEntity target, SpellModifiers modifiers) {
        super(world, castingTicks, modifiers);
        this.caster = caster;
        this.hand = hand;
        this.target = target;
    }


    @Override
    public LivingEntity caster() {
        return caster;
    }

    public InteractionHand hand() {
        return hand;
    }

    public @Nullable LivingEntity target() {
        return target;
    }

    public void hand(InteractionHand hand) {
        this.hand = hand;
    }

    public void target(@Nullable LivingEntity target) {
        this.target = target;
    }
}
