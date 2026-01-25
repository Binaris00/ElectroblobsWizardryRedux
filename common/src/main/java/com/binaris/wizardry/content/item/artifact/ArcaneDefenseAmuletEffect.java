package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.core.IArtefactEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArcaneDefenseAmuletEffect implements IArtefactEffect {
    public static final int MANA_RECHARGE_INTERVAL_TICKS = 300;

    @Override
    public void onTick(LivingEntity entity, Level level, ItemStack stack) {
        if (!(entity instanceof Player player)) return;

        if (player.tickCount % MANA_RECHARGE_INTERVAL_TICKS == 0) {
            for (ItemStack armorSlot : player.getArmorSlots()) {
                if (armorSlot.getItem() instanceof IManaStoringItem)
                    ((IManaStoringItem) armorSlot.getItem()).rechargeMana(armorSlot, 1);
            }
        }
    }
}