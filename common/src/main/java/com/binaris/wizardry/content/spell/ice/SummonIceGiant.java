package com.binaris.wizardry.content.spell.ice;

import com.binaris.wizardry.api.content.spell.SpellAction;
import com.binaris.wizardry.api.content.spell.SpellType;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.content.entity.living.IceGiant;
import com.binaris.wizardry.content.spell.DefaultProperties;
import com.binaris.wizardry.content.spell.abstr.MinionSpell;
import com.binaris.wizardry.setup.registries.EBEntities;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummonIceGiant extends MinionSpell<IceGiant> {

    public SummonIceGiant() {
        super((level) -> new IceGiant(EBEntities.ICE_GIANT.get(), level));
        this.soundValues(1, 0.15f, 0.1f);
    }

    @Override
    protected IceGiant createMinion(Level world, @Nullable LivingEntity caster, SpellModifiers modifiers) {
        return super.createMinion(world, caster, modifiers);
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder()
                .assignBaseProperties(SpellTiers.MASTER, Elements.ICE, SpellType.MINION, SpellAction.SUMMON, 100, 20, 400)
                .add(DefaultProperties.MINION_COUNT, 1)
                .add(DefaultProperties.MINION_LIFETIME, 600)
                .add(DefaultProperties.SUMMON_RADIUS, 2)
                .add(DefaultProperties.SENSIBLE, true) // TODO: sadly we need to change the texture
                .build();
    }
}