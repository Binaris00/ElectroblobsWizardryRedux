package com.binaris.wizardry.content.item.artifact;

import com.binaris.wizardry.api.content.event.SpellCastEvent;
import com.binaris.wizardry.api.content.spell.internal.SpellModifiers;
import com.binaris.wizardry.core.ArtifactEffectContext;
import com.binaris.wizardry.core.IArtifactEffect;
import com.binaris.wizardry.setup.registries.Elements;

public class FullMoonRingEffect implements IArtifactEffect {
    @Override
    public void onSpellPreCast(SpellCastEvent.Pre e, ArtifactEffectContext context) {
        if (e.getSpell().getElement() == Elements.EARTH && !e.getCaster().level().isDay() && e.getCaster().level().getMoonPhase() == 0) {
            e.getModifiers().multiply(SpellModifiers.COOLDOWN, 0.3f);
        }
    }
}
