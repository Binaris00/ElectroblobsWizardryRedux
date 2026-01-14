package com.binaris.wizardry.registry;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.api.content.spell.Element;
import com.binaris.wizardry.api.content.spell.Spell;
import com.binaris.wizardry.api.content.spell.SpellTier;
import com.binaris.wizardry.core.registry.EBRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public final class EBRegistriesForge {
    private static final DeferredRegister<Element> ELEMENTS = DeferredRegister.create(EBRegistries.ELEMENT, WizardryMainMod.MOD_ID);
    public static final Supplier<IForgeRegistry<Element>> ELEMENT = ELEMENTS.makeRegistry(() -> new RegistryBuilder<Element>().disableSaving().disableOverrides());

    private static final DeferredRegister<Spell> SPELLS = DeferredRegister.create(EBRegistries.SPELL, WizardryMainMod.MOD_ID);
    public static final Supplier<IForgeRegistry<Spell>> SPELL = SPELLS.makeRegistry(() -> new RegistryBuilder<Spell>().disableSaving().disableOverrides());

    private static final DeferredRegister<SpellTier> TIERS = DeferredRegister.create(EBRegistries.TIER, WizardryMainMod.MOD_ID);
    public static final Supplier<IForgeRegistry<SpellTier>> TIER = TIERS.makeRegistry(() -> new RegistryBuilder<SpellTier>().disableSaving().disableOverrides());

    private EBRegistriesForge() {
    }

    public static void elements(IEventBus modBus) {
        ELEMENTS.register(modBus);
    }

    public static void spells(IEventBus modBus) {
        SPELLS.register(modBus);
    }

    public static void tiers(IEventBus modBus) {
        TIERS.register(modBus);
    }
}
