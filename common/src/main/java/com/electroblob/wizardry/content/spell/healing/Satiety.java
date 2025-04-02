package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class Satiety extends BuffSpell {
    public Satiety() {
        super(1, 0.7f, 0.3f);
    }

    @Override
    protected boolean applyEffects(LivingEntity caster) {
        return true;
    }

    @Override
    protected void perform(Caster caster) {
        if(caster instanceof Player player && player.getFoodData().needsFood()){
            player.getFoodData().eat(property(ReplenishHunger.HUNGER_POINTS), property(ReplenishHunger.SATURATION_MODIFIER));
        }
        super.perform(caster);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(ReplenishHunger.HUNGER_POINTS, 16)
                .add(ReplenishHunger.SATURATION_MODIFIER, 0.1F)
                .build();
    }
}
