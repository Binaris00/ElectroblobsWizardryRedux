package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.core.IArtefactEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class LichAmuletEffect implements IArtefactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (player.level().random.nextFloat() < 0.15f) {
            List<LivingEntity> nearbyMobs = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5));
            // TODO SUMMONED CREATURE / MINIONS
            //nearbyMobs.removeIf(e -> !(e instanceof ISummonedCreature && ((ISummonedCreature) e).getCaster() == player));

            if (!nearbyMobs.isEmpty()) {
                Collections.shuffle(nearbyMobs);
                nearbyMobs.get(0).hurt(event.getSource(), event.getAmount());
                event.setCanceled(true);
            }
        }
    }
}
