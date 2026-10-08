package com.binaris.wizardry.client;

import com.binaris.wizardry.client.gui.screens.handbook.HandBookScreen;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

public class ResourceManagerListener implements ResourceManagerReloadListener {
    public static final ResourceManagerListener INSTANCE = new ResourceManagerListener();

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        HandBookScreen.loadHandbookFile(resourceManager);
    }
}
