package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.common.spell.Spell;
import com.electroblob.wizardry.common.core.SpellRegistry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class SpellRegistryFabric {
    private SpellRegistryFabric() {}

    private static final MappedRegistry<Spell> SPELL_REGISTRY = FabricRegistryBuilder.createSimple(SpellRegistry.key())
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static MappedRegistry<Spell> get() {
        return SPELL_REGISTRY;
    }

    public static void register() {
        SpellRegistry.initEntryGetter(() -> get().entrySet());
        EBRegister.registerSpells((spellCollection) -> {
            // TODO: ebwizardry modid?
            spellCollection.forEach((spellEntry) -> Registry.register(SpellRegistryFabric.get(),
                    new ResourceLocation(WizardryMainMod.MOD_ID, spellEntry.getKey()), spellEntry.getValue()));
        });
        SpellRegistry.initializeSpellLocations();
    }
}
