package com.binaris.wizardry.api.content.util;

import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.api.content.item.ITierValue;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellContext;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.core.EBConfig;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Utility class for workbench-related operations, such as recharging mana and applying spell books to wands. This is
 * saved as a separate class to have a clear separation between workbench logic and actual workbench block/menu implementations
 * and also give the possibility for other workbench-like blocks/menus to reuse this logic on addons/mods.
 */
public final class WorkbenchUtils {

    /**
     * Recharges the mana of the item in the centre slot using the crystals in the crystals slot.
     *
     * @param centre   The slot containing the item to be recharged
     * @param crystals The slot containing the mana crystals
     * @return True if the item was recharged, false otherwise
     */
    public static boolean rechargeManaFromCrystals(Slot centre, Slot crystals) {
        ItemStack stack = centre.getItem();
        if (!(stack.getItem() instanceof IManaStoringItem manaItem)) return false;
        if (!crystals.hasItem() || manaItem.isManaFull(centre.getItem())) return false;

        int chargeDepleted = manaItem.getManaCapacity(centre.getItem()) - manaItem.getMana(centre.getItem());
        int manaPerItem = getManaValuePerCrystal(crystals.getItem());
        int totalAvailableMana = crystals.getItem().getCount() * manaPerItem;

        if (totalAvailableMana < chargeDepleted) {
            manaItem.rechargeMana(centre.getItem(), totalAvailableMana);
            crystals.remove(crystals.getItem().getCount());
        } else {
            manaItem.setMana(centre.getItem(), manaItem.getManaCapacity(centre.getItem()));
            crystals.remove((int) Math.ceil((double) chargeDepleted / manaPerItem));
        }

        return true;
    }

    /**
     * Returns the amount of mana contained in a single crystal item.
     *
     * @param crystal The crystal item stack
     * @return The amount of mana contained in a single crystal item
     */
    public static int getManaValuePerCrystal(ItemStack crystal) {
        if (crystal.getItem() instanceof IManaStoringItem manaItem) return manaItem.getMana(crystal);
        return 0;
    }

    /**
     * Applies the spell books in the given slots to the wand in the centre slot.
     *
     * @param centre     The slot containing the wand
     * @param spellBooks The slots containing the spell books
     * @param ctx        The spell context
     * @return True if any spells were bound, false otherwise
     */
    public static boolean applySpellBooks(Slot centre, Slot[] spellBooks, SpellContext ctx) {
        List<Spell> spells = WandHelper.getSpells(centre.getItem());
        boolean changed = false;
        SpellTier origin = centre.getItem().getItem() instanceof ITierValue tierItem
                ? tierItem.getTier(centre.getItem())
                : SpellTiers.NOVICE;

        for (int i = 0; i < spells.size(); i++) {
            if (!spellBooks[i].hasItem()) continue;

            Spell spell = SpellUtil.getSpell(spellBooks[i].getItem());
            if (!canBindSpell(spell, spells, origin, i, ctx)) continue;

            updateSpellSlot(centre.getItem(), spells, i, spell);
            if (EBConfig.singleUseSpellBooks) {
                spellBooks[i].getItem().shrink(1);
            }
            changed = true;
        }

        if (changed) WandHelper.setSpells(centre.getItem(), spells);
        return changed;
    }

    /**
     * Determines whether the given spell can be bound to the specified slot on a wand of the given tier.
     *
     * @param spell  The spell to check
     * @param spells The list of spells currently bound to the wand
     * @param origin The tier of the wand
     * @param slot   The slot to which the spell would be bound
     * @param ctx    The spell context
     * @return True if the spell can be bound, false otherwise
     */
    public static boolean canBindSpell(Spell spell, List<Spell> spells, SpellTier origin, int slot, SpellContext ctx) {
        return spell.getTier().level <= origin.level
                && spells.get(slot) != spell
                && spell.isEnabled(ctx)
                && (!EBConfig.preventBindingSameSpellTwiceToWands || spells.stream().noneMatch(s -> s == spell));
    }

    /**
     * Updates the spell in the given slot, taking care to preserve the currently selected spell if necessary.
     *
     * @param wand   The wand item stack
     * @param spells The list of spells on the wand
     * @param slot   The slot to update
     * @param spell  The new spell to set
     */
    public static void updateSpellSlot(ItemStack wand, List<Spell> spells, int slot, Spell spell) {
        int currentSelectedIndex = spells.indexOf(WandHelper.getCurrentSpell(wand));
        if (currentSelectedIndex == slot) WandHelper.setCurrentSpell(wand, spell);
        spells.set(slot, spell);
    }

    private WorkbenchUtils() {
    }
}
