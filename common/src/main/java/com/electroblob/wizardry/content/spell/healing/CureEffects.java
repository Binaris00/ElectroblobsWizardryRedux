package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.effect.CurseMobEffect;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Tiers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

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

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(Tiers.APPRENTICE, Elements.HEALING, SpellType.DEFENCE, SpellAction.POINT_UP, 25, 10, 40)
                .build();
    }
}
