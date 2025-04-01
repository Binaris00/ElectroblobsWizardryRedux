package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.common.spell.Tier;
import com.electroblob.wizardry.common.core.TierRegistry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class TierRegistryFabric {
    private static final MappedRegistry<Tier> TIERS = FabricRegistryBuilder.createSimple(TierRegistry.key())
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    private TierRegistryFabric() {}

    public static MappedRegistry<Tier> get() {
        return TIERS;
    }

    public static void register() {
        TierRegistry.initEntryGetter(() -> get().entrySet());
        EBRegister.registerTiers((tiers) -> {
            // TODO: ebwizardry modid?
            tiers.forEach((entry) -> Registry.register(TierRegistryFabric.get(),
                    new ResourceLocation(WizardryMainMod.MOD_ID, entry.getKey()), entry.getValue()));
        });
    }
}
