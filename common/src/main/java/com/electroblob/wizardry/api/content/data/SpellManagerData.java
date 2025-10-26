package com.electroblob.wizardry.api.content.data;

import com.electroblob.wizardry.api.content.enchantment.Imbuement;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.util.ImbuementLoader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

public interface SpellManagerData {

    /**
     * Synchronises this SpellManagerData with the client.
     */
    void sync();

    /**
     * Retrieves the value of a specific spell variable.
     *
     * @param var the spell variable to retrieve
     * @param <T> the type of the spell variable
     * @return the value of the specified spell variable
     */
    <T> T getVariable(ISpellVar<T> var);

    /**
     * Sets the value of a specific spell variable.
     *
     * @param variable the spell variable to set
     * @param value    the value to set for the spell variable
     * @param <T>      the type of the spell variable
     */
    <T> void setVariable(ISpellVar<? super T> variable, T value);

    /**
     * Returns a map of all spell variables and their corresponding values.
     *
     * @return a Map containing all spell variables and their values
     */
    Map<ISpellVar, Object> getSpellData();

    /**
     * Checks if the given spell has been discovered by the player.
     *
     * @param spell the Spell to check
     * @return true if the spell has been discovered, false otherwise
     */
    boolean hasSpellBeenDiscovered(Spell spell);

    /**
     * Marks the given spell as discovered for the player.
     *
     * @param spell the Spell to mark as discovered
     * @return true if the spell was not already discovered, false otherwise
     */
    boolean discoverSpell(Spell spell);

    /**
     * Sets the duration of a given Imbuement on a ItemStack. This adds a
     * new ImbuementLoader to the list of imbuementLoaders and a new tag to
     * the ItemStack for saving the UUID of the ImbuementLoader.
     *
     * @param stack       the ItemStack to modify
     * @param enchantment the Imbuement to add
     * @param duration    the duration of the Imbuement in ticks
     * @throws IllegalArgumentException if the given enchantment is not an Imbuement
     */
    void setImbuementDuration(ItemStack stack, Enchantment enchantment, int duration);

    /**
     * Gets the duration of the Imbuement on the ItemStack.
     * If no matching Imbuement is found, returns 0.
     *
     * @param stack       the ItemStack to check for the Imbuement
     * @param enchantment the Enchantment
     * @return the duration of the Imbuement in ticks, or 0 if no matching Imbuement is found
     */
    int getImbuementDuration(ItemStack stack, Enchantment enchantment);

    /**
     * Gets the duration of the first Imbuement found in the list of ImbuementLoaders that matches the given Enchantment.
     * If no matching Imbuement is found, returns 0. <br>
     * If you want to get the duration of a specific item, use {@link #getImbuementDuration(ItemStack, Enchantment)}
     *
     * @param enchantment the Enchantment to search for
     * @return the duration of the Imbuement in ticks, or 0 if no matching Imbuement is found
     */
    int getGeneralImbuementDuration(Enchantment enchantment);

    /**
     * Returns the list of ImbuementLoaders currently active.
     *
     * @return a List of ImbuementLoaders
     */
    List<ImbuementLoader> getImbuementLoaders();

    /**
     * Removes the Imbuement from the given ItemStack.
     * If the Imbuement is found and removed, this method will also remove the corresponding ImbuementLoader
     * from the list of ImbuementLoaders.
     * If the Imbuement is not found, this method will do nothing and return false.
     *
     * @param stack       the ItemStack to remove the Imbuement from
     * @param enchantment the Enchantment to remove
     * @return true if the Imbuement was found and removed, false otherwise
     */
    boolean removeImbuement(ItemStack stack, Enchantment enchantment);

    /**
     * <b>Internal use only.</b> <br><br>
     * This method is called when an Imbuement's time limit has been reached.
     * It goes through the player's inventory, armor, and offhand items, and
     * removes the Imbuement from the first item it finds that matches the given
     * ImbuementLoader. It then calls the {@link Imbuement#onImbuementRemoval(ItemStack)}
     * on that item.
     *
     * @param loader the ImbuementLoader to remove from the player's inventory
     */
    void removeImbuement(ImbuementLoader loader);
}


