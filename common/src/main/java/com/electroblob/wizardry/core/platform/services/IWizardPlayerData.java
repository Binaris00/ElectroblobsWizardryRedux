package com.electroblob.wizardry.core.platform.services;

import com.electroblob.wizardry.api.PlayerWizardData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IWizardPlayerData {
    PlayerWizardData getWizardData(Player player, Level level);

    void onUpdate(PlayerWizardData wizardData, Player player);
}
