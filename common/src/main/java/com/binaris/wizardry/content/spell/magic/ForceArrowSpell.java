package com.binaris.wizardry.content.spell.magic;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.entity.projectile.ForceArrow;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.ArrowSpell;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForceArrowSpell extends ArrowSpell<ForceArrow> {
    public ForceArrowSpell() {
        super(ForceArrow::new);
    }

    @Override
    protected void addArrowExtras(ForceArrow arrow, @Nullable LivingEntity caster) {
        //arrow.setMana((int) (this.getCost() * this.getCostScale()));
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.APPRENTICE, Elements.SORCERY, SpellType.PROJECTILE, SpellAction.POINT, 15, 0, 20)
                .add(DefaultProperties.RANGE, 20f)
                .add(DefaultProperties.DAMAGE, 7f)
                .build();
    }
}
