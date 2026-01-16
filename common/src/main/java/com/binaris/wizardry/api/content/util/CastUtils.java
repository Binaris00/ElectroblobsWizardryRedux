package com.binaris.wizardry.api.content.util;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.networking.s2c.SpellCastS2C;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.world.entity.player.Player;

/**
 * A utility class for handling various aspects of spell casting, including event firing, cooldown application,
 * spell tracking, and network packet sending. This class is created from {@code WandItem} and {@code ScrollItem} shared
 * casting logic to avoid code duplication.
 */
public final class CastUtils {
    /**
     * Fires the appropriate spell cast event based on casting ticks.
     *
     * @param source The source of the spell cast
     * @param spell  The spell being cast
     * @param ctx    The casting context
     * @return true if the event was cancelled (cast should be blocked)
     */
    public static boolean fireSpellCastEvent(SpellCastEvent.Source source, Spell spell, PlayerCastContext ctx) {
        SpellCastEvent event = ctx.castingTicks() == 0
                ? new SpellCastEvent.Pre(source, spell, ctx.caster(), ctx.modifiers())
                : new SpellCastEvent.Tick(source, spell, ctx.caster(), ctx.modifiers(), ctx.castingTicks());

        return WizardryEventBus.getInstance().fire(event);
    }

    /**
     * Applies cooldown when a cast is cancelled/forfeited.
     *
     * @param caster        The player casting the spell
     * @param cooldownTicks The cooldown duration in ticks
     */
    public static void applyCooldownForfeit(Player caster, int cooldownTicks) {
        caster.getCooldowns().addCooldown(caster.getUseItem().getItem(), cooldownTicks);
    }

    /**
     * Tracks the spell in wizard data for recent spell tracking.
     *
     * @param caster The player casting the spell
     * @param spell  The spell being cast
     */
    public static void trackSpellUsage(Player caster, Spell spell) {
        Services.OBJECT_DATA.getWizardData(caster).trackRecentSpell(spell, caster.level().getGameTime());
    }

    /**
     * Executes the spell cast and fires post-cast event.
     *
     * @param source The source of the spell cast
     * @param spell  The spell being cast
     * @param ctx    The casting context
     * @return true if the spell was successfully cast
     */
    public static boolean executeSpellCast(SpellCastEvent.Source source, Spell spell, PlayerCastContext ctx) {
        if (!spell.cast(ctx)) return false;

        if (ctx.castingTicks() == 0) {
            WizardryEventBus.getInstance().fire(new SpellCastEvent.Post(source, spell, ctx.caster(), ctx.modifiers()));
        }

        return true;
    }

    /**
     * Sends spell cast packet to other clients if needed (for non-instant spells).
     *
     * @param caster The player casting the spell
     * @param spell  The spell being cast
     * @param ctx    The casting context
     */
    public static void sendSpellCastPacket(Player caster, Spell spell, PlayerCastContext ctx) {
        if (!ctx.world().isClientSide && spell.requiresPacket()) {
            SpellCastS2C msg = new SpellCastS2C(caster.getId(), ctx.hand(), spell, ctx.modifiers());
            Services.NETWORK_HELPER.sendToDimension(ctx.world().getServer(), msg, ctx.world().dimension());
        }
    }

    private CastUtils() {
    }
}
