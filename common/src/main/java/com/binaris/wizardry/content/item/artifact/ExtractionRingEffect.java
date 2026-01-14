package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.item.IManaStoringItem;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.core.IArtefactEffect;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ExtractionRingEffect implements IArtefactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getSource().is(EBDamageSources.SORCERY))) return;

        InventoryUtil.getHotBarAndOffhand(player).stream()
                .filter(s -> s.getItem() instanceof IManaStoringItem && !((IManaStoringItem) s.getItem()).isManaFull(s))
                .findFirst()
                .ifPresent(s -> ((IManaStoringItem) s.getItem()).rechargeMana(s, 4 + player.level().random.nextInt(3)));
    }
}
