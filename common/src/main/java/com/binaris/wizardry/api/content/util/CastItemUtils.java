package com.binaris.wizardry.api.content.util;

import com.binaris.wizardry.api.content.data.SpellManagerData;
import com.binaris.wizardry.api.content.data.WizardData;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.item.IElementValue;
import com.binaris.wizardry.api.content.item.ITierValue;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.core.EBConstants;
import com.binaris.wizardry.core.config.EBConfig;
import com.binaris.wizardry.core.event.WizardryEventBus;
import com.binaris.wizardry.core.networking.s2c.SpellCastS2C;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * A utility class for handling various aspects of spell casting, including event firing, cooldown application,
 * spell tracking, and network packet sending. This class is created from {@code WandItem} and {@code ScrollItem} shared
 * casting logic to avoid code duplication.
 */
public final class CastItemUtils {
    /** Modifier applied to progression for second time tier unlocks. */
    private static final float SECOND_TIME_PROGRESSION_MODIFIER = 1.5f;

    /**
     * Fires the appropriate spell cast event based on casting ticks.
     *
     * @param source The source of the spell cast
     * @param spell  The spell being cast
     * @param ctx    The casting context
     * @return true if the event was canceled (cast should be blocked)
     */
    public static boolean fireSpellCastEvent(SpellCastEvent.Source source, Spell spell, PlayerCastContext ctx) {
        SpellCastEvent event = ctx.castingTicks() == 0
                ? new SpellCastEvent.Pre(source, spell, ctx.caster(), ctx.modifiers())
                : new SpellCastEvent.Tick(source, spell, ctx.caster(), ctx.modifiers(), ctx.castingTicks());

        return WizardryEventBus.getInstance().fire(event);
    }

    /**
     * Applies cooldown when a cast is canceled/forfeited.
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

    /**
     * Calculates the spell modifiers provided by the stack for the given spell and player.
     *
     * @param stack  The ItemStack.
     * @param player The player casting the spell.
     * @param spell  The spell being cast.
     * @return The calculated spell modifiers.
     */
    public static SpellModifiers calculateModifiers(ItemStack stack, Player player, Spell spell) {
        SpellModifiers modifiers = new SpellModifiers();

        applyModifierUpgrade(stack, modifiers, EBItems.RANGE_UPGRADE, SpellModifiers.RANGE, EBConstants.RANGE_INCREASE_PER_LEVEL, false);
        applyModifierUpgrade(stack, modifiers, EBItems.DURATION_UPGRADE, SpellModifiers.DURATION, EBConstants.DURATION_INCREASE_PER_LEVEL, false);
        applyModifierUpgrade(stack, modifiers, EBItems.BLAST_UPGRADE, SpellModifiers.BLAST, EBConstants.BLAST_RADIUS_INCREASE_PER_LEVEL, false);
        applyModifierUpgrade(stack, modifiers, EBItems.COOLDOWN_UPGRADE, SpellModifiers.COOLDOWN, EBConstants.COOLDOWN_REDUCTION_PER_LEVEL, true);

        float progressionModifier = 1.0F - ((float) Services.OBJECT_DATA.getWizardData(player).countRecentCasts(spell) / EBConstants.MAX_RECENT_SPELLS) * EBConfig.MAX_PROGRESSION_REDUCTION.get();
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);
        WizardData wizardData = Services.OBJECT_DATA.getWizardData(player);

        if (stack.getItem() instanceof IElementValue elementValue && stack.getItem() instanceof ITierValue tierValue) {
            if (elementValue.getElement() == spell.getElement()) {
                modifiers.set(SpellModifiers.POTENCY, 1.0f + (tierValue.getTier(stack).getLevel() + 1) * EBConstants.POTENCY_INCREASE_PER_TIER);
                progressionModifier *= 1.2f;
            }

            if (!wizardData.hasReachedTier(tierValue.getTier(stack).next())) progressionModifier *= SECOND_TIME_PROGRESSION_MODIFIER;
        }

        if (!data.hasSpellBeenDiscovered(spell)) progressionModifier *= 5f;


        modifiers.set(SpellModifiers.PROGRESSION, progressionModifier);
        return modifiers;
    }

    /**
     * Applies a single upgrade modifier to the given {@link SpellModifiers} if the upgrade level is greater than 0.
     * <p>
     * The resulting modifier value is {@code 1.0 + level * rate} for normal upgrades,
     * or {@code 1.0 - level * rate} for inverse upgrades (e.g. cooldown reduction).
     *
     * @param stack       The ItemStack to read the upgrade level from.
     * @param modifiers   The modifiers object to apply the upgrade to.
     * @param upgradeItem The upgrade item to check the level of.
     * @param modifier    The modifier key to set.
     * @param ratePerLevel The rate of change per upgrade level.
     * @param inverse     If {@code true}, the modifier decreases with level (e.g. cooldown reduction).
     */
    private static void applyModifierUpgrade(ItemStack stack, SpellModifiers modifiers, Supplier<? extends Item> upgradeItem, String modifier, float ratePerLevel, boolean inverse) {
        int level = CastItemDataHelper.getUpgradeLevel(stack, upgradeItem.get());
        if (level > 0) modifiers.set(modifier, 1.0f + level * (inverse ? -ratePerLevel : ratePerLevel));
    }

    /**
     * Calculates the accumulated mana cost for a continuous spell based on the casting ticks.
     *
     * @param spell       The spell being cast
     * @param castingTick The number of ticks the spell has been cast for
     * @param totalCost   The total mana cost of the spell
     * @return The accumulated mana cost for the given casting ticks
     */
    public static int getAccumulatedCastCost(Spell spell, int castingTick, int totalCost) {
        int accumulatedCost = 0;
        if (!spell.isInstantCast() && castingTick > 0) {
            int completeCycles = castingTick / 20;  // Number of complete 20-tick cycles
            int remainingTicks = castingTick % 20;  // Ticks in the partial cycle (0-19)

            // Each complete cycle applies cost twice: at tick 0 and tick 10
            // This equals totalCost per cycle (cost/2 + cost%2 + cost/2 = cost)
            accumulatedCost = completeCycles * totalCost;

            // Only add partial cycle costs if there are remaining ticks
            // (remainingTicks == 0 means we completed exactly at a cycle boundary)
            if (remainingTicks > 0) {
                accumulatedCost += totalCost / 2 + totalCost % 2;
                if (remainingTicks >= 10) accumulatedCost += totalCost / 2;
            }
        }
        return accumulatedCost;
    }

    /**
     * Calculates the distributed cost of a spell being cast over multiple ticks. The cost is halved at 10 ticks and
     * again at 20 ticks, with any remainder added to the first half.
     *
     * @param cost        The total cost of the spell.
     * @param castingTick The current tick of casting.
     * @return The distributed cost for the current tick.
     */
    public static int getDistributedCastCost(int cost, int castingTick) {
        int partialCost;

        if (castingTick % 20 == 0) partialCost = cost / 2 + cost % 2;
        else if (castingTick % 10 == 0) partialCost = cost / 2;
        else partialCost = 0;

        return partialCost;
    }

    /**
     * Calculates the progression of a spell based on its base cost and the given spell modifiers.
     * This could result in negative values! Always try to check the result before using it.
     *
     * @param spell     The spell being cast
     * @param modifiers The spell modifiers affecting the cast
     * @return The calculated progression
     */
    public static int calcCastProgression(Spell spell, SpellModifiers modifiers) {
        return (int) (spell.getCost() * modifiers.get(SpellModifiers.PROGRESSION));
    }

    /**
     * Calculates the cooldown of a spell based on its base cooldown and the given spell modifiers.
     *
     * @param spell     The spell being cast
     * @param modifiers The spell modifiers affecting the cast
     * @return The calculated cooldown
     */
    public static int calcCastCooldown(Spell spell, SpellModifiers modifiers) {
        return (int) (spell.getCooldown() * modifiers.get(SpellModifiers.COOLDOWN));
    }

    /**
     * Calculates the mana cost of a spell based on its base cost and the given spell modifiers.
     *
     * @param spell     The spell being cast
     * @param modifiers The spell modifiers affecting the cast
     * @return The calculated mana cost
     */
    public static int calcCastCost(Spell spell, SpellModifiers modifiers) {
        return (int) (spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f);
    }

    /**
     * Calculates the charge time required for a spell based on the spell's base charge and the given spell modifiers.
     *
     * @param spell     The spell being cast
     * @param modifiers The spell modifiers affecting the cast
     * @return The calculated charge time
     */
    public static int calcCharge(Spell spell, SpellModifiers modifiers) {
        return (int) (spell.getChargeUp() * modifiers.get(SpellModifiers.CHARGEUP));
    }

    private CastItemUtils() {
    }
}
