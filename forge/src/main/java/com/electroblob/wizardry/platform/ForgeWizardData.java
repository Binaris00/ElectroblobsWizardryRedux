package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.api.MinionData;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.api.content.data.ConjureData;
import com.electroblob.wizardry.capabilities.ConjureDataHolder;
import com.electroblob.wizardry.capabilities.MinionDataHolder;
import com.electroblob.wizardry.capabilities.PlayerWizardDataHolder;
import com.electroblob.wizardry.core.platform.services.IWizardData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ForgeWizardData implements IWizardData {
    @Override
    public PlayerWizardData getWizardData(Player player, Level level) {
        return PlayerWizardDataHolder.get(player).getWizardData();
    }

    @Override
    public void onWizardDataUpdate(PlayerWizardData wizardData, Player player) {
        // TODO
    }

    @Override
    public MinionData getMinionData(Mob mob) {
        return MinionDataHolder.get(mob).getMinionData();
    }

    @Override
    public void onMinionDataUpdate(MinionData data, Mob entity) {
        // TODO
    }

    @Override
    public ConjureData getConjureData(ItemStack stack) {
        return stack.getCapability(ConjureDataHolder.INSTANCE).orElse(null);
    }
}
