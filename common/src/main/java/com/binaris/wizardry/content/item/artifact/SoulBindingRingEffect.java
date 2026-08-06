package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.core.ArtifactEffectContext;
import com.binaris.wizardry.content.spell.necromancy.CurseOfSoulbinding;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import com.google.common.util.concurrent.AtomicDouble;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class SoulBindingRingEffect implements IArtifactEffect {
    @Override
    public void onHurtEntity(LivingEntity user, LivingEntity damagedEntity, DamageSource source, AtomicDouble amount, AtomicBoolean canceled, ArtifactEffectContext context) {
        if (damagedEntity.level().isClientSide || !(user instanceof Player player)) return;
        damagedEntity.addEffect(new MobEffectInstance(EBMobEffects.CURSE_OF_SOULBINDING.get(), 400));
        CurseOfSoulbinding.getSoulboundCreatures(Services.OBJECT_DATA.getSpellManagerData(player)).add(damagedEntity.getUUID());
    }
}
