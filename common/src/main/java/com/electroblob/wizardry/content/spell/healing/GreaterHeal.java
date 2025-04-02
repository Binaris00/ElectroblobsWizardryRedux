package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import net.minecraft.world.entity.LivingEntity;

public class GreaterHeal extends BuffSpell {
    public GreaterHeal() {
        super(1, 1, 0.3f);
        this.soundValues(0.7f, 1.2f, 0.4f);
    }

    @Override
    protected boolean applyEffects(LivingEntity caster) {
        if (caster.getHealth() < caster.getMaxHealth() && caster.getHealth() > 0) {
            Heal.heal(caster, property(DefaultProperties.HEALTH));
            return true;
        }

        return super.applyEffects(caster);
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder().add(DefaultProperties.HEALTH, 8F).build();
    }
}
