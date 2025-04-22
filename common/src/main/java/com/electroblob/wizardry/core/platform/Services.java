package com.electroblob.wizardry.core.platform;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.core.platform.services.INetworkHelper;
import com.electroblob.wizardry.core.platform.services.IPlatformHelper;
import com.electroblob.wizardry.core.platform.services.IWizardPlayerData;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IWizardPlayerData WIZARD_DATA = load(IWizardPlayerData.class);
    public static final INetworkHelper NETWORK_HELPER = load(INetworkHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        EBLogger.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}