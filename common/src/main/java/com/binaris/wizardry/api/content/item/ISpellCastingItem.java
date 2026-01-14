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
 * This helps items to hold and cast spells that could be consumables (like scrolls) or they may be durability-based
 * (like wands), it could also handle custom behaviours outside the mod like items that are special for certain spells,
 * unlock new modifiers, exclusive effects and much more. Custom spell casting items should implement this to integrate
 * properly with Electroblob's Wizardry Ecosystem. <br><br>
 * Used for the following:
 * <ul>
 *     <li>Display of the arcane workbench tooltip (in conjunction with {@link IManaStoringItem})</li>
 *     <li>Supplying information to the spell HUD</li>
 *     <li>Spell switching controls for different spells (Controlled by packets by main mod, don't worry)</li>
 * </ul>
 */
public interface ISpellCastingItem {

    /**
     * The items implementing this interface are responsible about how to use this. Normally you would call the events
     * {@link SpellCastEvent.Pre} and {@link SpellCastEvent.Tick} in order to know when to allow the spell to run. <br><br>
     * <p>
     * You could also add some custom to logic (like special cooldowns or handling specific spells) if you want to.
     */
    boolean canCast(ItemStack stack, Spell spell, PlayerCastContext ctx);

    /**
     * The items implementing this interface are responsible about how to use this. This is where you make all the spell cast handling
     * (normally just instant spells). For doing the continuous spells you should use {@link net.minecraft.world.item.Item#onUseTick(Level, LivingEntity, ItemStack, int)}
     */
    boolean cast(ItemStack stack, Spell spell, PlayerCastContext ctx);

    /**
     * This gets the actual item that's on the ItemStack, normally you won't use this. Used by the Spell GUI to get
     * the actual spell icon and some client related features
     */
    @NotNull
    Spell getCurrentSpell(ItemStack stack);

    /**
     * If your item will have more than just one spell loaded you need to override this in order to have a next spell
     * on list, by default it just gets the current spell saved. Used by the Spell GUI to get the actual spell icon and
     * some client related features
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
