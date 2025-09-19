package com.electroblob.wizardry.core;

import com.electroblob.wizardry.WizardryMainMod;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;

public class PropertiesFabricDataManager extends PropertiesDataManager implements IdentifiableResourceReloadListener {
    public static final ResourceLocation ID = WizardryMainMod.location("spell_properties");

    public PropertiesFabricDataManager() {
        super();
        INSTANCE = this;
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public Collection<ResourceLocation> getFabricDependencies() {
        return List.of(ResourceReloadListenerKeys.TAGS);
    }
}
