package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class EntityRegistryFabric {

    public static void register() {
        EBRegister.registerEntityTypes((entityTypeCollection)
                -> entityTypeCollection.forEach((name, entityType) ->
                        Registry.register(BuiltInRegistries.ENTITY_TYPE, WizardryMainMod.location(name), entityType.get())
                )
        );
    }

    private EntityRegistryFabric() {}
}
