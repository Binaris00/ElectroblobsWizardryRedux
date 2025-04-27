package com.electroblob.wizardry.api.content.spell;

import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import com.electroblob.wizardry.setup.registries.Elements;
import org.jetbrains.annotations.NotNull;

/**
 * This is an empty spell used for whenever a non-null empty spell is needed; it's great for
 * using as a template when making your own spell.
 */
// You should not be inheriting none spell this can cause big breaks
public final class NoneSpell extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        return true;
    }

    @Override
    public Element getElement() {
        return Elements.MAGIC;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
