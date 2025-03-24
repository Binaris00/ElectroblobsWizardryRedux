package com.electroblob.wizardry.common.content.spell.healing;

import com.electroblob.wizardry.common.content.spell.abstr.BuffSpell;
import net.minecraft.world.entity.LivingEntity;

public class GreaterHeal extends BuffSpell {
    public GreaterHeal() {
        super(1, 1, 0.3f);
        this.soundValues(0.7f, 1.2f, 0.4f);
    }

    @Override
    protected boolean applyEffects(LivingEntity caster) {
        if (caster.getHealth() < caster.getMaxHealth() && caster.getHealth() > 0) {
            Heal.heal(caster, 8);
            return true;
        }

        return super.applyEffects(caster);
    }
}
