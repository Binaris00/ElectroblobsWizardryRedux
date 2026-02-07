package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.content.spell.necromancy.CurseOfSoulbinding;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.core.platform.Services;
import com.binaris.wizardry.setup.registries.EBMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SoulBindingRingEffect implements IArtifactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent e, ItemStack s) {
        if (!e.getDamagedEntity().level().isClientSide) return;
        e.getDamagedEntity().addEffect(new MobEffectInstance(EBMobEffects.CURSE_OF_SOULBINDING.get(), 400));
        CurseOfSoulbinding.getSoulboundCreatures(Services.OBJECT_DATA.getSpellManagerData((Player) e.getSource().getEntity())).add(e.getDamagedEntity().getUUID());
    }
}
