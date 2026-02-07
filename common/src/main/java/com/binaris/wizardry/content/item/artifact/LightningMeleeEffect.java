package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.Elements;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

import static com.binaris.wizardry.core.ArtifactUtils.handleLightningEffect;
import static com.binaris.wizardry.core.ArtifactUtils.meleeRing;

public class LightningMeleeEffect implements IArtifactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent event, ItemStack stack) {
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        LivingEntity target = event.getDamagedEntity();

        if (meleeRing(event.getSource(), Elements.LIGHTNING)) {
            Optional<LivingEntity> nearestTarget = EntityUtil.getLivingWithinRadius(3, player.getX(), player.getY(), player.getZ(), player.level()).stream()
                    .filter(EntityUtil::isLiving)
                    .filter(e -> e != target && e != player)
                    .findAny();

            handleLightningEffect(player, target, event);
            nearestTarget.ifPresent(e -> handleLightningEffect(player, e, event));
        }
    }
}
