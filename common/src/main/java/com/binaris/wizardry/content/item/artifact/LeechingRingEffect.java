package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.EBLivingHurtEvent;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.core.IArtefactEffect;
import com.binaris.wizardry.setup.registries.Spells;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class LeechingRingEffect implements IArtefactEffect {
    @Override
    public void onHurtEntity(EBLivingHurtEvent e, ItemStack s) {
        Player player = (Player) e.getSource().getEntity();
        if (player.getHealth() < player.getMaxHealth()) {
            float healFactor = Optional.ofNullable(Spells.LIFE_DRAIN.property(DefaultProperties.HEALTH)).map(Number::floatValue).orElse(0.5f);
            player.heal(e.getAmount() * healFactor);
        }
    }
}
