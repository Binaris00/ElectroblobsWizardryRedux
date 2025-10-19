package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.api.ConjureItemData;
import com.electroblob.wizardry.api.MinionData;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.capabilities.ForgeConjureItemData;
import com.electroblob.wizardry.capabilities.ForgeMinionData;
import com.electroblob.wizardry.capabilities.ForgePlayerWizardData;
import com.electroblob.wizardry.core.platform.services.IWizardData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ForgeWizardData implements IWizardData {
    @Override
    public PlayerWizardData getWizardData(Player player, Level level) {
        return ForgePlayerWizardData.get(player).getWizardData();
    }

    @Override
    public void onWizardDataUpdate(PlayerWizardData wizardData, Player player) {
        // TODO
    }

    @Override
    public MinionData getMinionData(Mob mob) {
        return ForgeMinionData.get(mob).getMinionData();
    }

    @Override
    public ConjureItemData getConjureItemData(ItemStack itemStack) {
        ForgeConjureItemData data = ForgeConjureItemData.get(itemStack);
        return data != null ? data.getConjureItemData() : null;
    }

    @Override
    public void onConjureItemDataUpdate(ConjureItemData data, ItemStack itemStack) {}

    @Override
    public void onMinionDataUpdate(MinionData data, Mob entity) {
        // TODO
    }
}
