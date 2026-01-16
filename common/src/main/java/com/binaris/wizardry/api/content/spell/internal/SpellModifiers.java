package com.binaris.wizardry.api.content.spell.internal;

import com.binaris.wizardry.setup.registries.WandUpgrades;
import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

/**
 * Glorified map for storing and saving spell modifier values such as potency, cost, chargeup and many others. This is
 * a mutable object that is intended to be modified rather than replaced, for example, inside the {@code SpellCastEvent.Pre}
 * you would use this object to modify specific parts of the spell ("modifiers") rather making some hacky replacement.
 * <p>
 * It also keeps track of which modifiers need to be synced to the client, so that only those are sent over the network.
 * <p>
 * If you try to add a new modifier that's not on the original mod (e.g. custom wand upgrades/modifiers) make sure to
 * mark them as needing syncing if you want the client to be aware of them and of course make the needed implementations
 * on your spell casting code to make use of them.
 */
public final class SpellModifiers {
    /** Constant string identifier for the potency modifier. */
    public static final String POTENCY = "potency";
    /** Constant string identifier for the mana cost modifier. */
    public static final String COST = "cost";
    /** Constant string identifier for the wand charge-up modifier. */
    public static final String CHARGEUP = "chargeup";
    /** Constant string identifier for the wand progression modifier. */
    public static final String PROGRESSION = "progression";

    private final Map<String, Float> multiplierMap;
    private final Map<String, Float> syncedMultiplierMap;


    public SpellModifiers() {
        multiplierMap = new HashMap<>();
        syncedMultiplierMap = new HashMap<>();
    }

    /**
     * Creates a {@link SpellModifiers} instance from the given NBT tag. All entries in the tag are treated as
     * float multipliers with their keys as the modifier identifiers.
     *
     * @param tag The NBT tag containing the modifier data.
     * @return A {@link SpellModifiers} instance populated with the data from the tag.
     */
    public static SpellModifiers fromTag(CompoundTag tag) {
        SpellModifiers modifiers = new SpellModifiers();
        tag.getAllKeys().forEach(key -> modifiers.set(key, tag.getFloat(key), true));
        return modifiers;
    }

    /**
     * Converts this {@link SpellModifiers} instance to an NBT tag. All entries in the multiplier map are added
     * to the tag as float values with their keys as the modifier identifiers.
     *
     * @return A {@link CompoundTag} containing the modifier data.
     */
    public CompoundTag toTag() {
        CompoundTag nbt = new CompoundTag();
        multiplierMap.forEach(nbt::putFloat);
        return nbt;
    }

    /**
     * Combines this {@link SpellModifiers} instance with another by multiplying their corresponding modifier values.
     * If a modifier exists in either instance, it will be included in the result. The syncing status of each modifier
     * is preserved if it exists in either instance.
     *
     * @param modifiers The other {@link SpellModifiers} instance to combine with.
     * @return This {@link SpellModifiers} instance after combining.
     */
    public SpellModifiers combine(SpellModifiers modifiers) {
        for (String key : Sets.union(this.multiplierMap.keySet(), modifiers.multiplierMap.keySet())) {
            float newValue = this.get(key) * modifiers.get(key);
            boolean sync = this.syncedMultiplierMap.containsKey(key) || modifiers.syncedMultiplierMap.containsKey(key);
            this.set(key, newValue, sync);
        }
        return this;
    }

    /**
     * Sets the multiplier for a specific upgrade identified by the given item, along with its syncing status. The item
     * is converted to its string identifier using {@link WandUpgrades#getIdentifier(Item)} in order to store the multiplier.
     *
     * @param upgrade The item representing the upgrade.
     * @param multiplier The multiplier value to set.
     * @param needsSyncing Whether this modifier needs to be synced to the client.
     * @return This {@link SpellModifiers} instance after setting the multiplier.
     */
    public SpellModifiers set(Item upgrade, float multiplier, boolean needsSyncing) {
        this.set(WandUpgrades.getIdentifier(upgrade), multiplier, needsSyncing);
        return this;
    }

    /**
     * Sets the multiplier for a specific upgrade identified by the given key, along with its syncing status.
     *
     * @param key The string identifier for the upgrade.
     * @param multiplier The multiplier value to set.
     * @param needsSyncing Whether this modifier needs to be synced to the client.
     * @return This {@link SpellModifiers} instance after setting the multiplier.
     */
    public SpellModifiers set(String key, float multiplier, boolean needsSyncing) {
        multiplierMap.put(key, multiplier);
        if (needsSyncing) syncedMultiplierMap.put(key, multiplier);
        return this;
    }

    /**
     * Gets the multiplier for a specific upgrade identified by the given item. The item is converted to its string
     * identifier using {@link WandUpgrades#getIdentifier(Item)} in order to retrieve the multiplier.
     *
     * @param upgrade The item representing the upgrade.
     * @return The multiplier value for the specified upgrade, or 1 if not set.
     */
    public float get(Item upgrade) {
        return get(WandUpgrades.getIdentifier(upgrade));
    }

    /**
     * Gets the multiplier for a specific upgrade identified by the given key.
     *
     * @param key The string identifier for the upgrade.
     * @return The multiplier value for the specified upgrade, or 1 if not set.
     */
    public float get(String key) {
        Float value = multiplierMap.get(key);
        return value == null ? 1 : value;
    }

    /**
     * Retrieves the complete map of all multipliers, including those that do not require syncing.
     *
     * @return A map containing all modifier identifiers and their corresponding multiplier values.
     */
    public Map<String, Float> getMultipliers() {
        return multiplierMap;
    }

    /**
     * Retrieves the map of multipliers that need to be synced to the client.
     *
     * @return A map containing modifier identifiers and their corresponding multiplier values that require syncing.
     */
    public Map<String, Float> getSyncedMultipliers() {
        return syncedMultiplierMap;
    }

    /**
     * Resets all multipliers and synced multipliers, clearing all stored values, including those that do not require
     * syncing.
     */
    public void reset() {
        this.multiplierMap.clear();
        this.syncedMultiplierMap.clear();
    }
}
