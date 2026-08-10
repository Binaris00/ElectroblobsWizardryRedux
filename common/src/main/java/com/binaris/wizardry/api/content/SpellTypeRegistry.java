package com.binaris.wizardry.api.content;

import com.binaris.wizardry.api.content.spell.SpellType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/// Utility registry for spell types, try to always use the registry inside the game initialization and not in runtime to avoid
/// weird issues
public final class SpellTypeRegistry {
    private static final Map<ResourceLocation, SpellType> REGISTRY = new HashMap<>();

    /// Registers a spell type inside the registry
    ///
    /// @param location The resource location of the spell type
    /// @param type The spell type to register
    ///
    /// @throws IllegalArgumentException If the spell type is already registered
    public static void register(ResourceLocation location, SpellType type) {
        if (REGISTRY.containsKey(location)) throw new IllegalArgumentException("Duplicate spell type registered: " + location);
        REGISTRY.put(location, type);
    }

    /// Gets a spell type by its resource location
    ///
    /// @param location The resource location of the spell type
    ///
    /// @return The spell type if found, null otherwise
    public static @Nullable SpellType get(ResourceLocation location) {
        return REGISTRY.get(location);
    }

    /// Gets the resource location of a spell type
    ///
    /// @param type The spell type
    ///
    /// @return The resource location of the spell type if found, null otherwise
    public static @Nullable ResourceLocation getLocation(SpellType type) {
        return REGISTRY.entrySet().stream()
                .filter(entry -> entry.getValue() == type)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    /// Gets all spell types registered
    ///
    /// @return A map of resource locations to spell types
    public static Map<ResourceLocation, SpellType> getAll() {
        return REGISTRY;
    }

    private SpellTypeRegistry() {
    }
}