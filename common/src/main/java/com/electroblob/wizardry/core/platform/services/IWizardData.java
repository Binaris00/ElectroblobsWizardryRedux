package com.electroblob.wizardry.core.platform.services;

import com.electroblob.wizardry.api.MinionData;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.api.content.data.CastCommandData;
import com.electroblob.wizardry.api.content.data.ConjureData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface IWizardData {
    PlayerWizardData getWizardData(Player player, Level level);

    void onWizardDataUpdate(PlayerWizardData wizardData, Player player);

    MinionData getMinionData(Mob mob);

    void onMinionDataUpdate(MinionData data, Mob entity);

    /**
     * Gives you the conjure data to manipulate and view the current status of the conjure item.
     *
     * @return the conjure data, could return null if the item isn't part of ConjureItem list
     */
    @Nullable
    ConjureData getConjureData(ItemStack stack);

    /**
     * Gives you the cast command data to manipulate and view the current status of the cast command.
     *
     * @return the cast command data
     */
    CastCommandData getCastCommandData(Player player);
}
