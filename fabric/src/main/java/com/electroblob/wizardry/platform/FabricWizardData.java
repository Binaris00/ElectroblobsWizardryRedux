package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.api.MinionData;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.api.content.data.CastCommandData;
import com.electroblob.wizardry.api.content.data.ConjureData;
import com.electroblob.wizardry.cca.EBComponents;
import com.electroblob.wizardry.cca.PlayerWizardDataHolder;
import com.electroblob.wizardry.content.spell.abstr.ConjureItemSpell;
import com.electroblob.wizardry.core.platform.services.IWizardData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class FabricWizardData implements IWizardData {

    @Override
    public PlayerWizardData getWizardData(Player player, Level level) {
        PlayerWizardDataHolder dataHolder = EBComponents.WIZARD_DATA.getNullable(player);
        if(dataHolder == null) {
            return new PlayerWizardData();
        } else {
            return dataHolder.getWizardData();
        }
    }

    @Override
    public void onWizardDataUpdate(PlayerWizardData wizardData, Player player) {
        player.getComponent(EBComponents.WIZARD_DATA).onSync(wizardData);
    }

    @Override
    public MinionData getMinionData(Mob mob) {
        return mob.getComponent(EBComponents.MINION_DATA).getMinionData();
    }

    @Override
    public void onMinionDataUpdate(MinionData data, Mob entity) {
        entity.getComponent(EBComponents.MINION_DATA).getMinionData().copyFrom(data);
        EBComponents.MINION_DATA.sync(entity);
    }

    @Override
    public @Nullable ConjureData getConjureData(ItemStack stack) {
        if(!ConjureItemSpell.isSupportedItem(stack.getItem())) return null;
        return EBComponents.CONJURE.get(stack);
    }

    @Override
    public CastCommandData getCastCommandData(Player player) {
        return EBComponents.CAST_COMMAND_DATA.get(player);
    }
}
