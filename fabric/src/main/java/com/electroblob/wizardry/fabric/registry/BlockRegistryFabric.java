package com.electroblob.wizardry.fabric.registry;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class BlockRegistryFabric {

    public static void register() {
        EBRegister.registerBlocks((blockCollection) ->
                blockCollection.forEach(entry ->
                        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Wizardry.MOD_ID, entry.getKey()), entry.getValue().get())
                )
        );
    }

    private BlockRegistryFabric() {}
}
