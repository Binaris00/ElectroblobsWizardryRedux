package com.electroblob.wizardry.forge.registry;

import com.electroblob.wizardry.Wizardry;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ItemRegistryForge {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Wizardry.MOD_ID);

    public static void register() {
        EBRegister.registerItems((itemCollection) -> {
            itemCollection.forEach((itemEntry) -> {
                registerItem(itemEntry.getKey(), itemEntry.getValue());
            });
        });
    }

    private static RegistryObject<Item> registerItem(String name, Supplier<? extends Item> item) {
        return ITEMS.register(name, item);
    }



    private ItemRegistryForge() {}
}
