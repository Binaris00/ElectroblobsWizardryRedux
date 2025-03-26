package com.electroblob.wizardry.setup;

import com.electroblob.wizardry.WizardryMainMod;
import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.api.common.spell.SpellProperty;
import com.electroblob.wizardry.api.common.spell.SpellRegistry;
import com.electroblob.wizardry.setup.registries.EBDamageSources;
import com.electroblob.wizardry.setup.registries.EBSounds;
import net.minecraft.network.chat.Component;

public final class CommonSetup {

    public static void init() {
        SpellProperty.load();
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
