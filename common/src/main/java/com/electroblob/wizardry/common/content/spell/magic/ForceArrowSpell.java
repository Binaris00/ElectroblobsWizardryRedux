package com.electroblob.wizardry.common.content.spell.magic;

import com.electroblob.wizardry.api.common.spell.internal.Caster;
import com.electroblob.wizardry.api.common.spell.properties.SpellProperties;
import com.electroblob.wizardry.common.content.spell.DefaultProperties;
import com.electroblob.wizardry.common.content.spell.abstr.ArrowSpell;
import com.electroblob.wizardry.common.content.entity.projectile.ForceArrow;
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
