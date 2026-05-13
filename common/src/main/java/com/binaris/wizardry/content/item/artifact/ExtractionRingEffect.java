package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingDeathEvent;
import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.item.IManaItem;
import com.binaris.wizardry.api.content.util.InventoryUtil;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ExtractionRingEffect implements IArtifactEffect {
    @Override
    public void onKillEntity(EBLivingDeathEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        if (!(event.getSource().is(EBDamageSources.SORCERY) || event.getSource().is(EBDamageSources.FORCE))) return;

        InventoryUtil.getHotBarAndOffhand(player).stream()
                .filter(s -> s.getItem() instanceof IManaItem && !((IManaItem) s.getItem()).isManaFull(s))
                .findFirst()
                .ifPresent(s -> ((IManaItem) s.getItem()).rechargeMana(s, 4 + player.level().random.nextInt(3)));
    }
}
