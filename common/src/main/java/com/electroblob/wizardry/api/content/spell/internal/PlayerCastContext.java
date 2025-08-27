package com.electroblob.wizardry.api.content.spell.internal;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PlayerCastContext extends CastContext {
    private final Player caster;
    private final InteractionHand hand;

    public PlayerCastContext(Level world, Player caster, InteractionHand hand, int castingTicks, SpellModifiers modifiers) {
        super(world, castingTicks, modifiers);
        this.caster = caster;
        this.hand = hand;
    }

    @Override
    public Player caster() {
        return caster;
    }

    public InteractionHand hand() {
        return hand;
    }
}
