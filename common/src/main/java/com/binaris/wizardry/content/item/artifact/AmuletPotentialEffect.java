package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.core.ArtifactUtils;
import com.binaris.wizardry.core.IArtefactEffect;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AmuletPotentialEffect implements IArtefactEffect {
    public static float PROBABILITY_EFFECT = 0.2F;

    @Override
    public void onPlayerHurt(EBLivingHurtEvent event, ItemStack stack) {
        DamageSource e = event.getSource();
        if (event.getDamagedEntity() instanceof Player player && !e.isIndirect() && e.getDirectEntity() instanceof LivingEntity) {
            if (player.getRandom().nextFloat() < PROBABILITY_EFFECT) {
                ArtifactUtils.handleLightningEffect(player, (LivingEntity) e.getDirectEntity(), event);
            }
        }
    }
}
