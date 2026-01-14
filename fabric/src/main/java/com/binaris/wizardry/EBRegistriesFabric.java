package com.binaris.wizardry;

import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.core.registry.EBRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;

public final class EBRegistriesFabric {
    public static final MappedRegistry<Element> ELEMENTS = FabricRegistryBuilder.createSimple(EBRegistries.ELEMENT)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final MappedRegistry<SpellTier> TIERS = FabricRegistryBuilder.createSimple(EBRegistries.TIER)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static final MappedRegistry<Spell> SPELLS = FabricRegistryBuilder.createSimple(EBRegistries.SPELL)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    private EBRegistriesFabric() {
    }
}
