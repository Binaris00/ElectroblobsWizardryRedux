package com.electroblob.wizardry.core.platform.services;

import com.electroblob.wizardry.api.content.hell.BinWizardDataInternal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface IWizardPlayerData {
    BinWizardDataInternal getWizardData(Player player, Level level);
}
