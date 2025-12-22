package com.electroblob.wizardry.core.platform.services;

import com.electroblob.wizardry.api.content.data.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface IObjectData {
    /**
     * Gives you the conjure data to manipulate and view the current status of the conjure item.
     *
     * @return the conjure data, could return null if the item isn't part of ConjureItem list
     */
    @Nullable
    ConjureData getConjureData(ItemStack stack);

    /**
     * Gives you the imbuement data to manipulate and view the current status of the imbuements on the item.
     *
     * @return the imbuement data
     */
    ImbuementEnchantData getImbuementData(ItemStack stack);

    /**
     * Gives you the cast command data to manipulate and view the current status of the cast command. Remember to
     * check if the player is alive before using this! If the player is dead (on death screen or respawning), this could
     * cause a crash, so if in doubt, check Player.isAlive().
     *
     * @param player player whose cast command data is to be retrieved.
     * @return the cast command data
     */
    CastCommandData getCastCommandData(Player player);

    /**
     * Gives you the spell manager data to manipulate and view the current status of spells and spell variables. Remember
     * to check if the player is alive before using this! If the player is dead (on death screen or respawning), this could
     * cause a crash, so if in doubt, check Player.isAlive().
     *
     * @param player player whose spell manager data is to be retrieved.
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
     * Gets the wizard data for the given player. Don't forget to check if the player is alive before using this! If
     * the player is dead (on death screen or respawning), this could cause a crash, so if in doubt, check Player.isAlive().
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
     * Gets the containment data for the given living entity.
     *
     * @param entity The living entity whose containment data is to be retrieved.
     * @return The ContainmentData associated with the living entity.
     */
    ContainmentData getContainmentData(LivingEntity entity);

    /**
     * Checks if the given entity is a minion.
     *
     * @param mob The entity to check.
     * @return true if the entity is a minion, false otherwise.
     */
    boolean isMinion(Entity mob);

    /**
     * Gets the arcane lock data for the given block entity.
     *
     * @param blockEntity The block entity whose arcane lock data is to be retrieved.
     * @return The ArcaneLockData associated with the block entity.
     */
    ArcaneLockData getArcaneLockData(BlockEntity blockEntity);
}
