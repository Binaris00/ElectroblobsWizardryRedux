package com.binaris.wizardry.api.content.spell.internal;

import com.google.common.collect.Sets;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

/**
 * Glorified map for storing and saving spell modifier values such as potency, cost, chargeup and many others. This is
 * a mutable object that is intended to be modified rather than replaced, for example, inside the {@code SpellCastEvent.Pre}
 * you would use this object to modify specific parts of the spell ("modifiers") rather making some hacky replacement.
 * <p>
 * If you try to add a new modifier that's not on the original mod (e.g. custom wand upgrades/modifiers) make sure to
 * mark them as needing syncing if you want the client to be aware of them and of course make the needed implementations
 * on your spell casting code to make use of them.
 */
@SuppressWarnings("unused")
public final class SpellModifiers {
    /** Constant string identifier for the potency modifier. */
    public static final String POTENCY = "ebwizardry.potency";
    /** Constant string identifier for the mana cost modifier. */
    public static final String COST = "ebwizardry.cost";
    /** Constant string identifier for the wand charge-up modifier. */
    public static final String CHARGEUP = "ebwizardry.chargeup";
    /** Constant string identifier for the wand progression modifier. */
    public static final String PROGRESSION = "ebwizardry.progression";
    /** Constant string identifier for modifying the duration effects of spells */
    public static final String DURATION = "ebwizardry.duration";
    /** Constant string identifier for modifying the area effect of spells */
    public static final String BLAST = "ebwizardry.blast";
    /** Constant string identifier for modifying the effect range of spells */
    public static final String RANGE = "ebwizardry.range";
    /** Constant string identifier for modifying cooldown of spells */
    public static final String COOLDOWN = "ebwizardry.cooldown";

    private final Map<String, Float> multiplierMap;

    public SpellModifiers() {
        multiplierMap = new HashMap<>();
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
        tag.getAllKeys().forEach(key -> modifiers.set(key, tag.getFloat(key)));
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
            this.set(key, newValue);
        }
        return this;
    }

    /**
     * Sets the multiplier for a specific upgrade identified by the given key.
     *
     * @param key        The string identifier for the upgrade.
     * @param multiplier The multiplier value to set.
     * @return This {@link SpellModifiers} instance after setting the multiplier.
     */
    public SpellModifiers set(String key, float multiplier) {
        multiplierMap.put(key, multiplier);
        return this;
    }

    /**
     * Adds the value to the given key. In case there's not already a value for this modifier it will be saved as the
     * base.
     *
     * @param key   The string identifier for the upgrade.
     * @param value The value that's going to be added to the modifier
     * @return This {@link SpellModifiers} instance after setting the multiplier.
     */
    public SpellModifiers add(String key, float value) {
        multiplierMap.merge(key, value, Float::sum);
        return this;
    }

    /**
     * Subtract the value based on the given key. In case there's not already a value for this modifier it will be saved
     * as the base.
     *
     * @param key   The string identifier for the upgrade.
     * @param value The value that's going to be subtracted to the modifier
     * @return This {@link SpellModifiers} instance after setting the multiplier.
     */
    public SpellModifiers subtract(String key, float value) {
        multiplierMap.merge(key, -value, Float::sum);
        return this;
    }

    /**
     * Multiply the value based on the given key. In case there's not already a value for this modifier it won't do anything.
     *
     * @param key   The string identifier for the upgrade.
     * @param factor The value that's going to serve as the factor of the multiply
     * @return This {@link SpellModifiers} instance after setting the multiplier.
     */
    public SpellModifiers multiply(String key, float factor) {
        multiplierMap.computeIfPresent(key, (k, v) -> v * factor);
        return this;
    }

    /**
     * Divide the value based on the given key. In case there's not already a value for this modifier it won't do anything.
     *
     * @param key   The string identifier for the upgrade.
     * @param divisor The value that's going to serve as the divisor
     * @return This {@link SpellModifiers} instance after setting the multiplier.
     */
    public SpellModifiers divide(String key, float divisor) {
        if (divisor == 0) throw new ArithmeticException("Cannot divide spell modifier by zero: " + key);
        multiplierMap.computeIfPresent(key, (k, v) -> v / divisor);
        return this;
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
     * Resets all multipliers and synced multipliers, clearing all stored values, including those that do not require
     * syncing.
     */
    public void reset() {
        this.multiplierMap.clear();
    }
}
