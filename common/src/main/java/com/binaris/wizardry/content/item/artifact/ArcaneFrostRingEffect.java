package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.content.entity.projectile.IceShardEntity;
import com.binaris.wizardry.core.ArtifactEffectContext;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ArcaneFrostRingEffect implements IArtifactEffect {
    @Override
    public void onKillEntity(LivingEntity user, LivingEntity deadEntity, DamageSource source, ArtifactEffectContext context) {
        if (user instanceof Player player && source.is(EBDamageSources.FROST)) {
            for (int i = 0; i < 8; i++) {
                double dx = deadEntity.level().random.nextDouble() - 0.5;
                double dy = deadEntity.level().random.nextDouble() - 0.5;
                double dz = deadEntity.level().random.nextDouble() - 0.5;
                IceShardEntity iceShard = new IceShardEntity(player.level());
                iceShard.setPos(deadEntity.xo + dx + Math.signum(dx) * deadEntity.getBbWidth(),
                        deadEntity.yo + deadEntity.getBbHeight() / 2 + dy,
                        deadEntity.zo + dz + Math.signum(dz) * deadEntity.getBbWidth());
                iceShard.setDeltaMovement(dx * 1.5, dy * 1.5, dz * 1.5);
                iceShard.setOwner(player);
                deadEntity.level().addFreshEntity(iceShard);
            }
        }
    }
}
