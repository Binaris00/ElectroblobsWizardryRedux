package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArcaneDefenseAmuletEffect implements IArtifactEffect {
    public static final int MANA_RECHARGE_INTERVAL_TICKS = 300;

    @Override
    public void onTick(Player player, Level level, ItemStack artifact) {
        if (player.tickCount % MANA_RECHARGE_INTERVAL_TICKS != 0) return;

        for (ItemStack armorSlot : player.getArmorSlots()) {
            if (armorSlot.getItem() instanceof IManaItem manaItem)
                manaItem.rechargeMana(armorSlot, 1);
        }
    }
}