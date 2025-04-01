package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.common.spell.Element;
import com.electroblob.wizardry.common.core.ElementRegistry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public final class ElementRegistryForge {
    private static boolean elements_registered;

    private static final DeferredRegister<Element> REGISTER = DeferredRegister.create(ElementRegistry.key(), WizardryMainMod.MOD_ID);
    private static final Supplier<IForgeRegistry<Element>> REGISTRY = REGISTER.makeRegistry(() ->
            new RegistryBuilder<Element>().disableSaving().disableOverrides());

    public static void initialize(IEventBus modBus) {
        REGISTER.register(modBus);
        ElementRegistry.initEntryGetter(() -> get().getEntries());
    }

    public static void register() {
        if(!elements_registered) {
            EBRegister.registerElements((elements) ->
                    elements.forEach((entry) -> REGISTER.register(entry.getKey(), entry::getValue)));
            elements_registered = true;
        }
    }

    public static IForgeRegistry<Element> get() {
        return REGISTRY.get();
    }

    private ElementRegistryForge() {}
}
