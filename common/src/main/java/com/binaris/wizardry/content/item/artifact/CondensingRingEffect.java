package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.core.IArtefactEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CondensingRingEffect implements IArtefactEffect {
    @Override
    public void onTick(LivingEntity entity, Level level, ItemStack stack) {
        if (!(entity instanceof Player player)) return;

        if (player.tickCount % 150 == 0) {
            InventoryUtil.getHotbar(player).stream()
                    .filter(st -> st.getItem() instanceof IManaStoringItem)
                    .forEach(st -> ((IManaStoringItem) st.getItem()).rechargeMana(st, 1));
        }
    }
}
