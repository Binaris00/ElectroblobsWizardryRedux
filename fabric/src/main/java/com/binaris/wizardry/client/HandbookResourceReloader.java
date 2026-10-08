package com.binaris.wizardry.client;

import com.binaris.wizardry.WizardryMainMod;
import com.binaris.wizardry.client.gui.screens.handbook.HandBookScreen;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

public class HandbookResourceReloader implements SimpleSynchronousResourceReloadListener {
    public static final HandbookResourceReloader INSTANCE = new HandbookResourceReloader();

    @Override
    public ResourceLocation getFabricId() {
        return WizardryMainMod.location("handbook_reloader");
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        HandBookScreen.loadHandbookFile(resourceManager);
    }
}
