package com.electroblob.wizardry.api.common.spell;

/**
 * This is an empty spell used for whenever a non-null empty spell is needed; it's great for
 * using as a template when making your own spell.
 */
// You should not be inheriting none spell this can cause big breaks
public final class NoneSpell extends Spell {

    @Override
    protected void perform(Caster caster) {}

    @Override
    protected SpellProperties properties() {
        return SpellProperties.empty();
    }
}
