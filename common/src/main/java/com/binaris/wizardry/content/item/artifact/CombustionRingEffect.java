package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.EBDamageSources;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CombustionRingEffect implements IArtifactEffect {
    @Override
    public void onKillEntity(Player player, LivingEntity deadEntity, DamageSource source, ItemStack artifact) {
        if (!(source.is(EBDamageSources.FIRE))) return;
        deadEntity.level().explode(deadEntity, deadEntity.xo, deadEntity.yo + 1, deadEntity.zo, 2.0f, Level.ExplosionInteraction.NONE);
    }
}
