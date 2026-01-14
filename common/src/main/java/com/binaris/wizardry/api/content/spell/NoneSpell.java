package com.binaris.wizardry.api.content.spell;

import com.binaris.wizardry.api.content.spell.internal.PlayerCastContext;
import com.binaris.wizardry.api.content.spell.properties.SpellProperties;
import com.binaris.wizardry.setup.registries.Elements;
import com.binaris.wizardry.setup.registries.SpellTiers;
import org.jetbrains.annotations.NotNull;

/**
 * This is an empty spell used for whenever a non-null empty spell is needed; it's great for
 * using as a template when making your own spell.
 */
// You should not be inheriting none spell this can cause big breaks
public final class NoneSpell extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        return false;
    }

    @Override
    public boolean requiresPacket() {
        return false;
    }


    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.builder().assignBaseProperties(SpellTiers.NOVICE, Elements.MAGIC, SpellType.UTILITY, SpellAction.NONE, 0, 0, 0)
                .build();
    }
}
