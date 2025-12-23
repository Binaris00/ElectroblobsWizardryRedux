package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.item.IManaStoringItem;
import com.electroblob.wizardry.api.content.util.InventoryUtil;
import com.electroblob.wizardry.core.IArtefactEffect;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
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
