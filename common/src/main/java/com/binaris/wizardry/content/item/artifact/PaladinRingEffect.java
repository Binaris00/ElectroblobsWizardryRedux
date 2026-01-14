package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.client.ParticleBuilder;
import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.util.EntityUtil;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.healing.GreaterHeal;
import com.binaris.wizardry.content.spell.healing.Heal;
import com.binaris.wizardry.content.spell.healing.HealAlly;
import com.binaris.wizardry.core.AllyDesignation;
import com.binaris.wizardry.core.IArtefactEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PaladinRingEffect implements IArtefactEffect {

    @Override
    public void onSpellPostCast(SpellCastEvent.Post event, ItemStack stack) {
        if (!(event.getCaster() instanceof Player player)) return;

        if (event.getSpell() instanceof Heal || event.getSpell() instanceof HealAlly || event.getSpell() instanceof GreaterHeal) {
            float healthGained = event.getSpell().property(DefaultProperties.HEALTH) * event.getModifiers().get(SpellModifiers.POTENCY);

            EntityUtil.getLivingWithinRadius(4, player.xo, player.yo, player.zo, event.getLevel()).stream().filter(livingEntity -> AllyDesignation.isAllied(player, livingEntity) && livingEntity.getHealth() > 0 && livingEntity.getHealth() < livingEntity.getMaxHealth()).forEach(livingEntity -> {
                livingEntity.heal(healthGained * 0.2f);
                if (event.getLevel().isClientSide) ParticleBuilder.spawnHealParticles(event.getLevel(), livingEntity);
            });
        }
    }
}
