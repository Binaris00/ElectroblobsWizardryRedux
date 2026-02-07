package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.platform.Services;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class LichAmuletEffect implements IArtifactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (player.level().random.nextFloat() < 0.15f) {
            List<LivingEntity> nearbyMobs = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5));
            nearbyMobs.removeIf(e -> Services.OBJECT_DATA.isMinion(e) && Services.OBJECT_DATA.getMinionData((Mob) e).getOwner() == player);

            if (!nearbyMobs.isEmpty()) {
                Collections.shuffle(nearbyMobs);
                nearbyMobs.get(0).hurt(event.getSource(), event.getAmount());
                event.setCanceled(true);
            }
        }
    }
}
