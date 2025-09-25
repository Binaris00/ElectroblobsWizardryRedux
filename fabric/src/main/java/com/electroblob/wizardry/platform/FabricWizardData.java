package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.api.MinionData;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.cca.EBFabricComponents;
import com.electroblob.wizardry.cca.FabricPlayerWizardDataHolder;
import com.electroblob.wizardry.core.platform.services.IWizardData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FabricWizardData implements IWizardData {

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
    public void onWizardDataUpdate(PlayerWizardData wizardData, Player player) {
        player.getComponent(EBFabricComponents.WIZARD_DATA).onSync(wizardData);
    }

    @Override
    public MinionData getMinionData(Mob mob) {
        return mob.getComponent(EBFabricComponents.MINION_DATA).getMinionData();
    }

    @Override
    public void onMinionDataUpdate(MinionData data, Mob entity) {
        entity.getComponent(EBFabricComponents.MINION_DATA).getMinionData().copyFrom(data);
        EBFabricComponents.MINION_DATA.sync(entity);
    }
}
