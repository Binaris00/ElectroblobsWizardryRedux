package com.electroblob.wizardry.content.spell.magic;

import com.electroblob.wizardry.api.content.spell.internal.Caster;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ArrowSpell;
import com.electroblob.wizardry.content.entity.projectile.ForceArrow;
import org.jetbrains.annotations.Nullable;

public class ForceArrowSpell extends ArrowSpell<ForceArrow> {
    public ForceArrowSpell() {
        super(ForceArrow::new);
    }

    @Override
    protected void addArrowExtras(ForceArrow arrow, @Nullable Caster caster) {
        //arrow.setMana((int) (this.getCost() * this.getCostScale()));
    }

    @Override
    protected SpellProperties properties() {
        return SpellProperties.builder()
                .add(DefaultProperties.RANGE, 20f)
                .add(DefaultProperties.DAMAGE, 7f)
                .build();
    }
}
