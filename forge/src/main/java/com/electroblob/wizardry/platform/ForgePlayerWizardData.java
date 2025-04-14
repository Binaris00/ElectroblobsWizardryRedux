package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.core.platform.services.IWizardPlayerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ForgePlayerWizardData implements IWizardPlayerData {
    @Override
    public PlayerWizardData getWizardData(Player player, Level level) {
        return com.electroblob.wizardry.capabilities.ForgePlayerWizardData.get(player).getWizardData();
    }

    @Override
    public void onUpdate(PlayerWizardData wizardData, Player player) {
        // TODO
    }
}
