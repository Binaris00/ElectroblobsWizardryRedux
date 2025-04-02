package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.effect.CurseMobEffect;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class CureEffects extends BuffSpell {
    public CureEffects() {
        super(0.8f, 0.8f, 1);
    }

    @Override
    protected boolean applyEffects(LivingEntity caster) {
        if(!caster.getActiveEffects().isEmpty()){
            boolean flag = false;

            for(MobEffectInstance effect : caster.getActiveEffects()){
                if(!(effect.getEffect() instanceof CurseMobEffect)){
                    caster.removeEffect(effect.getEffect());
                    flag = true;
                }
            }

            return flag;
        }

        return false;
    }
}
