package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ItemRegistryFabric {

    public static void register() {
        EBRegister.registerItems((itemCollection) -> {
            itemCollection.forEach((itemEntry) -> item(itemEntry.getKey(), itemEntry.getValue().get()));
        });
    }

    private static Item item(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(WizardryMainMod.MOD_ID, name), item);
    }

    private ItemRegistryFabric() {}
}
