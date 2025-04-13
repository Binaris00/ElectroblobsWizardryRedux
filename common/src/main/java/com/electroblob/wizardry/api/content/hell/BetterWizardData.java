package com.electroblob.wizardry.api.content.hell;

import com.electroblob.wizardry.api.content.spell.NoneSpell;
import com.electroblob.wizardry.api.content.spell.Spell;

import java.util.HashSet;

public class BetterWizardData {
    BinWizardDataInternal dataInternal;

    public BetterWizardData(BinWizardDataInternal internal){
        this.dataInternal = internal;
    }

    public boolean hasSpellBeenDiscovered(Spell spell){
        return dataInternal.spellsDiscovered.contains(spell) || spell instanceof NoneSpell;
    }

    public boolean discoverSpell(Spell spell){
        if(dataInternal.spellsDiscovered == null){
            dataInternal.spellsDiscovered = new HashSet<>();
        }
        if(spell instanceof NoneSpell) return false;
        // Tries to add the spell to the list of discovered spells, and returns false if it was already present
        return dataInternal.spellsDiscovered.add(spell);
    }
}
