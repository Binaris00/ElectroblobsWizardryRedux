package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.ForgeWizardData;
import com.electroblob.wizardry.api.content.hell.BinWizardDataInternal;
import com.electroblob.wizardry.core.platform.services.IWizardPlayerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ForgePlayerWizardData implements IWizardPlayerData {
    @Override
    public BinWizardDataInternal getWizardData(Player player, Level level) {
        return ForgeWizardData.get(player).getWizardData();
    }
}
