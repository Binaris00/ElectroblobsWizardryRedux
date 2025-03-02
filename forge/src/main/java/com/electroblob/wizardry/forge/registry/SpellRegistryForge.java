package com.electroblob.wizardry.forge.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.api.common.spell.SpellRegistry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public final class SpellRegistryForge {

    private static boolean spells_registered;

    private static final DeferredRegister<Spell> REGISTER = DeferredRegister.create(SpellRegistry.key(), WizardryMainMod.MOD_ID);
    private static final Supplier<IForgeRegistry<Spell>> REGISTRY = REGISTER.makeRegistry(() -> new RegistryBuilder<Spell>().disableSaving().disableOverrides());

    public static void initialize(IEventBus modBus) {
        REGISTER.register(modBus);
        SpellRegistry.initEntryGetter(() -> get().getEntries());
    }

    public static void register() {
        if(!spells_registered) {
            EBRegister.registerSpells((spellCollection) -> {
                spellCollection.forEach((spellEntry) -> {
                    REGISTER.register(spellEntry.getKey(), spellEntry::getValue);
                });
            });
            spells_registered = true;
        }
    }

    public static IForgeRegistry<Spell> get() {
        return REGISTRY.get();
    }

    private SpellRegistryForge() {}
}
