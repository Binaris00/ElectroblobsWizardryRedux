package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.content.entity.construct.FireRingConstruct;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FireCloakingAmuletEffect implements IArtifactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        List<FireRingConstruct> fireRings = player.level().getEntitiesOfClass(FireRingConstruct.class, player.getBoundingBox());

        for (FireRingConstruct fireRing : fireRings) {
            if (fireRing.getCaster() instanceof Player && (fireRing.getCaster() == player || AllyDesignation.isOwnerAlly(player, fireRing))) {
                event.setAmount(event.getAmount() * 0.25f);
                break;
            }
        }

    }
}
