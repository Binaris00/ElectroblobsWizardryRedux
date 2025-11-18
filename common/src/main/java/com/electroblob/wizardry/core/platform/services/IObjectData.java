package com.electroblob.wizardry.core.platform.services;

import com.electroblob.wizardry.api.content.data.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IObjectData {
    /**
     * Gives you the conjure data to manipulate and view the current status of the conjure item.
     *
     * @return the conjure data, could return null if the item isn't part of ConjureItem list
     */
    @Nullable
    ConjureData getConjureData(ItemStack stack);

    /**
     * Gives you the temporary enchantment data to manipulate and view the current status of temporary enchantments.
     *
     * @return the temporary enchantment data, could return null if the item doesn't have temporary enchantments
     */
    @Nullable
    TemporaryEnchantmentData getTemporaryEnchantmentData(ItemStack stack);

    /**
     * Gives you the cast command data to manipulate and view the current status of the cast command.
     *
     * @return the cast command data
     */
    CastCommandData getCastCommandData(Player player);

    /**
     * Gives you the spell manager data to manipulate and view the current status of spells and spell variables.
     *
     * @return the spell manager data
     */
    SpellManagerData getSpellManagerData(Player player);

    /**
     * Registers one or more stored spell variables to this SpellHandlerData.
     * Stored spell variables are those that implement the IStoredSpellVar interface.
     *
     * @param variables one or more stored spell variables to register
     */
    void spellStoredVariables(IStoredSpellVar<?>... variables);

    /**
     * Gets the wizard data for the given player.
     *
     * @param player The player whose wizard data is to be retrieved.
     * @return The PlayerWizardData associated with the player.
     */
    WizardData getWizardData(Player player);

    /**
     * Gets the minion data holder for the given mob.
     *
     * @param mob The mob whose minion data holder is to be retrieved.
     * @return The MinionDataHolder associated with the mob.
     */
    MinionData getMinionData(Mob mob);

    /**
     * Checks if the given entity is a minion.
     *
     * @param mob The entity to check.
     * @return true if the entity is a minion, false otherwise.
     */
    boolean isMinion(Entity mob);
}
