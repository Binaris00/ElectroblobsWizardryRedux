package com.electroblob.wizardry.core.platform.services;

import com.electroblob.wizardry.api.ConjureItemData;
import com.electroblob.wizardry.api.MinionData;
import com.electroblob.wizardry.api.PlayerWizardData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IWizardData {
    PlayerWizardData getWizardData(Player player, Level level);

    void onWizardDataUpdate(PlayerWizardData wizardData, Player player);

    MinionData getMinionData(Mob mob);

    ConjureItemData getConjureItemData(ItemStack itemStack);

    void onConjureItemDataUpdate(ConjureItemData data, ItemStack itemStack);

    void onMinionDataUpdate(MinionData data, Mob entity);
}
