package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.api.content.event.EBLivingHurtEvent;
import com.electroblob.wizardry.content.spell.necromancy.CurseOfSoulbinding;
import com.electroblob.wizardry.core.IArtefactEffect;
import com.electroblob.wizardry.core.platform.Services;
import com.electroblob.wizardry.setup.registries.EBMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SoulBindingRingEffect implements IArtefactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent e, ItemStack s) {
        if (!e.getDamagedEntity().level().isClientSide) return;
        e.getDamagedEntity().addEffect(new MobEffectInstance(EBMobEffects.CURSE_OF_SOULBINDING.get(), 400));
        CurseOfSoulbinding.getSoulboundCreatures(Services.OBJECT_DATA.getSpellManagerData((Player) e.getSource().getEntity())).add(e.getDamagedEntity().getUUID());
    }
}
