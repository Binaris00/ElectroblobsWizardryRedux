package com.electroblob.wizardry.api.content.entity.living;

import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.internal.SpellModifiers;
import com.electroblob.wizardry.api.content.util.EntityUtil;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ISpellCaster {
    @NotNull
    List<Spell> getSpells();

    @NotNull
    default SpellModifiers getModifiers() {
        return new SpellModifiers();
    }

    @NotNull
    default Spell getContinuousSpell() {
        return Spells.NONE;
    }

    default void setContinuousSpell(Spell spell) {

    }

    default int getSpellCounter() {
        return 0;
    }

    default void setSpellCounter(int count) {

    }

    default int getAimingError(Difficulty difficulty) {
        return EntityUtil.getDefaultAimingError(difficulty);
    }
}
