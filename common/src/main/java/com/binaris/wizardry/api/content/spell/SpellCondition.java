package com.binaris.wizardry.api.content.spell;

import com.binaris.wizardry.core.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of conditions that can be tested against a {@link Spell}.
 * This record encapsulates a list of {@link SingleCondition} predicates that must all be satisfied
 * for the condition to pass.
 */
public record SpellCondition(List<SingleCondition> conditions) {
    public enum Type {
        /** Condition based on the spell's element. */
        ELEMENT,
        /** Condition based on the spell's tier. */
        TIER,
        /** Condition based on the spell's type. */
        SPELL_TYPE,
        /** Condition based on the specific spell. */
        SPELL;

        /**
         * Converts the enum name to a lowercase string key without underscores. For example, SPELL_TYPE becomes "spelltype".
         *
         * @return the string key representation of this type.
         */
        public String key() {
            return name().toLowerCase().replace("_", "");
        }

        /**
         * Parses a string key back to a {@link Type} enum value.
         *
         * @param key the string key to parse.
         * @return the corresponding {@link Type}, or null if not found.
         */
        public static @Nullable Type fromKey(String key) {
            for (Type t : values()) {
                if (t.key().equalsIgnoreCase(key.trim())) return t;
            }
            return null;
        }
    }

    /**
     * A single condition predicate consisting of a type and a value. This record tests whether a given spell matches
     * the specified condition.
     */
    public record SingleCondition(Type type, ResourceLocation value) {
        public boolean test(Spell spell) {
            return switch (type) {
                case ELEMENT -> value.equals(spell.getElement().getLocation());
                case TIER -> value.equals(spell.getTier().getLocation());
                case SPELL_TYPE -> value.toString().equals(spell.getType().getName());
                case SPELL -> value.equals(spell.getLocation());
            };
        }

        /** Saves this condition to the provided NBT compound tag. */
        public void save(CompoundTag nbt) {
            nbt.putString(type.key(), value.toString());
        }
    }

    /**
     * Tests if the given spell satisfies all conditions in this {@link SpellCondition}.
     *
     * @param spell the spell to test.
     * @return true if all conditions are met, false otherwise.
     */
    public boolean test(Spell spell) {
        for (SingleCondition c : conditions) {
            if (!c.test(spell)) return false;
        }
        return true;
    }

    /**
     * Tests if this {@link SpellCondition} is a subset or equals of the given condition. That is, all conditions in
     * this instance are present in the other condition.
     *
     * @param condition the other condition to compare against.
     * @return true if this condition is a subset or equals, false otherwise.
     */
    public boolean test(SpellCondition condition) {
        for (SingleCondition entry : this.conditions) {
            if (!condition.conditions().contains(entry)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this condition has no single conditions.
     *
     * @return true if the conditions list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return conditions.isEmpty();
    }

    /**
     * Loads a {@link SpellCondition} from the provided NBT compound tag.
     *
     * @param nbt the NBT compound tag to load from.
     * @return the loaded {@link SpellCondition}, or null if no conditions were found.
     */
    public static @Nullable SpellCondition load(CompoundTag nbt) {
        List<SingleCondition> list = new ArrayList<>();
        for (Type type : Type.values()) {
            if (nbt.contains(type.key())) {
                ResourceLocation rl = ResourceLocation.tryParse(nbt.getString(type.key()));
                if (rl != null) list.add(new SingleCondition(type, rl));
            }
        }
        return list.isEmpty() ? null : new SpellCondition(list);
    }

    /**
     * Saves this {@link SpellCondition} to the provided NBT compound tag.
     *
     * @param nbt the NBT compound tag to save to.
     * @return the modified NBT compound tag. (Or same nbt tag if there isn't any conditions)
     */
    public CompoundTag save(CompoundTag nbt) {
        for (SingleCondition c : conditions) c.save(nbt);
        return nbt;
    }

    /**
     * Parses a string representation of conditions into a {@link SpellCondition}. The string should be in the format
     * "key1=value1,key2=value2,...". Valid keys are defined by {@link Type#key()}.
     *
     * @param raw the raw string to parse.
     * @return the parsed {@link SpellCondition}, or null if the string is blank or no conditions are parsed.
     * @throws IllegalArgumentException if the string format is invalid or contains unknown keys/values.
     */
    public static @Nullable SpellCondition parse(String raw) {
        if (raw == null || raw.isBlank()) return null;

        List<SingleCondition> list = new ArrayList<>();
        String[] pairs = raw.split(",");

        for (String pair : pairs) {
            pair = pair.trim();
            if (pair.isEmpty()) continue;

            int eq = pair.indexOf('=');
            if (eq < 0)
                throw new IllegalArgumentException("Invalid condition pair (missing '='): '%s'".formatted(pair));

            String key = pair.substring(0, eq).trim();
            String value = pair.substring(eq + 1).trim();

            Type type = Type.fromKey(key);
            if (type == null)
                throw new IllegalArgumentException("Unknown condition key: '%s'. Valid keys: %s".formatted(key, validKeysString()));

            ResourceLocation rl = ResourceLocation.tryParse(value);
            if (rl == null) throw new IllegalArgumentException("Invalid ResourceLocation value: '%s'".formatted(value));

            switch (type) {
                case ELEMENT -> {
                    if (Services.REGISTRY_UTIL.getElement(rl) == null)
                        throw new IllegalArgumentException("Invalid Element provided: '%s' isn't registered".formatted(value));
                }
                case SPELL -> {
                    if (Services.REGISTRY_UTIL.getSpell(rl) == null)
                        throw new IllegalArgumentException("Invalid Spell provided: '%s' isn't registered".formatted(value));
                }
                case TIER -> {
                    if (Services.REGISTRY_UTIL.getTier(rl) == null)
                        throw new IllegalArgumentException("Invalid Spell Tier provided: '%s' isn't registered".formatted(value));
                }
                case SPELL_TYPE -> {
                    if (SpellType.fromLocation(rl) == null)
                        throw new IllegalArgumentException("Invalid Spell Type provided: '%s' isn't registered".formatted(value));
                }
            }

            list.add(new SingleCondition(type, rl));
        }

        return list.isEmpty() ? null : new SpellCondition(list);
    }

    /**
     * Formats the {@link Type} values to a string representation.
     *
     * @return the {@link Type} values on a string list
     */
    private static String validKeysString() {
        StringBuilder sb = new StringBuilder();
        for (Type t : Type.values()) sb.append(t.key()).append(", ");
        return sb.substring(0, sb.length() - 2);
    }
}