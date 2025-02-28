package com.electroblob.wizardry.common.content.spell.healing;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.common.content.spell.abstr.BuffSpell;
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
        if(caster instanceof Player player){
            if(player.getFoodData().needsFood()){
                int foodAmount = 16;
                player.getFoodData().eat(foodAmount, 0.1F);
            }
        }
        super.perform(caster);
    }
}
