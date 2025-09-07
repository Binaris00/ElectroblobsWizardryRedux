package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.core.integrations.EBAccessoriesIntegration;
import com.electroblob.wizardry.setup.registries.EBItems;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class Heal extends BuffSpell {
    public Heal() {
        super(1, 1, 0.3f);
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity caster) {
        if(caster.getHealth() < caster.getMaxHealth() && caster.getHealth() > 0){
            heal(caster, property(DefaultProperties.HEALTH) * ctx.modifiers().get(SpellModifiers.POTENCY));
            return true;
        }

        return false;
    }

    public static void heal(LivingEntity entity, float health){
        float excessHealth = entity.getHealth() + health - entity.getMaxHealth();

        entity.heal(health);

        if(excessHealth > 0 && entity instanceof Player player && EBAccessoriesIntegration.isEquipped(player, EBItems.AMULET_ABSORPTION.get())){
            entity.setAbsorptionAmount(excessHealth);
        }
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.NOVICE, Elements.HEALING, SpellType.DEFENCE, SpellAction.POINT_UP, 5, 0, 20)
                .add(DefaultProperties.HEALTH, 4F).build();
    }
}
