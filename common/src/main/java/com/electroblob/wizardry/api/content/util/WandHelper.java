package com.electroblob.wizardry.api.content.util;

import com.electroblob.wizardry.api.content.DeferredObject;
import com.electroblob.wizardry.api.content.data.SpellManagerData;
import com.electroblob.wizardry.api.content.item.IElementValue;
import com.electroblob.wizardry.api.content.item.ITierValue;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.content.item.WandItem;
import com.electroblob.wizardry.core.EBConfig;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For avoiding a big and messy WandItem class (and helping addon devs by providing an API a similar way to use the
 * wand NBT structure). This class provides static methods for getting and setting wand-related data on wand ItemStacks.
 * <p>
 * Wand data is stored in the wand's NBT as follows:
 * <ul>
 *     <li>A list of spells under the key {@link #SPELL_ARRAY_KEY}, stored as a list of string ResourceLocations.</li>
 *     <li>The currently selected spell under the key {@link #SELECTED_SPELL_KEY}, stored as a string ResourceLocation.</li>
 *     <li>An array of integers under the key {@link #COOLDOWN_ARRAY_KEY}, storing the current cooldown for each spell slot.</li>
 *     <li>An array of integers under the key {@link #MAX_COOLDOWN_ARRAY_KEY}, storing the maximum cooldown for each spell slot.</li>
 *     <li>A compound tag under the key {@link #UPGRADES_KEY}, storing the levels of each upgrade.</li>
 *     <li>An integer under the key {@link #PROGRESSION_KEY}, storing the wand's progression level.</li>
 * </ul>
 */
public final class WandHelper {
    /** The NBT key used to store the array of spells on the wand. */
    public static final String SPELL_ARRAY_KEY = "spells";

    /** The NBT key used to store the currently selected spell on the wand. */
    public static final String SELECTED_SPELL_KEY = "selectedSpell";

    /** The NBT key used to store the array of cooldowns for each spell on the wand. */
    public static final String COOLDOWN_ARRAY_KEY = "cooldown";

    /** The NBT key used to store the array of maximum cooldowns for each spell on the wand. */
    public static final String MAX_COOLDOWN_ARRAY_KEY = "maxCooldown";

    /** The NBT key used to store the wand upgrades. */
    public static final String UPGRADES_KEY = "upgrades";

    /** The NBT key used to store the wand progression level. */
    public static final String PROGRESSION_KEY = "progression";

    /**
     * Returns the list of spells stored on the wand. If there are fewer spells stored than the wand's maximum spell slots,
     * the list is padded with {@link Spells#NONE} to reach the maximum size.
     *
     * @param wand The wand ItemStack.
     * @return A list of spells on the wand.
     */
    public static List<Spell> getSpells(ItemStack wand) {
        ArrayList<Spell> spells = new ArrayList<>();
        CompoundTag tag = wand.getOrCreateTag();

        if (tag.contains(SPELL_ARRAY_KEY)) {
            ListTag list = tag.getList(SPELL_ARRAY_KEY, Tag.TAG_STRING);
            for (Tag element : list) {
                if (element instanceof StringTag stringTag) {
                    ResourceLocation location = ResourceLocation.tryParse(stringTag.getAsString());
                    if (location != null) {
                        spells.add(Services.REGISTRY_UTIL.getSpell(location));
                    }
                }
            }
        }

        int maxSlots = wand.getItem() instanceof WandItem wandItem ? wandItem.getSpellSlotCount(wand) : 5;
        while (spells.size() < maxSlots) spells.add(Spells.NONE);

        return spells;
    }

    /**
     * Sets the list of spells on the wand.
     *
     * @param wand The wand ItemStack.
     * @param spells The collection of spells to set on the wand.
     */
    public static void setSpells(ItemStack wand, Collection<Spell> spells) {
        ListTag list = new ListTag();
        spells.forEach(spell -> list.add(StringTag.valueOf(spell.getLocation().toString())));
        wand.getOrCreateTag().put(SPELL_ARRAY_KEY, list);
    }

    /**
     * Returns the currently selected spell on the wand.
     *
     * @param wand The wand ItemStack.
     * @return The currently selected spell.
     */
    public static Spell getCurrentSpell(ItemStack wand) {
        String tag = wand.getOrCreateTag().getString(SELECTED_SPELL_KEY);
        Spell spell = Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(tag));
        return spell != null ? spell : Spells.NONE;
    }

    /**
     * Sets the currently selected spell on the wand.
     *
     * @param wand The wand ItemStack.
     * @param spell The spell to set as currently selected.
     */
    public static void setCurrentSpell(ItemStack wand, Spell spell) {
        wand.getOrCreateTag().putString(SELECTED_SPELL_KEY, spell.getLocation().toString());
    }

    /**
     * Returns the next spell in the wand's spell list, wrapping around to the start if necessary.
     *
     * @param wand The wand ItemStack.
     * @return The next spell.
     */
    public static Spell getNextSpell(ItemStack wand) {
        return getAdjacentSpell(wand, 1);
    }

    /**
     * Returns the previous spell in the wand's spell list, wrapping around to the end if necessary.
     *
     * @param wand The wand ItemStack.
     * @return The previous spell.
     */
    public static Spell getPreviousSpell(ItemStack wand) {
        return getAdjacentSpell(wand, -1);
    }

    private static Spell getAdjacentSpell(ItemStack wand, int offset) {
        List<Spell> spells = getSpells(wand);
        if (spells.isEmpty()) return Spells.NONE;

        int currentIndex = spells.indexOf(getCurrentSpell(wand));
        if (currentIndex == -1) return spells.get(0);

        int newIndex = (currentIndex + offset + spells.size()) % spells.size();
        return spells.get(newIndex);
    }

    /**
     * Selects the spell at the given index on the wand.
     *
     * @param wand The wand ItemStack.
     * @param index The index of the spell to select.
     * @return True if the spell was successfully selected, false otherwise.
     */
    public static boolean selectSpell(ItemStack wand, int index) {
        List<Spell> spells = getSpells(wand);
        if (spells.isEmpty()) return false;

        setCurrentSpell(wand, spells.get(index));
        return true;
    }

    /**
     * Returns the array of cooldowns for each spell on the wand.
     *
     * @param wand The wand ItemStack.
     * @return An array of cooldowns.
     */
    public static int[] getCooldowns(ItemStack wand) {
        return wand.getOrCreateTag().getIntArray(COOLDOWN_ARRAY_KEY);
    }

    /**
     * Sets the array of cooldowns for each spell on the wand.
     *
     * @param wand The wand ItemStack.
     * @param cooldowns The array of cooldowns to set.
     */
    public static void setCooldowns(ItemStack wand, int[] cooldowns) {
        wand.getOrCreateTag().putIntArray(COOLDOWN_ARRAY_KEY, cooldowns);
    }

    /**
     * Decrements the cooldowns for all spells on the wand by 1, ensuring they do not go below 0.
     *
     * @param wand The wand ItemStack.
     */
    public static void decrementCooldowns(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        if (cooldowns.length == 0) return;

        for (int i = 0; i < cooldowns.length; i++) {
            cooldowns[i] = Math.max(0, cooldowns[i] - 1);
        }
        setCooldowns(wand, cooldowns);
    }

    /**
     * Returns the current cooldown for the currently selected spell on the wand.
     *
     * @param wand The wand ItemStack.
     * @return The current cooldown for the selected spell.
     */
    public static int getCurrentCooldown(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        int selectedSpellIndex = getSpells(wand).indexOf(getCurrentSpell(wand));

        return (selectedSpellIndex >= 0 && selectedSpellIndex < cooldowns.length) ? cooldowns[selectedSpellIndex] : 0;
    }

    /**
     * Sets the cooldown for the currently selected spell on the wand.
     *
     * @param wand The wand ItemStack.
     * @param cooldown The cooldown to set.
     */
    public static void setCurrentCooldown(ItemStack wand, int cooldown) {
        int selectedSpell = getSpells(wand).indexOf(getCurrentSpell(wand));
        int spellCount = getSpells(wand).size();

        if (selectedSpell >= spellCount) return;

        int[] cooldowns = getCooldowns(wand);
        if (cooldowns.length <= selectedSpell) cooldowns = new int[spellCount];

        cooldowns[selectedSpell] = Math.max(1, cooldown);
        setCooldowns(wand, cooldowns);

        int[] maxCooldowns = getMaxCooldowns(wand);
        if (maxCooldowns.length <= selectedSpell) maxCooldowns = new int[spellCount];
        maxCooldowns[selectedSpell] = Math.max(1, cooldown);
        setMaxCooldowns(wand, maxCooldowns);
    }

    /**
     * Returns the array of maximum cooldowns for each spell on the wand.
     *
     * @param wand The wand ItemStack.
     * @return An array of maximum cooldowns.
     */
    public static int[] getMaxCooldowns(ItemStack wand) {
        return wand.getOrCreateTag().getIntArray(MAX_COOLDOWN_ARRAY_KEY);
    }

    /**
     * Sets the array of maximum cooldowns for each spell on the wand.
     *
     * @param wand The wand ItemStack.
     * @param cooldowns The array of maximum cooldowns to set.
     */
    public static void setMaxCooldowns(ItemStack wand, int[] cooldowns) {
        wand.getOrCreateTag().putIntArray(MAX_COOLDOWN_ARRAY_KEY, cooldowns);
    }

    /**
     * Returns the maximum cooldown for the currently selected spell on the wand.
     *
     * @param wand The wand ItemStack.
     * @return The maximum cooldown for the selected spell, or 0 if not set.
     */
    public static int getCurrentMaxCooldown(ItemStack wand) {
        int[] cooldowns = getMaxCooldowns(wand);
        int selectedSpell = getSpells(wand).indexOf(getCurrentSpell(wand));

        return (selectedSpell >= 0 && selectedSpell < cooldowns.length) ? cooldowns[selectedSpell] : 0;
    }

    /**
     * Returns the level of the specified upgrade on the wand.
     *
     * @param wand The wand ItemStack.
     * @param upgrade The upgrade item.
     * @return The level of the upgrade, or 0 if the upgrade is not found.
     */
    public static int getUpgradeLevel(ItemStack wand, DeferredObject<Item> upgrade) {
        String key = WandUpgrades.getWandUpgrades().get(upgrade);
        return key != null ? wand.getOrCreateTag().getCompound(UPGRADES_KEY).getInt(key) : 0;
    }

    /**
     * Returns the level of the specified upgrade on the wand.
     *
     * @param wand The wand ItemStack.
     * @param upgrade The upgrade item.
     * @return The level of the upgrade, or 0 if the upgrade is not found.
     */
    public static int getUpgradeLevel(ItemStack wand, Item upgrade) {
        for (var entry : WandUpgrades.getWandUpgrades().entrySet()) {
            if (entry.getKey().get().equals(upgrade)) {
                return wand.getOrCreateTag().getCompound(UPGRADES_KEY).getInt(entry.getValue());
            }
        }
        return 0;
    }

    /**
     * Returns the total number of upgrades applied to the wand.
     *
     * @param wand The wand ItemStack.
     * @return The total number of upgrades, could be zero if no upgrades are applied.
     */
    public static int getTotalUpgrades(ItemStack wand) {
        return WandUpgrades.getWandUpgrades().keySet().stream()
                .mapToInt(item -> getUpgradeLevel(wand, item))
                .sum();
    }

    /**
     * Applies the specified upgrade to the wand, increasing its level by 1.
     *
     * @param wand The wand ItemStack.
     * @param upgrade The upgrade item.
     */
    public static void applyUpgrade(ItemStack wand, Item upgrade) {
        CompoundTag upgrades = wand.getOrCreateTag().getCompound(UPGRADES_KEY);
        if (upgrades.isEmpty()) {
            upgrades = new CompoundTag();
        }

        for (var entry : WandUpgrades.getWandUpgrades().entrySet()) {
            if (entry.getKey().get().equals(upgrade)) {
                String key = entry.getValue();
                upgrades.putInt(key, upgrades.getInt(key) + 1);
                wand.getOrCreateTag().put(UPGRADES_KEY, upgrades);
                return;
            }
        }
    }

    /**
     * Returns the wand's current progression level.
     *
     * @param wand The wand ItemStack.
     * @return The wand's progression level.
     */
    public static int getProgression(ItemStack wand) {
        return wand.getOrCreateTag().getInt(PROGRESSION_KEY);
    }

    /**
     * Sets the wand's progression level to the specified value.
     *
     * @param wand The wand ItemStack.
     * @param progression The progression level to set.
     */
    public static void setProgression(ItemStack wand, int progression) {
        wand.getOrCreateTag().putInt(PROGRESSION_KEY, progression);
    }

    /**
     * Increases the wand's progression level by the specified amount.
     *
     * @param wand The wand ItemStack.
     * @param progression The amount to increase the progression level by.
     */
    public static void addProgression(ItemStack wand, int progression) {
        setProgression(wand, getProgression(wand) + progression);
    }

    /**
     * Returns the wand Item corresponding to the given tier and element.
     *
     * @param tier    The tier of the wand.
     * @param element The element of the wand. If null, defaults to {@link Elements#MAGIC}.
     * @return The wand Item.
     * @throws NullPointerException if the given tier is null.
     */
    public static Item getWand(SpellTier tier, Element element) {
        if (tier == null) throw new NullPointerException("The given tier cannot be null.");
        if (element == null) element = Elements.MAGIC;
        String registryName = tier == SpellTiers.NOVICE && element == Elements.MAGIC ? "novice" : tier.getOrCreateLocation().getPath();
        if (element != Elements.MAGIC) registryName = registryName + "_" + element.getLocation().getPath();
        registryName = "wand_" + registryName;
        return BuiltInRegistries.ITEM.get(new ResourceLocation(element.getLocation().getNamespace(), registryName));
    }

    /**
     * Calculates the distributed cost of a spell being cast over multiple ticks.
     * <p>
     * The cost is halved at 10 ticks and again at 20 ticks, with any remainder added to the first half.
     *
     * @param cost        The total cost of the spell.
     * @param castingTick The current tick of casting.
     * @return The distributed cost for the current tick.
     */
    public static int getDistributedCost(int cost, int castingTick) {
        int partialCost;

        if (castingTick % 20 == 0) partialCost = cost / 2 + cost % 2;
        else if (castingTick % 10 == 0) partialCost = cost / 2;
        else partialCost = 0;

        return partialCost;
    }

    /**
     * Calculates the spell modifiers provided by the wand for the given spell and player.
     *
     * @param stack  The wand ItemStack.
     * @param player The player casting the spell.
     * @param spell  The spell being cast.
     * @return The calculated spell modifiers.
     */
    public static SpellModifiers calculateModifiers(ItemStack stack, Player player, Spell spell) {
        SpellModifiers modifiers = new SpellModifiers();

        int level = getUpgradeLevel(stack, EBItems.RANGE_UPGRADE);
        if (level > 0)
            modifiers.set(EBItems.RANGE_UPGRADE.get(), 1.0f + level * EBConfig.RANGE_INCREASE_PER_LEVEL, true);

        level = getUpgradeLevel(stack, EBItems.DURATION_UPGRADE);
        if (level > 0)
            modifiers.set(EBItems.DURATION_UPGRADE.get(), 1.0f + level * EBConfig.DURATION_INCREASE_PER_LEVEL, false);

        level = getUpgradeLevel(stack, EBItems.BLAST_UPGRADE);
        if (level > 0)
            modifiers.set(EBItems.BLAST_UPGRADE.get(), 1.0f + level * EBConfig.BLAST_RADIUS_INCREASE_PER_LEVEL, true);

        level = getUpgradeLevel(stack, EBItems.COOLDOWN_UPGRADE);
        if (level > 0)
            modifiers.set(EBItems.COOLDOWN_UPGRADE.get(), 1.0f - level * EBConfig.COOLDOWN_REDUCTION_PER_LEVEL, true);

        float progressionModifier = 1.0F - ((float) Services.OBJECT_DATA.getWizardData(player).countRecentCasts(spell) / EBConfig.MAX_RECENT_SPELLS) * EBConfig.MAX_PROGRESSION_REDUCTION;
        SpellManagerData data = Services.OBJECT_DATA.getSpellManagerData(player);


        if (stack.getItem() instanceof IElementValue elementValue && stack.getItem() instanceof ITierValue tierValue) {
            if (elementValue.getElement() == spell.getElement()) {
                modifiers.set(SpellModifiers.POTENCY, 1.0f + (tierValue.getTier(stack).level + 1) * EBConfig.POTENCY_INCREASE_PER_TIER, true);
                progressionModifier *= 1.2f;
            }
        }

        if (!data.hasSpellBeenDiscovered(spell)) {
            progressionModifier *= 5f;
        }

        // TODO DATA TIER
//        if (!data.hasReachedTier(this.tier.next())) {
//            progressionModifier *= SECOND_TIME_PROGRESSION_MODIFIER;
//        }


        modifiers.set(SpellModifiers.PROGRESSION, progressionModifier, false);
        return modifiers;
    }
}