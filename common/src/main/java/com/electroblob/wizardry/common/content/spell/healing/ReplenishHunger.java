package com.electroblob.wizardry.common.content.spell.healing;

import com.electroblob.wizardry.api.common.spell.Caster;
import com.electroblob.wizardry.common.content.spell.abstr.BuffSpell;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ReplenishHunger extends BuffSpell {
    public ReplenishHunger() {
        super(1, 0.7f, 0.4f);
    }

    @Override
    protected boolean applyEffects(LivingEntity caster) {
        return true;
    }

    @Override
    protected void perform(Caster caster) {
        if(caster instanceof Player player){
            if(player.getFoodData().needsFood()){
                int foodAmount = 6;
                player.getFoodData().eat(foodAmount, 0.1F);
            }
        }
        super.perform(caster);
    }
}
