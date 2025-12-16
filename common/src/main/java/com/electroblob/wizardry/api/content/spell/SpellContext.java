package com.electroblob.wizardry.api.content.spell;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum representing different contexts in which a spell can be used. Each context has a unique identifier key
 * that is used inside these spell data files.
 * <p>
 * This allows for fine-grained control over where spells can appear or be used,
 * such as in spell books, scrolls, wands, NPC trades, treasure loot, etc.
 */
public enum SpellContext {
    /** Whether the spell can appear in spell books (crafted or found) */
    BOOK("book"),
    
    /** Whether the spell can appear in spell scrolls */
    SCROLL("scroll"),
    
    /** Whether the spell can be used/bound to wands */
    WANDS("wands"),
    
    /** Whether NPCs can cast this spell */
    NPCS("npcs"),
    
    /** Whether the spell can be cast from dispensers */
    DISPENSERS("dispensers"),
    
    /** Whether the spell can be cast via commands */
    COMMANDS("commands"),
    
    /** Whether the spell can appear in treasure/loot chests */
    TREASURE("treasure"),
    
    /** Whether the spell can appear in villager/NPC trades */
    TRADES("trades"),
    
    /** Whether the spell can be obtained through looting */
    LOOTING("looting");

    private final String key;

    SpellContext(String key) {
        this.key = key;
    }

    /** Gets the string key used in spell data files */
    public String getKey() {
        return key;
    }

    /**
     * Gets a SpellContext from its string key
     * @param key The string key
     * @return The corresponding SpellContext, or null if not found
     */
    public static SpellContext fromKey(String key) {
        if (key == null) return null;
        for (SpellContext context : values()) {
            if (context.key.equals(key)) {
                return context;
            }
        }
        return null;
    }

    /**
     * Creates a map with all contexts set to the default value (true)
     * @return A map of all context keys to true
     */
    public static Map<String, Boolean> createDefaultMap() {
        Map<String, Boolean> map = new HashMap<>();
        for (SpellContext context : values()) {
            map.put(context.key, true);
        }
        return map;
    }

    /**
     * Gets an array of all context keys
     * @return Array of all context key strings
     */
    public static String[] getAllKeys() {
        return Arrays.stream(values())
                .map(SpellContext::getKey)
                .toArray(String[]::new);
    }

    /**
     * Checks if a given string is a valid context key
     * @param key The key to check
     * @return true if the key corresponds to a valid context
     */
    public static boolean isValidKey(String key) {
        return fromKey(key) != null;
    }

    @Override
    public String toString() {
        return key;
    }
}

