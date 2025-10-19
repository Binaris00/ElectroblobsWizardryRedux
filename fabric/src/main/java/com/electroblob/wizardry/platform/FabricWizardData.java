package com.electroblob.wizardry.platform;

import com.electroblob.wizardry.api.ConjureItemData;
import com.electroblob.wizardry.api.MinionData;
import com.electroblob.wizardry.api.PlayerWizardData;
import com.electroblob.wizardry.cca.EBEntityComponents;
import com.electroblob.wizardry.cca.FabricConjureItemDataHolder;
import com.electroblob.wizardry.cca.FabricPlayerWizardDataHolder;
import com.electroblob.wizardry.core.platform.services.IWizardData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FabricWizardData implements IWizardData {

    @Override
    public PlayerWizardData getWizardData(Player player, Level level) {
        FabricPlayerWizardDataHolder dataHolder = EBEntityComponents.WIZARD_DATA.getNullable(player);
        if(dataHolder == null) {
            return new PlayerWizardData();
        } else {
            return dataHolder.getWizardData();
        }
    }

    @Override
    public void onWizardDataUpdate(PlayerWizardData wizardData, Player player) {
        player.getComponent(EBEntityComponents.WIZARD_DATA).onSync(wizardData);
    }

    @Override
    public MinionData getMinionData(Mob mob) {
        return mob.getComponent(EBEntityComponents.MINION_DATA).getMinionData();
    }

    @Override
    public ConjureItemData getConjureItemData(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        if(!ConjureItemData.applyItem(stack.getItem())) return null;
        FabricConjureItemDataHolder holder = EBEntityComponents.CONJURE_ITEM.get(stack);
        if (holder == null) return null;
        return holder.getConjureItemData();
    }

    @Override
    public void onConjureItemDataUpdate(ConjureItemData data, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        FabricConjureItemDataHolder holder = EBEntityComponents.CONJURE_ITEM.get(stack);
        if (holder == null) return;

        // Copy primitive fields back into the component instance and persist into the stack NBT.
        ConjureItemData target = holder.getConjureItemData();
        target.setLifetime(data.getLifetime());
        target.summoned(data.isSummoned());
        holder.saveConjureItemData();
    }

    @Override
    public void onMinionDataUpdate(MinionData data, Mob entity) {
        entity.getComponent(EBEntityComponents.MINION_DATA).getMinionData().copyFrom(data);
        EBEntityComponents.MINION_DATA.sync(entity);
    }
}
