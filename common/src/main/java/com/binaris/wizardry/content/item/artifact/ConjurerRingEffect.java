package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.content.spell.abstr.ConjureItemSpell;
import com.binaris.wizardry.core.IArtifactEffect;
import net.minecraft.world.item.ItemStack;

public class ConjurerRingEffect implements IArtifactEffect {
    @Override
    public void onSpellPreCast(SpellCastEvent.Pre e, ItemStack artifact) {
        if (e.getSpell() instanceof ConjureItemSpell) {
            e.getModifiers().multiply(SpellModifiers.DURATION, 2);
        }
    }
}
