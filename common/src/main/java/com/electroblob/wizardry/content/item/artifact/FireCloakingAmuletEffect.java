package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.content.entity.construct.FireRingConstruct;
import com.electroblob.wizardry.core.AllyDesignation;
import com.electroblob.wizardry.core.IArtefactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FireCloakingAmuletEffect implements IArtefactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        List<FireRingConstruct> fireRings = player.level().getEntitiesOfClass(FireRingConstruct.class, player.getBoundingBox());

        for (FireRingConstruct fireRing : fireRings) {
            if(fireRing.getCaster() instanceof Player && (fireRing.getCaster() == player || AllyDesignation.isOwnerAlly(player, fireRing))){
                event.setAmount(event.getAmount() * 0.25f);
                break;
            }
        }

    }
}
