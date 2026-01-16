package com.binaris.wizardry.setup.registries;

import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class manages the registration and retrieval of wand upgrade items along with their unique identifiers. Wand upgrades
 * are just a different type of special item that can be used inside the {@link SpellModifiers} to modify spell cast
 * properties. Essentially this is just a util class to save items and then retrieve their string identifiers to be used
 * inside the {@link SpellModifiers}.
 * <p>
 * It provides methods to register new wand upgrades, retrieve their identifiers, and access the collection of
 * registered wand upgrades.
 */
public final class WandUpgrades {
    static final HashMap<Item, String> UPGRADES = new HashMap<>();

    /**
     * Initializes and registers all predefined wand upgrades. Avoid calling this method!! It is called automatically
     * during mod setup, and it shouldn't be needed by any addon/modder. In case you need to register your own wand upgrades
     * use the {@link #register(Item, String)} method.
     */
    public static void initUpgrades() {
        register(EBItems.CONDENSER_UPGRADE.get(), "condenser");
        register(EBItems.STORAGE_UPGRADE.get(), "storage");
        register(EBItems.SIPHON_UPGRADE.get(), "siphon");
        register(EBItems.RANGE_UPGRADE.get(), "range");
        register(EBItems.DURATION_UPGRADE.get(), "duration");
        register(EBItems.COOLDOWN_UPGRADE.get(), "cooldown");
        register(EBItems.BLAST_UPGRADE.get(), "blast");
        register(EBItems.ATTUNEMENT_UPGRADE.get(), "attunement");
        register(EBItems.MELEE_UPGRADE.get(), "melee");
    }

    /**
     * Registers a wand upgrade item with its corresponding identifier.
     *
     * @param upgrade    The wand upgrade item to register.
     * @param identifier The unique identifier string for the wand upgrade.
     * @throws IllegalArgumentException if the identifier is already registered (duplicate).
     */
    public static void register(Item upgrade, String identifier) {
        if (UPGRADES.containsValue(identifier))
            throw new IllegalArgumentException("Duplicate wand upgrade identifier: " + identifier);
        UPGRADES.put(upgrade, identifier);
    }

    /**
     * Retrieves the identifier associated with the given wand upgrade item.
     *
     * @param upgrade The wand upgrade item.
     * @return The identifier string for the wand upgrade.
     * @throws IllegalArgumentException if the wand upgrade is not registered.
     */
    public static String getIdentifier(Item upgrade) {
        for (Map.Entry<Item, String> entry : UPGRADES.entrySet()) {
            if (entry.getKey().equals(upgrade)) return entry.getValue();
        }
        throw new IllegalArgumentException("Wand upgrade not registered: " + upgrade);
    }

    /**
     * Retrieves the map of all registered wand upgrades including their identifiers (String value used in SpellModifiers).
     * If you need just the set of items use {@link #getSpecialUpgrades()} instead.
     *
     * @return A HashMap containing wand upgrade items and their identifiers.
     */
    public static HashMap<Item, String> getWandUpgrades() {
        return UPGRADES;
    }

    /**
     * Retrieves an unmodifiable set of all the registered wand upgrade items. If you need the identifiers as well,
     * use {@link #getWandUpgrades()} instead.
     *
     * @return An unmodifiable set of wand upgrade items.
     */
    public static Set<Item> getSpecialUpgrades() {
        return Collections.unmodifiableSet(getWandUpgrades().keySet());
    }

    /**
     * Checks if the given item is registered as a wand upgrade.
     *
     * @param upgrade The item to check.
     * @return True if the item is a registered wand upgrade, false otherwise.
     */
    public static boolean isWandUpgrade(Item upgrade) {
        return getWandUpgrades().keySet().stream().anyMatch(item -> item.equals(upgrade));
    }

    private WandUpgrades() {
    }
}
