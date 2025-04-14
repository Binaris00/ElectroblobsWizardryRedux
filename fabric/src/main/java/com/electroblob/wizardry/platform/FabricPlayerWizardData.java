package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.cca.EBFabricComponents;
import com.electroblob.wizardry.cca.FabricPlayerWizardDataHolder;
import com.electroblob.wizardry.core.platform.services.IWizardPlayerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FabricPlayerWizardData implements IWizardPlayerData {

    @Override
    public PlayerWizardData getWizardData(Player player, Level level) {
        FabricPlayerWizardDataHolder dataHolder = EBFabricComponents.WIZARD_DATA.getNullable(player);
        if(dataHolder == null) {
            return new PlayerWizardData();
        } else {
            return dataHolder.getWizardData();
        }
    }

    @Override
    public void onUpdate(PlayerWizardData wizardData, Player player) {
        player.getComponent(EBFabricComponents.WIZARD_DATA).onSync(wizardData);
    }
}
