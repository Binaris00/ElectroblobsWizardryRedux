package com.binaris.wizardry.api.content.spell.internal;

import com.binaris.wizardry.api.content.item.ISpellCastingItem;
import com.binaris.wizardry.content.item.ScrollItem;
import com.binaris.wizardry.content.item.WandItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * <b>PlayerCastContext - Casting Context for Players</b>
 * <p>
 * Specialized context used when a player casts a spell through casting items
 * such as wands ({@link WandItem}) or scrolls
 * ({@link ScrollItem}).
 * <p>
 * In addition to the base data from {@link CastContext}, this context let you know which hand made the cast, (normally
 * main hand, but could be offhand) and provides access to the Player caster.
 *
 * @see CastContext The base class with shared information
 * @see WandItem Example of creation from wands
 * @see ScrollItem Example of creation from scrolls
 * @see ISpellCastingItem Interface for spell-casting items
 */
public class PlayerCastContext extends CastContext {
    private final InteractionHand hand;

    public PlayerCastContext(Level world, Player caster, InteractionHand hand, int castingTicks, SpellModifiers modifiers) {
        super(world, caster, castingTicks, modifiers);
        this.hand = hand;
    }

    @Override
    public Player caster() {
        return (Player) caster;
    }

    public InteractionHand hand() {
        return hand;
    }
}
