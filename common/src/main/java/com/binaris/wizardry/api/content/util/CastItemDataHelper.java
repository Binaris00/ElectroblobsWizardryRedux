package com.binaris.wizardry.api.content.util;

import com.binaris.wizardry.api.content.item.IWorkbenchItem;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.Spells;
import com.binaris.wizardry.setup.registries.WandUpgrades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class avoids big classes just to handle NBT data for getting/saving values.
 * <p>
 * Data is stored in the NBT as follows:
 * <ul>
 *     <li>A list of spells under the key {@link #SPELL_ARRAY_KEY}, stored as a list of string {@code ResourceLocations}s.</li>
 *     <li>The currently selected spell index under the key {@link #SELECTED_SPELL_KEY}, stored as an integer.</li>
 *     <li>An array of longs under the key {@link #COOLDOWN_END_TIME_ARRAY_KEY}, storing the game time when each spell's cooldown ends.</li>
 *     <li>An array of integers under the key {@link #MAX_COOLDOWN_ARRAY_KEY}, storing the maximum cooldown for each spell slot.</li>
 *     <li>A compound tag under the key {@link #UPGRADES_KEY}, storing the levels of each upgrade.</li>
 *     <li>An integer under the key {@link #PROGRESSION_KEY}, storing the item's progression level.</li>
 * </ul>
 */
public final class CastItemDataHelper {
    /** The NBT key used to store the array of spells on the item in form of {@code ResourceLocation}. */
    public static final String SPELL_ARRAY_KEY = "spells";

    /** The NBT key used to store the currently selected spell index on the item in form of an integer. */
    public static final String SELECTED_SPELL_KEY = "selectedSpell";

    /** The NBT key used to store the array of cooldown end times (game time) for each spell on the item in form of an array of longs. */
    public static final String COOLDOWN_END_TIME_ARRAY_KEY = "cooldownEndTime";

    /** The NBT key used to store the array of maximum cooldowns for each spell on the item in form of an array of longs. */
    public static final String MAX_COOLDOWN_ARRAY_KEY = "maxCooldown";

    /** The NBT key used to store the item upgrades in form of an array of integers. */
    public static final String UPGRADES_KEY = "upgrades";

    /** The NBT key used to store the item progression level in form of an integer. */
    public static final String PROGRESSION_KEY = "progression";

    /**
     * Gives the list of spells stored on the stack. If there are fewer spells stored than the item's maximum spell slots,
     * the list is padded with {@link Spells#NONE} to reach the maximum size.
     *
     * @param stack The ItemStack.
     * @return A list of spells on the item, padded with {@link Spells#NONE} if necessary. If the item has no tag, an
     * empty list is returned.
     */
    public static List<Spell> getSpells(ItemStack stack) {
        ArrayList<Spell> spells = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (isEmpty(tag)) return spells;

        if (tag.contains(SPELL_ARRAY_KEY)) {
            ListTag list = tag.getList(SPELL_ARRAY_KEY, Tag.TAG_STRING);
            for (Tag element : list) {
                if (element instanceof StringTag stringTag) {
                    ResourceLocation location = ResourceLocation.tryParse(stringTag.getAsString());
                    if (location != null) spells.add(Services.REGISTRY_UTIL.getSpell(location));
                }
            }
        }

        int maxSlots = stack.getItem() instanceof IWorkbenchItem workbenchItem ? workbenchItem.getSpellSlotCount(stack) : 5;
        while (spells.size() < maxSlots) spells.add(Spells.NONE);

        return spells;
    }

    /**
     * Sets the list of spells on the stack's tag.
     *
     * @param stack  The ItemStack.
     * @param spells The collection of spells to set on the stack.
     */
    public static void setSpells(ItemStack stack, Collection<Spell> spells) {
        ListTag list = new ListTag();
        spells.forEach(spell -> list.add(StringTag.valueOf(spell.getLocation().toString())));
        stack.getOrCreateTag().put(SPELL_ARRAY_KEY, list);
    }

    /**
     * Gives the currently selected spell on the stack.
     *
     * @param stack The ItemStack.
     * @return The currently selected spell, or {@link Spells#NONE} if the stack has no tag or no spells are stored/selected.
     */
    public static Spell getCurrentSpell(ItemStack stack) {
        List<Spell> spells = getSpells(stack);
        if (spells.isEmpty()) return Spells.NONE;
        if (isEmpty(stack.getTag())) return Spells.NONE;
        int selectedIndex = stack.getTag().getInt(SELECTED_SPELL_KEY);

        // Bounds check
        if (selectedIndex >= 0 && selectedIndex < spells.size()) {
            return spells.get(selectedIndex);
        }

        return spells.get(0);
    }

    /**
     * Gives the index of the current spell selected by the stack.
     *
     * @param stack The ItemStack.
     * @return The index of the currently selected spell, or 0 if the stack has no tag or no spells are stored/selected.
     */
    public static int getCurrentSpellIndex(ItemStack stack) {
        if (isEmpty(stack.getTag())) return 0;
        return stack.getTag().getInt(SELECTED_SPELL_KEY);
    }

    /**
     * Sets the currently selected spell on the stack based on its location in the list of saved spells.
     *
     * @param stack The ItemStack.
     * @param spell The spell to set as currently selected. If the spell is not in the list, the first spell is selected.
     */
    public static void setCurrentSpell(ItemStack stack, Spell spell) {
        List<Spell> spells = getSpells(stack);
        int index = spells.indexOf(spell);
        if (index == -1) index = 0;
        stack.getOrCreateTag().putInt(SELECTED_SPELL_KEY, index);
    }

    /**
     * Sets the currently selected spell on the stack by index based on the list of saved spells.
     *
     * @param stack The ItemStack.
     * @param index The index of the spell to set as currently selected.
     * @return True if the index was valid and the spell was set, false otherwise.
     */
    public static boolean setCurrentSpell(ItemStack stack, int index) {
        List<Spell> spells = getSpells(stack);
        if (index < 0 || index >= spells.size()) {
            return false;
        }
        stack.getOrCreateTag().putInt(SELECTED_SPELL_KEY, index);
        return true;
    }

    /**
     * Gives the next spell in the stack's spell list, wrapping around to the start if necessary.
     *
     * @param stack The ItemStack.
     * @return The next spell in the list based on the currently selected spell.
     */
    public static Spell getNextSpell(ItemStack stack) {
        return getAdjacentSpell(stack, 1);
    }

    /**
     * Returns the previous spell in the stack's spell list, wrapping around to the end if necessary.
     *
     * @param stack The ItemStack.
     * @return The previous spell in the list based on the currently selected spell.
     */
    public static Spell getPreviousSpell(ItemStack stack) {
        return getAdjacentSpell(stack, -1);
    }

    /**
     * Selects the next spell on the stack by incrementing the index, wrapping around if necessary.
     * This method directly manipulates indices to avoid issues with duplicate spells (e.g., multiple NONE spells).
     *
     * @param stack The ItemStack.
     */
    public static void selectNextSpell(ItemStack stack) {
        List<Spell> spells = getSpells(stack);
        if (spells.isEmpty()) return;
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return;
        int currentIndex = stack.getTag().getInt(SELECTED_SPELL_KEY);

        // Bounds check
        if (currentIndex < 0 || currentIndex >= spells.size()) {
            currentIndex = 0;
        }

        int newIndex = (currentIndex + 1) % spells.size();
        stack.getTag().putInt(SELECTED_SPELL_KEY, newIndex);
    }

    /**
     * Selects the previous spell on the stack by decrementing the index, wrapping around if necessary.
     * This method directly manipulates indices to avoid issues with duplicate spells (e.g., multiple NONE spells).
     *
     * @param stack The ItemStack.
     */
    public static void selectPreviousSpell(ItemStack stack) {
        List<Spell> spells = getSpells(stack);
        if (spells.isEmpty()) return;
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return;
        int currentIndex = stack.getTag().getInt(SELECTED_SPELL_KEY);

        // Bounds check
        if (currentIndex < 0 || currentIndex >= spells.size()) {
            currentIndex = 0;
        }

        int newIndex = (currentIndex - 1 + spells.size()) % spells.size();
        stack.getTag().putInt(SELECTED_SPELL_KEY, newIndex);
    }

    /**
     * Gives the wanted Spell on the saved Spells list based on the current selected spell and the offset.
     *
     * @param stack  The ItemStack.
     * @param offset The offset to apply to the current index.
     * @return The Spell at the new index. If the Spells list is empty or the tag is null, returns Spells.NONE.
     */
    private static Spell getAdjacentSpell(ItemStack stack, int offset) {
        List<Spell> spells = getSpells(stack);
        if (spells.isEmpty()) return Spells.NONE;
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return Spells.NONE;
        int currentIndex = stack.getTag().getInt(SELECTED_SPELL_KEY);

        // Bounds check
        if (currentIndex < 0 || currentIndex >= spells.size()) {
            currentIndex = 0;
        }

        int newIndex = (currentIndex + offset + spells.size()) % spells.size();
        return spells.get(newIndex);
    }

    /**
     * Gives the array of cooldown end times (level game time) for each spell on the stack.
     *
     * @param stack The ItemStack.
     * @return An array of cooldown end times. If the tag is null or empty, returns an empty array.
     */
    public static long[] getCooldownEndTimes(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return new long[0];
        return stack.getTag().getLongArray(COOLDOWN_END_TIME_ARRAY_KEY);
    }

    /**
     * Sets the array of cooldown end times (level game time) for each spell on the stack.
     *
     * @param stack            The ItemStack.
     * @param cooldownEndTimes The array of cooldown end times to set.
     */
    public static void setCooldownEndTimes(ItemStack stack, long[] cooldownEndTimes) {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return;
        stack.getTag().putLongArray(COOLDOWN_END_TIME_ARRAY_KEY, cooldownEndTimes);
    }

    /**
     * Gives the current cooldown for the currently selected spell on the stack based on game time.
     *
     * @param stack           The ItemStack.
     * @param currentGameTime The current game time from the world.
     * @return The current cooldown remaining for the selected spell. If the tag is null or empty, returns 0.
     */
    public static int getCurrentCooldown(ItemStack stack, long currentGameTime) {
        long[] endTimes = getCooldownEndTimes(stack);
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return 0;
        int selectedSpellIndex = stack.getTag().getInt(SELECTED_SPELL_KEY);

        if (selectedSpellIndex >= 0 && selectedSpellIndex < endTimes.length) {
            long endTime = endTimes[selectedSpellIndex];
            if (endTime > currentGameTime) {
                return (int) (endTime - currentGameTime);
            }
        }
        return 0;
    }

    /**
     * Sets the cooldown for the currently selected spell on the stack using game time.
     *
     * @param stack           The ItemStack.
     * @param cooldown        The cooldown duration in ticks.
     * @param currentGameTime The current game time from the world.
     */
    public static void setCurrentCooldown(ItemStack stack, int cooldown, long currentGameTime) {
        int selectedSpell = stack.getOrCreateTag().getInt(SELECTED_SPELL_KEY);
        int spellCount = getSpells(stack).size();

        if (selectedSpell >= spellCount) return;

        long[] endTimes = getCooldownEndTimes(stack);
        if (endTimes.length <= selectedSpell) endTimes = new long[spellCount];

        endTimes[selectedSpell] = currentGameTime + Math.max(1, cooldown);
        setCooldownEndTimes(stack, endTimes);

        int[] maxCooldowns = getMaxCooldowns(stack);
        if (maxCooldowns.length <= selectedSpell) maxCooldowns = new int[spellCount];
        maxCooldowns[selectedSpell] = Math.max(1, cooldown);
        setMaxCooldowns(stack, maxCooldowns);
    }

    /**
     * Gives the array of maximum cooldowns for each spell on the stack.
     *
     * @param stack The ItemStack.
     * @return An array of maximum cooldowns, or an empty array if the tag is null or empty.
     */
    public static int[] getMaxCooldowns(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return new int[0];
        return stack.getTag().getIntArray(MAX_COOLDOWN_ARRAY_KEY);
    }

    /**
     * Sets the array of maximum cooldowns for each spell on the stack.
     *
     * @param stack     The ItemStack.
     * @param cooldowns The array of maximum cooldowns to set.
     */
    public static void setMaxCooldowns(ItemStack stack, int[] cooldowns) {
        stack.getOrCreateTag().putIntArray(MAX_COOLDOWN_ARRAY_KEY, cooldowns);
    }

    /**
     * Gives the maximum cooldown for the currently selected spell on the stack.
     *
     * @param stack The ItemStack.
     * @return The maximum cooldown for the selected spell, or 0 if not set.
     */
    public static int getCurrentMaxCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return 0;
        int[] cooldowns = getMaxCooldowns(stack);
        int selectedSpell = stack.getTag().getInt(SELECTED_SPELL_KEY);

        return (selectedSpell >= 0 && selectedSpell < cooldowns.length) ? cooldowns[selectedSpell] : 0;
    }

    /**
     * Gives the level of the specified upgrade on the stack.
     *
     * @param stack   The ItemStack.
     * @param upgrade The upgrade item.
     * @return The level of the upgrade, or 0 if the upgrade is not found.
     */
    public static int getUpgradeLevel(ItemStack stack, Item upgrade) {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return 0;

        for (var entry : WandUpgrades.getWandUpgrades().entrySet()) {
            if (entry.getKey().equals(upgrade)) {
                return stack.getTag().getCompound(UPGRADES_KEY).getInt(entry.getValue());
            }
        }
        return 0;
    }

    /**
     * Gives the total number of upgrades applied to the stack.
     *
     * @param stack The ItemStack.
     * @return The total number of upgrades, could be 0 if no upgrades are applied.
     */
    public static int getTotalUpgrades(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return 0;
        return WandUpgrades.getWandUpgrades().keySet().stream()
                .mapToInt(item -> getUpgradeLevel(stack, item))
                .sum();
    }

    /**
     * Applies the specified upgrade to the stack, increasing its level by 1 each time.
     *
     * @param stack   The ItemStack.
     * @param upgrade The upgrade item.
     */
    public static void applyUpgrade(ItemStack stack, Item upgrade) {
        CompoundTag upgrades = stack.getOrCreateTag().getCompound(UPGRADES_KEY);
        if (upgrades.isEmpty()) {
            upgrades = new CompoundTag();
        }

        for (var entry : WandUpgrades.getWandUpgrades().entrySet()) {
            if (entry.getKey().equals(upgrade)) {
                String key = entry.getValue();
                upgrades.putInt(key, upgrades.getInt(key) + 1);
                stack.getOrCreateTag().put(UPGRADES_KEY, upgrades);
                return;
            }
        }
    }

    /**
     * Returns the stack's current progression level.
     *
     * @param stack The ItemStack.
     * @return The stack's progression level. If the tag is null or empty, returns 0.
     */
    public static int getProgression(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || tag.isEmpty()) return 0;
        return stack.getTag().getInt(PROGRESSION_KEY);
    }

    /**
     * Sets the stack's progression level to the specified value.
     *
     * @param stack       The ItemStack.
     * @param progression The progression level to set.
     */
    public static void setProgression(ItemStack stack, int progression) {
        stack.getOrCreateTag().putInt(PROGRESSION_KEY, progression);
    }

    /**
     * Increases the stack's progression level by the specified amount.
     *
     * @param stack       The ItemStack.
     * @param progression The amount to increase the progression level by.
     */
    public static void addProgression(ItemStack stack, int progression) {
        setProgression(stack, getProgression(stack) + progression);
    }

    /**
     * Gives true if the tag is null or empty. Used to simplify null checks.
     *
     * @param tag The tag to check.
     * @return True if the tag is null or empty, false otherwise.
     */
    private static boolean isEmpty(@Nullable CompoundTag tag) {
        return tag == null || tag.isEmpty();
    }

    private CastItemDataHelper() {
    }
}