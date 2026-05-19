package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.Elements;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.binaris.wizardry.core.ArtifactUtils.handleLightningEffect;
import static com.binaris.wizardry.core.ArtifactUtils.meleeRing;

public class LightningMeleeEffect implements IArtifactEffect {
    @Override
    public void onHurtEntity(Player player, LivingEntity damagedEntity, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
        if (meleeRing(source, Elements.LIGHTNING)) {
            Optional<LivingEntity> nearestTarget = EntityUtil.getLivingWithinRadius(3, player.getX(), player.getY(), player.getZ(), player.level()).stream()
                    .filter(EntityUtil::isLiving)
                    .filter(e -> e != damagedEntity && e != player)
                    .findAny();

            handleLightningEffect(player, damagedEntity, damagedEntity);
            nearestTarget.ifPresent(e -> handleLightningEffect(player, e, damagedEntity));
        }
    }
}
