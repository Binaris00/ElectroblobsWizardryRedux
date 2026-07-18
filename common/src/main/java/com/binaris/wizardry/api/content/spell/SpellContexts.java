package com.binaris.wizardry.api.content.spell;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/// Enum representing different contexts (the default ones for EBW) in which a spell can be used. Each context has a unique
/// identifier key that is used inside these spell data files.
///
/// This allows for fine-grained control over where spells can appear or be used, such as in spell books, scrolls, wands,
/// NPCs, dispensers, commands, treasure, trades, and looting.
///
/// In case you make your own contexts you need to implement the SpellContext interface and set the use in some of the
/// official events (e.g. spell book crafting, spell scrolls, wands, etc.)
public enum SpellContexts implements SpellContext {
    /// Whether the spell can appear in spell books (crafted or found)
    BOOK("book"),

    /// Whether the spell can appear in spell scrolls
    SCROLL("scroll"),

    /// Whether the spell can be used/bound to wands
    WANDS("wands"),

    /// Whether NPCs can cast this spell
    NPCS("npcs"),

    /// Whether the spell can be cast from dispensers
    DISPENSERS("dispensers"),

    /// Whether the spell can be cast via commands
    COMMANDS("commands"),

    /// Whether the spell can appear in treasure/loot chests
    TREASURE("treasure"),

    /// Whether the spell can appear in villager/NPC trades
    TRADES("trades"),

    /// Whether the spell can be obtained through looting
    LOOTING("looting");

    private final String key;

    SpellContexts(String key) {
        this.key = key;
    }

    /// Creates a map with all contexts set to the default value (true)
    ///
    /// @return A map of all context keys to true
    public static Map<String, Boolean> createDefaultMap() {
        Map<String, Boolean> map = new HashMap<>();
        for (SpellContexts context : values()) {
            map.put(context.key, true);
        }
        return map;
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public String getName() {
        return key;
    }
}

