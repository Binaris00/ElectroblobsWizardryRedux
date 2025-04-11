package com.electroblob.wizardry.setup;

import com.electroblob.wizardry.EBEventHelper;
import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.content.spell.properties.SpellProperty;
import com.electroblob.wizardry.core.registry.SpellRegistry;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import net.minecraft.network.chat.Component;

public final class CommonSetup {

    public static void init() {
        SpellProperty.load();
        EBEventHelper.register();
        //EBNetwork.bootstrap();
    }

    public static void setup() {
        informStart();
        SpellRegistry.initializeSpellLocations();
        EBDamageSources.init();
    }


    private static void informStart() {
        EBLogger.info(Component.translatable("logger.info.ebwizardry.started"));
        EBLogger.useIfEnabled(logger ->
                logger.info("{} {}", Component.translatable("logger.info.ebwizardry.running_on_version").getString(), WizardryMainMod.MOD_VERSION)
        );
    }

    private CommonSetup() {}
}
