package com.electroblob.wizardry;

import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.api.content.spell.Spell;
import com.electroblob.wizardry.api.content.spell.SpellTier;
import com.electroblob.wizardry.core.registry.EBRegistries;
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

    private EBRegistriesFabric() {}
}
