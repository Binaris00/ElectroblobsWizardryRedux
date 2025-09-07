package com.electroblob.wizardry.content.item.artifact;

import com.electroblob.wizardry.api.client.ParticleBuilder;
import com.electroblob.wizardry.api.content.event.SpellCastEvent;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.healing.GreaterHeal;
import com.electroblob.wizardry.content.spell.healing.Heal;
import com.electroblob.wizardry.content.spell.healing.HealAlly;
import com.electroblob.wizardry.core.AllyDesignationSystem;
import com.electroblob.wizardry.core.IArtefactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PaladinRingEffect implements IArtefactEffect {

    @Override
    public void onSpellPostCast(SpellCastEvent.Post event, ItemStack stack) {
        if (!(event.getCaster() instanceof Player player)) return;

        if (event.getSpell() instanceof Heal || event.getSpell() instanceof HealAlly || event.getSpell() instanceof GreaterHeal) {
            float healthGained = event.getSpell().property(DefaultProperties.HEALTH) * event.getModifiers().get(SpellModifiers.POTENCY);

            EntityUtil.getLivingWithinRadius(4, player.xo, player.yo, player.zo, event.getLevel()).stream().filter(livingEntity -> AllyDesignationSystem.isAllied(player, livingEntity) && livingEntity.getHealth() > 0 && livingEntity.getHealth() < livingEntity.getMaxHealth()).forEach(livingEntity -> {
                livingEntity.heal(healthGained * 0.2f);
                if (event.getLevel().isClientSide) ParticleBuilder.spawnHealParticles(event.getLevel(), livingEntity);
            });
        }
    }
}
