package com.binaris.wizardry.core.platform;

import com.binaris.wizardry.core.EBLogger;
import com.binaris.wizardry.core.platform.services.INetworkHelper;
import com.binaris.wizardry.core.platform.services.IObjectData;
import com.binaris.wizardry.core.platform.services.IPlatformHelper;
import com.binaris.wizardry.core.platform.services.IRegistryUtil;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IObjectData OBJECT_DATA = load(IObjectData.class);
    public static final INetworkHelper NETWORK_HELPER = load(INetworkHelper.class);
    public static final IRegistryUtil REGISTRY_UTIL = load(IRegistryUtil.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        EBLogger.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}