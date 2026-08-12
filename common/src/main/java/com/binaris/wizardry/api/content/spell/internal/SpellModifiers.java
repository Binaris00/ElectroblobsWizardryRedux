package com.binaris.wizardry.api.content.spell.internal;

import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/// Glorified map for storing and saving spell modifier values such as potency, cost, chargeup and many others. This is
/// a mutable object that is intended to be modified rather than replaced, for example, inside the `SpellCastEvent.Pre`
/// you would use this object to modify specific parts of the spell ("modifiers") rather making some hacky replacement.
///
/// Values are stored as direct factors relative to a base of 1.0: `add` adds a flat amount, while `multiply` and
/// `multiplyTotal` multiply by the factor they receive (1.3 = +30%, 0.7 = −30%). `set` overrides the base value read
/// by [#get(String, float)].
///
/// The order of operation is as follows (the entries are sorted by operation type before applying):
/// 1. Base value (float) (when you use [#get(String, float)])
/// 2. Set values (when you use [#set(String, float)])
/// 3. Additions (when you use [#add(String, float)])
/// 4. Multiplies (when you use [#multiply(String, float)] or [#multiplyTotal(String, float)])
@SuppressWarnings("unused")
public final class SpellModifiers {
    /// Constant string identifier for the potency modifier.
    public static final String POTENCY = "ebwizardry.potency";
    /// Constant string identifier for the mana cost modifier.
    public static final String COST = "ebwizardry.cost";
    /// Constant string identifier for the wand charge-up modifier.
    public static final String CHARGEUP = "ebwizardry.chargeup";
    /// Constant string identifier for the wand progression modifier.
    public static final String PROGRESSION = "ebwizardry.progression";
    /// Constant string identifier for modifying the duration effects of spells
    public static final String DURATION = "ebwizardry.duration";
    /// Constant string identifier for modifying the area effect of spells
    public static final String BLAST = "ebwizardry.blast";
    /// Constant string identifier for modifying the effect range of spells
    public static final String RANGE = "ebwizardry.range";
    /// Constant string identifier for modifying cooldown of spells
    public static final String COOLDOWN = "ebwizardry.cooldown";
    /// Constant string identifier for modifying health of players and mobs, normally used for minions
    public static final String HEALTH_MODIFIER = "ebwizardry.health_modifier";

    private final Map<String, ModifiersInstance> modifiersMap;

    public SpellModifiers() {
        modifiersMap = new HashMap<>();
    }

    public enum Operation {
        ADDITION,
        MULTIPLY_BASE,
        MULTIPLY_TOTAL
    }

    public record ModifierEntry(Operation operation, float amount) {
    }

    public float get(String key, float value) {
        ModifiersInstance instance = modifiersMap.get(key);
        return instance == null ? value : instance.get(value);
    }

    public float getFactor(String key) {
        ModifiersInstance instance = modifiersMap.get(key);
        return instance == null ? 1.0f : instance.get(1.0F);
    }

    public SpellModifiers set(String key, float multiplier) {
        ModifiersInstance instance = modifiersMap.computeIfAbsent(key, k -> new ModifiersInstance());
        instance.setBaseValue(multiplier);
        return this;
    }

    public SpellModifiers add(String key, float value) {
        ModifiersInstance instance = modifiersMap.computeIfAbsent(key, k -> new ModifiersInstance());
        instance.addModifier(Operation.ADDITION, value);
        return this;
    }

    public SpellModifiers multiply(String key, float factor) {
        ModifiersInstance instance = modifiersMap.computeIfAbsent(key, k -> new ModifiersInstance());
        instance.addModifier(Operation.MULTIPLY_TOTAL, factor);
        return this;
    }

    public SpellModifiers multiplyTotal(String key, float factor) {
        ModifiersInstance instance = modifiersMap.computeIfAbsent(key, k -> new ModifiersInstance());
        instance.addModifier(Operation.MULTIPLY_TOTAL, factor);
        return this;
    }

    public SpellModifiers operate(String key, float value, Operation op) {
        ModifiersInstance instance = modifiersMap.computeIfAbsent(key, k -> new ModifiersInstance());
        instance.addModifier(op, value);
        return this;
    }

    /// Combines the modifiers of another [SpellModifiers] instance by merging its modifier lists into this instance.
    /// For each key, the operations of `modifiers` are added first, followed by the ones already present in `this`,
    /// and the combined list is sorted by operation type (additions first, then multiplies).
    ///
    /// @param modifiers the other instance to combine
    /// @return this instance, with the other's modifiers merged in
    public SpellModifiers combine(SpellModifiers modifiers) {
        Set<String> allKeys = new HashSet<>(this.modifiersMap.keySet());
        allKeys.addAll(modifiers.modifiersMap.keySet());
        for (String key : allKeys) {
            ModifiersInstance fresh = new ModifiersInstance();
            fresh.merge(modifiers.modifiersMap.get(key));
            fresh.merge(this.modifiersMap.get(key));
            fresh.sortByOperation();
            this.modifiersMap.put(key, fresh);
        }
        return this;
    }

    @Deprecated
    public float get(String key) {
        ModifiersInstance instance = modifiersMap.get(key);
        return instance == null ? 1 : instance.get(1.0F);
    }

    public static SpellModifiers fromTag(CompoundTag tag) {
        SpellModifiers modifiers = new SpellModifiers();
        for (String key : tag.getAllKeys())
            modifiers.modifiersMap.put(key, ModifiersInstance.load(tag.getCompound(key)));
        return modifiers;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        modifiersMap.forEach((key, inst) -> tag.put(key, inst.save()));
        return tag;
    }

    /// Retrieves the complete map of all multipliers, including those that do not require syncing.
    ///
    /// @return A map containing all modifier identifiers and their corresponding multiplier values.
    public Map<String, ModifiersInstance> getMultipliers() {
        return modifiersMap;
    }

    /// Resets all multipliers and synced multipliers, clearing all stored values, including those that do not require
    /// syncing.
    public void reset() {
        modifiersMap.clear();
    }
}
