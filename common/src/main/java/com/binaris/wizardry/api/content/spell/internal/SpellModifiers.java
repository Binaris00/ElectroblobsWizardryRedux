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
/// If you try to add a new modifier that's not on the original mod (e.g. custom wand upgrades/modifiers) make sure to
/// mark them as needing syncing if you want the client to be aware of them and of course make the needed implementations
/// on your spell casting code to make use of them.
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
        instance.addModifier(Operation.MULTIPLY_TOTAL, factor - 1.0f);
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

    /// Combines this [SpellModifiers] instance with another by multiplying their corresponding modifier values.
    /// If a modifier exists in either instance, it will be included in the result. The syncing status of each modifier
    /// is preserved if it exists in either instance.
    ///
    /// @param modifiers The other [SpellModifiers] instance to combine with.
    /// @return This [SpellModifiers] instance after combining.
    public SpellModifiers combine(SpellModifiers modifiers) {
        Set<String> allKeys = new HashSet<>(this.modifiersMap.keySet());
        allKeys.addAll(modifiers.modifiersMap.keySet());
        for (String key : allKeys) {
            float product = this.get(key, 1.0F) * modifiers.get(key, 1.0F);
            ModifiersInstance fresh = new ModifiersInstance();
            fresh.setBaseValue(product);
            this.modifiersMap.put(key, fresh);
        }
        return this;
    }

    @Deprecated
    public float get(String key) {
        ModifiersInstance instance = modifiersMap.get(key);
        return instance == null ? 1 : instance.get();
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
