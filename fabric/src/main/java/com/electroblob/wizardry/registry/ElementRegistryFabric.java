package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.content.spell.Element;
import com.electroblob.wizardry.core.registry.ElementRegistry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class ElementRegistryFabric {
    private static final MappedRegistry<Element> ELEMENTS = FabricRegistryBuilder.createSimple(ElementRegistry.key())
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    private ElementRegistryFabric() {}

    public static MappedRegistry<Element> get() {
        return ELEMENTS;
    }

    public static void register() {
        ElementRegistry.initEntryGetter(() -> get().entrySet());
        EBRegister.registerElements((elements) -> {
            // TODO: ebwizardry modid?
            elements.forEach((entry) -> Registry.register(ElementRegistryFabric.get(),
                    new ResourceLocation(WizardryMainMod.MOD_ID, entry.getKey()), entry.getValue()));
        });
    }
}
