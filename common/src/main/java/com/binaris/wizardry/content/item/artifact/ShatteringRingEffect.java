package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.content.entity.projectile.IceShardEntity;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class ShatteringRingEffect implements IArtifactEffect {

    @Override
    public void onHurtEntity(Player player, LivingEntity damagedEntity, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ItemStack artifact) {
        if (player.level().random.nextFloat() < 0.15f && damagedEntity.getHealth() < 12f
                && damagedEntity.hasEffect(EBMobEffects.FROST.get()) && !source.isIndirect()) {

            amount.set(12f);

            for (int i = 0; i < 8; i++) {
                double dx = damagedEntity.level().random.nextDouble() - 0.5;
                double dy = damagedEntity.level().random.nextDouble() - 0.5;
                double dz = damagedEntity.level().random.nextDouble() - 0.5;
                IceShardEntity iceshard = new IceShardEntity(damagedEntity.level());
                iceshard.setPos(damagedEntity.xo + dx + Math.signum(dx) * damagedEntity.getBbWidth(), damagedEntity.yo + damagedEntity.getBbHeight() / 2 + dy, damagedEntity.zo + dz + Math.signum(dz) * damagedEntity.getBbWidth());
                iceshard.setDeltaMovement(dx * 1.5, dy * 1.5, dz * 1.5);
                iceshard.setOwner(player);
                player.level().addFreshEntity(iceshard);
            }
        }
    }
}
