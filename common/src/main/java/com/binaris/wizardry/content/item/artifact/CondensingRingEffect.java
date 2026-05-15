package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CondensingRingEffect implements IArtifactEffect {
    public static final int MANA_RECHARGE_INTERVAL_TICKS = 150;

    @Override
    public void onTick(Player player, Level level, ItemStack artifact) {
        if (player.tickCount % MANA_RECHARGE_INTERVAL_TICKS != 0) return;

        InventoryUtil.getHotbar(player).stream()
                .filter(st -> st.getItem() instanceof IManaItem)
                .forEach(st -> ((IManaItem) st.getItem()).rechargeMana(st, 1));
    }
}
