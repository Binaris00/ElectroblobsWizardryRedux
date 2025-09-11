package com.electroblob.wizardry.datagen.provider;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.setup.registries.Spells;
import net.minecraft.data.PackOutput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EBSpellsProvider extends SpellDataProvider {
    public EBSpellsProvider(PackOutput output) {
        super(output, WizardryMainMod.MOD_ID);
    }

    @Override
    protected void buildSpells(@NotNull Consumer<Spell> consumer) {
        // We aren't adding custom spells made by addon devs here
        Spells.SPELLS.values().forEach(consumer);
    }
}