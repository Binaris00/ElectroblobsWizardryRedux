package com.electroblob.wizardry.fabric.registry;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class EntityRegistryFabric {

    public static void register() {
        EBRegister.registerEntityTypes((entityTypeCollection)
                -> entityTypeCollection.forEach((name, entityType) ->
                        Registry.register(BuiltInRegistries.ENTITY_TYPE, Wizardry.location(name), entityType.get())
                )
        );
    }

    private EntityRegistryFabric() {}
}
