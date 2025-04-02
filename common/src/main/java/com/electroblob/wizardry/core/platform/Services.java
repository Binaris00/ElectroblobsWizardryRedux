package com.electroblob.wizardry.core.platform;

import com.electroblob.wizardry.api.EBLogger;
import com.electroblob.wizardry.core.platform.services.IPlatformHelper;
import net.minecraft.network.chat.Component;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        EBLogger.debug(Component.literal("Loaded {} for service {}".formatted(loadedService, clazz)));
        return loadedService;
    }
}