package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.effect.CurseMobEffect;
import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CureEffects extends BuffSpell {
    public CureEffects() {
        super(0.8f, 0.8f, 1);
        this.soundValues(0.7f, 1.2f, 0.4f);
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity caster) {
        if (caster.getActiveEffects().isEmpty()) return false;

        boolean flag = false;
        // Iterate over a copy to avoid ConcurrentModificationException when removing effects
        List<MobEffectInstance> effectsCopy = new ArrayList<>(caster.getActiveEffects());

        for (MobEffectInstance effect : effectsCopy) {
            if (!(effect.getEffect() instanceof CurseMobEffect)) {
                caster.removeEffect(effect.getEffect());
                flag = true;
            }
        }

        return flag;

    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.HEALING, SpellType.DEFENCE, SpellAction.POINT_UP, 25, 10, 40)
                .build();
    }
}
