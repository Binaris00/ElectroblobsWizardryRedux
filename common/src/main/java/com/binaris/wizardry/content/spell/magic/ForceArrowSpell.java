package com.binaris.wizardry.content.spell.magic;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellTypes;
import com.binaris.wizardry.api.content.spell.internal.CastContext;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.entity.projectile.ForceArrow;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.ArrowSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import org.jetbrains.annotations.NotNull;

public class ForceArrowSpell extends ArrowSpell<ForceArrow> {
    public ForceArrowSpell() {
        super(ForceArrow::new);
    }

    @Override
    protected void addArrowExtras(CastContext ctx, ForceArrow arrow) {
        arrow.setMana((int) (ctx.modifiers().get(SpellModifiers.COST, this.getCost())));
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.SORCERY, SpellTypes.PROJECTILE, SpellAction.POINT, 15, 0, 20)
                .add(DefaultProperties.RANGE, 20f)
                .add(DefaultProperties.DAMAGE, 7f)
                .build();
    }
}
