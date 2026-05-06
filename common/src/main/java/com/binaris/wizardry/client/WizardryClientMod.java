package com.binaris.wizardry.client;

import com.binaris.wizardry.core.config.ConfigManager;
import com.binaris.wizardry.core.config.EBClientConfig;

public final class WizardryClientMod {

    public static void init() {
        ConfigManager.register(EBClientConfig.INSTANCE);
    }

    private WizardryClientMod() {
    }
}
