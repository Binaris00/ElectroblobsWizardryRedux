package com.electroblob.wizardry.content.spell.healing;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.internal.CastContext;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.BuffSpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class GreaterHeal extends BuffSpell {
    public GreaterHeal() {
        super(1, 1, 0.3f);
        this.soundValues(0.7f, 1.2f, 0.4f);
    }

    @Override
    protected boolean applyEffects(CastContext ctx, LivingEntity caster) {
        if (caster.getHealth() < caster.getMaxHealth() && caster.getHealth() > 0) {
            Heal.heal(caster, property(DefaultProperties.HEALTH) * ctx.modifiers().get(SpellModifiers.POTENCY));
            return true;
        }

        return super.applyEffects(ctx, caster);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.ADVANCED, Elements.HEALING, SpellType.DEFENCE, SpellAction.POINT_UP, 15, 10, 40)
                .add(DefaultProperties.HEALTH, 8F).build();
    }
}
