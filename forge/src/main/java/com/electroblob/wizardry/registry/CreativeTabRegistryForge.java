package com.electroblob.wizardry.registry;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.setup.registries.EBRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;

public final class CreativeTabRegistryForge {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WizardryMainMod.MOD_ID);

    public static void register() {
        EBRegister.registerCreativeTabs((creativeTabCollection) -> {
            creativeTabCollection.forEach(CREATIVE_MODE_TABS::register);
        });
    }

    private CreativeTabRegistryForge() {}
}
