package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.common.spell.Tier;
import com.electroblob.wizardry.common.core.TierRegistry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public final class TierRegistryForge {
    private static boolean tiers_registered;

    private static final DeferredRegister<Tier> REGISTER = DeferredRegister.create(TierRegistry.key(), WizardryMainMod.MOD_ID);
    private static final Supplier<IForgeRegistry<Tier>> REGISTRY = REGISTER.makeRegistry(() ->
            new RegistryBuilder<Tier>().disableSaving().disableOverrides());

    public static void initialize(IEventBus modBus) {
        REGISTER.register(modBus);
        TierRegistry.initEntryGetter(() -> get().getEntries());
    }

    public static void register() {
        if(!tiers_registered) {
            EBRegister.registerTiers((tiers) ->
                    tiers.forEach((entry) -> REGISTER.register(entry.getKey(), entry::getValue)));
            tiers_registered = true;
        }
    }

    public static IForgeRegistry<Tier> get() {
        return REGISTRY.get();
    }

    private TierRegistryForge() {}
}
