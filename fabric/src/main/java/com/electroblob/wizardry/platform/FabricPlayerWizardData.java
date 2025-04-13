package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.FabricWizardData;
import com.electroblob.wizardry.api.content.hell.BinWizardDataInternal;
import com.electroblob.wizardry.core.platform.services.IWizardPlayerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FabricPlayerWizardData implements IWizardPlayerData {

    @Override
    public BinWizardDataInternal getWizardData(Player player, Level level) {
        if(level.isClientSide) throw new RuntimeException("Don't try to access wizard data in client");
        //MinecraftServer server = level.getServer();
        return FabricWizardData.getPlayerState(player);
    }
}
