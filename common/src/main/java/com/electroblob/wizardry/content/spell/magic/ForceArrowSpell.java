package com.electroblob.wizardry.content.spell.magic;

import com.electroblob.wizardry.api.content.spell.SpellAction;
import com.electroblob.wizardry.api.content.spell.SpellType;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.content.entity.projectile.ForceArrow;
import com.electroblob.wizardry.content.spell.DefaultProperties;
import com.electroblob.wizardry.content.spell.abstr.ArrowSpell;
import com.electroblob.wizardry.setup.registries.Elements;
import com.electroblob.wizardry.setup.registries.Tiers;
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
                .assignBaseProperties(Tiers.APPRENTICE, Elements.SORCERY, SpellType.PROJECTILE, SpellAction.POINT, 15, 0, 20)
                .add(DefaultProperties.RANGE, 20f)
                .add(DefaultProperties.DAMAGE, 7f)
                .build();
    }
}
