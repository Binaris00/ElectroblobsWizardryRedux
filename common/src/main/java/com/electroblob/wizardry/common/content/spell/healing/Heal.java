package com.electroblob.wizardry.common.content.spell.healing;

import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperty;
import com.electroblob.wizardry.common.content.spell.abstr.BuffSpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class Heal extends BuffSpell {
    public static final SpellProperty<Float> HEALTH = SpellProperty.floatProperty("health");

    public Heal() {
        super(1, 1, 0.3f);
    }

    @Override
    protected boolean applyEffects(LivingEntity caster) {
        if(caster.getHealth() < caster.getMaxHealth() && caster.getHealth() > 0){
            heal(caster, property(HEALTH));
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

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder().add(HEALTH, 4F).build();
    }
}
