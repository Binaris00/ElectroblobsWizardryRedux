package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.core.ArtifactEffectContext;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class CombustionRingEffect implements IArtifactEffect {
    @Override
    public void onKillEntity(LivingEntity user, LivingEntity deadEntity, DamageSource source, ArtifactEffectContext context) {
        if (source.is(EBDamageSources.FIRE)) {
            deadEntity.level().explode(deadEntity, deadEntity.getX(), deadEntity.getY() + 1, deadEntity.getZ(), 2.0f, Level.ExplosionInteraction.NONE);
        }
    }
}
