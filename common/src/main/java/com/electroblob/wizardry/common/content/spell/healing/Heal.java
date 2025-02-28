package com.electroblob.wizardry.common.content.spell.healing;

import com.electroblob.wizardry.common.content.spell.abstr.BuffSpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class Heal extends BuffSpell {
    public Heal() {
        super(1, 1, 0.3f);
    }

    @Override
    protected boolean applyEffects(LivingEntity caster) {
        if(caster.getHealth() < caster.getMaxHealth() && caster.getHealth() > 0){
            heal(caster, 10);
            return true;
        }

        return false;
    }

    public static void heal(LivingEntity entity, float health){

        float excessHealth = entity.getHealth() + health - entity.getMaxHealth();

        entity.heal(health);

        if(excessHealth > 0 && entity instanceof Player){
            entity.setAbsorptionAmount(excessHealth);
        }

    }
}
