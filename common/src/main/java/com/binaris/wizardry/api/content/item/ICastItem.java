package com.binaris.wizardry.api.content.item;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;


/**
 * Offers a template for spell casting items to handle the base spell casting logic, cooldown, spell selection, cast
 * verification and spell casting. Implementing this interface allows you to have a better integration with EBWR Ecosystem
 * and order how the spell casting works in your item.
 *
 * @see IManaItem
 * @see com.binaris.wizardry.api.content.util.CastItemDataHelper CastItemDataHelper
 */
public interface ICastItem {

    /**
     * Normally you would call the events {@link SpellCastEvent.Pre} and {@link SpellCastEvent.Tick} in order to know
     * when to allow the spell to run, but here you also handle any other conditions to allow casting.
     *
     * @param stack The ItemStack to cast the spell from
     * @param spell The spell to cast
     * @param ctx The context of the cast
     * @return Whether the spell can be cast or not
     */
    boolean canCast(ItemStack stack, Spell spell, PlayerCastContext ctx);

    /**
     * This is where you make all the spell cast handling (normally just instant spells). For doing the continuous spells
     * you could use {@link net.minecraft.world.item.Item#onUseTick(Level, LivingEntity, ItemStack, int)}
     *
     * @param stack The ItemStack to cast the spell from
     * @param spell The spell to cast
     * @param ctx The context of the cast
     * @return Whether the spell was cast successfully or not
     */
    boolean cast(ItemStack stack, Spell spell, PlayerCastContext ctx);

    /**
     * Gets the current spell selected in the ItemStack. This is used for client-side rendering or spell selection utils.
     *
     * @param stack The ItemStack to get the current spell from
     * @return The current spell selected in the ItemStack
     */
    @NotNull
    Spell getCurrentSpell(ItemStack stack);

    /**
     * Gets the next spell in the list of spells. By default, it just returns the current spell.
     *
     * @param stack The ItemStack to get the next spell from
     * @return The next spell in the list of spells or the current spell if there is only one spell allowed
     */
    @NotNull
    default Spell getNextSpell(ItemStack stack) {
        return getCurrentSpell(stack);
    }

    /**
     * If your item will have more than just one spell loaded you need to override this in order to have a previous
     * spell on list, by default it just gets the current spell saved. Used by the Spell GUI to get the actual spell
     * icon and some client related features
     */
    @NotNull
    default Spell getPreviousSpell(ItemStack stack) {
        return getCurrentSpell(stack);
    }

    /**
     * If your item will have more than just one spell loaded you need to override this in order to have a list of spells
     * saved, by default it just sends a list with just the current spell. Used by the Spell GUI to get all the needed
     * spells to show
     */
    default Spell[] getSpells(ItemStack stack) {
        return new Spell[]{getCurrentSpell(stack)};
    }

    /**
     * Selects the next spell bound to the given ItemStack. The given ItemStack will be of this item.
     */
    default void selectNextSpell(ItemStack stack) {
    }

    /**
     * Selects the previous spell bound to the given itemstack. The given itemstack will be of this item.
     */
    default void selectPreviousSpell(ItemStack stack) {
    }

    /**
     * If your item will have more than just one spell loaded you need to override this in order to have the possibility
     * to switch between the spell list.
     */
    default boolean selectSpell(ItemStack stack, int index) {
        return false;
    }

    /**
     * Returns the current cooldown to display on the spell HUD for the given ItemStack.
     */
    default int getCurrentCooldown(ItemStack stack, Level level) {
        return 0;
    }

    /**
     * Returns the max cooldown of the current spell to display on the spell HUD for the given ItemStack.
     */
    default int getCurrentMaxCooldown(ItemStack stack) {
        return 0;
    }

    /**
     * Returns whether the spell HUD should be shown when a player is holding this item. Only called client-side.
     */
    boolean showSpellHUD(Player player, ItemStack stack);

    /**
     * Returns whether this item's spells should be displayed in the arcane workbench tooltip. Only called client-side.
     * Ignore this method if you don't want to use it on the Arcane Workbench.
     */
    default boolean showSpellsInWorkbench(Player player, ItemStack stack) {
        return true;
    }
}