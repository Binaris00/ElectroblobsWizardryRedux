package com.electroblob.wizardry.content.spell.abstr;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.PlayerCastContext;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperties;
import org.jetbrains.annotations.NotNull;

// TODO NEED TO MAKE CONJURATION SPELL
public class ConjurationSpell extends Spell {
    @Override
    public boolean cast(PlayerCastContext ctx) {
        return false;
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return null;
    }
}
