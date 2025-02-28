package com.electroblob.wizardry.fabric.registry;

import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class CreativeTabRegistryFabric {

    public static void register() {
        EBRegister.registerCreativeTabs((creativeTabs) -> creativeTabs.forEach((name, tab) -> Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, name, tab.get())));
    }

    private CreativeTabRegistryFabric() {}
}
