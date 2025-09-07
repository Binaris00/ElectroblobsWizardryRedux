package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.core.IArtefactEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArcaneDefenseAmuletEffect implements IArtefactEffect {
    @Override
    public void onTick(LivingEntity entity, Level level, ItemStack stack) {
        if (!(entity instanceof Player player)) return;

        if (player.tickCount % 300 == 0) {
            for (ItemStack armorSlot : player.getArmorSlots()) {
                if (armorSlot.getItem() instanceof IManaStoringItem)
                    ((IManaStoringItem) armorSlot.getItem()).rechargeMana(stack, 1);
            }
        }
    }
}