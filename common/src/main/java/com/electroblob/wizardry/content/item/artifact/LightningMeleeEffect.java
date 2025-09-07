package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.core.IArtefactEffect;
import com.electroblob.wizardry.setup.registries.Elements;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.Optional;

import static com.electroblob.wizardry.core.ArtifactUtils.handleLightningEffect;
import static com.electroblob.wizardry.core.ArtifactUtils.meleeRing;

public class LightningMeleeEffect implements IArtefactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        if (meleeRing(event.getSource(), Elements.LIGHTNING)) {
            Optional<LivingEntity> nearestTarget = EntityUtil.getLivingWithinRadius(3, player.getX(), player.getY(), player.getZ(), player.level()).stream()
                    .filter(EntityUtil::isLiving)
                    .filter(e -> e != player)
                    .min(Comparator.comparingDouble(player::distanceToSqr));

            nearestTarget.ifPresent(target -> handleLightningEffect(player, target, event));
        }
    }
}
