package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.api.ConjureItemData;
import com.electroblob.wizardry.api.MinionData;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.cca.EBComponents;
import com.electroblob.wizardry.cca.ConjureItemDataHolder;
import com.electroblob.wizardry.cca.PlayerWizardDataHolder;
import com.electroblob.wizardry.core.platform.services.IWizardData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
    public ConjureItemData getConjureItemData(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if(!ConjureItemData.applyItem(stack.getItem())) return null;
        ConjureItemDataHolder holder = EBComponents.CONJURE_ITEM.get(stack);
        if (holder == null) return null;
        return holder.getConjureItemData();
    }

    @Override
    public void onConjureItemDataUpdate(ConjureItemData data, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        ConjureItemDataHolder holder = EBComponents.CONJURE_ITEM.get(stack);
        if (holder == null) return;

        // Copy primitive fields back into the component instance and persist into the stack NBT.
        ConjureItemData target = holder.getConjureItemData();
        target.setLifetime(data.getLifetime());
        target.summoned(data.isSummoned());
        target.setMaxLifetime(data.getMaxLifetime());
        holder.saveConjureItemData();
    }

    @Override
    public void onMinionDataUpdate(MinionData data, Mob entity) {
        entity.getComponent(EBComponents.MINION_DATA).getMinionData().copyFrom(data);
        EBComponents.MINION_DATA.sync(entity);
    }
}
